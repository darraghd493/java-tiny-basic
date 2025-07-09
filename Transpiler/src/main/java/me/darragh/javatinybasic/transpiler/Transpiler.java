package me.darragh.javatinybasic.transpiler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.darragh.javatinybasic.ast.Token;
import me.darragh.javatinybasic.ast.expression.*;
import me.darragh.javatinybasic.ast.expression.statement.FORExpression;
import me.darragh.javatinybasic.ast.expression.statement.IFExpression;
import me.darragh.javatinybasic.ast.expression.statement.LETExpression;
import me.darragh.javatinybasic.ast.expression.statement.PRINTExpression;
import me.darragh.javatinybasic.ast.langauge.LArithmeticOperator;
import me.darragh.javatinybasic.ast.langauge.LStatement;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

/**
 * Transpiles the Tiny BASIC AST into a Java .JAR file, composed of Java Bytecode.
 * <br/>
 * <h2>Useful notes:</h2>
 * <ul>
 *  <li>All variables stored will be of {@link Integer} type.</li>
 *  <li>GOTO uses goto, an internal JVM bytecode for bytecode.</li>
 *  <li>GOSUB acts as a subroutine, and therefore would be embedded as methods: {generated name from line number}#()V.</li>
 * </ul>
 *
 * @author darraghd493
 * @since 1.0.0
 */
// TODO: Improve code quality and generation
@Data
@RequiredArgsConstructor
public class Transpiler {
    private static final String CLASS_NAME = "Transpiled_BASIC_Main";
    private static final String SCANNER_FIELD_NAME = "scanner";

    //region Cache
    private final Map<Integer, Token> tokens;
    private final List<Integer> lineNumbers;
    //endregion

    //region Class Data
    private final List<String> variableNames = new ArrayList<>(),
            subroutineNames = new ArrayList<>();
    private final Map<Integer, LabelNode> labelNodes = new LinkedHashMap<>();
    private final List<LabelPlacement> needLabelNodes = new ArrayList<>();
    private final Deque<LoopContext> loopStack = new ArrayDeque<>();
    private boolean usesScanner;

    private ClassNode classNode;
    private boolean generated;
    //endregion

    //region Constructor
    public Transpiler(@NotNull List<Token> tokens) {
        // Cache the tokens and line numbers for quick access
        this.tokens = tokens.stream()
                .sorted(Comparator.comparing(Token::lineNumber))
                .collect(Collectors.toMap(
                        Token::lineNumber,
                        token -> token,
                        (a, b) -> a,
                        LinkedHashMap::new));
        this.lineNumbers = new ArrayList<>(this.tokens.keySet());
        this.generated = false;
    }
    //endregion

    /**
     * Generates the Java Bytecode for the Tiny BASIC program.
     */
    public void generate() {
        if (this.generated) {
            return;
        }

        ClassNode classNode = new ClassNode();
        classNode.name = CLASS_NAME;
        classNode.access = ACC_PUBLIC;
        classNode.version = Opcodes.V1_8; // Java 8
        classNode.superName = "java/lang/Object";
        this.classNode = classNode;

        // Generate the main method
        MethodNode mainMethodNode = new MethodNode( // public static void main(String[]);
                ACC_PUBLIC | ACC_STATIC, // public static
                "main", // main
                "([Ljava/lang/String;)V", // String[] in, void out
                null,
                null
        );
        this.generateMethodInstructions(mainMethodNode, this.tokens.values().stream().toList());
        this.classNode.methods.add(mainMethodNode);

        // Generate the <cinit> method
        MethodNode initMethodNode = new MethodNode(
                ACC_PUBLIC | ACC_STATIC, // public
                "<clinit>", // constructor
                "()V", // void out
                null,
                null
        );
        InsnList initMethodInstructions = new InsnList();
        boolean initMethodUsed = false;

        if (this.usesScanner) {
            initMethodUsed = true;

            // Create the Scanner field
            FieldNode fieldNode = new FieldNode(
                    ACC_PRIVATE | ACC_STATIC | ACC_FINAL,
                    SCANNER_FIELD_NAME,
                    "Ljava/util/Scanner;",
                    null,
                    null
            );
            this.classNode.fields.add(fieldNode);

            // Create the Scanner instance
            initMethodInstructions.add(new TypeInsnNode(NEW, "java/util/Scanner"));
            initMethodInstructions.add(new InsnNode(DUP));
            initMethodInstructions.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;"));
            initMethodInstructions.add(new MethodInsnNode(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false));
            initMethodInstructions.add(new FieldInsnNode(PUTSTATIC, CLASS_NAME, SCANNER_FIELD_NAME, "Ljava/util/Scanner;"));
        }

        if (initMethodUsed) {
            initMethodInstructions.add(new InsnNode(RETURN));
            initMethodNode.instructions = initMethodInstructions;
            this.classNode.methods.add(initMethodNode);
        }

        // Update label references
        this.needLabelNodes.forEach(placement ->
                placement.jumpInsnNode.label = placement.targetLabel != null ? placement.targetLabel : this.labelNodes.get(placement.lineNumber));
    }

    /**
     * Exports the generated Java Bytecode to a JAR file at the specified path.
     *
     * @param path The path where the JAR file will be created.
     */
    public void toFile(@NotNull Path path) {
        if (!this.generated) {
            this.generate();
        }

        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete existing file at " + path, e);
            }
        }

        try (JarOutputStream jarOutputStream = new JarOutputStream(Files.newOutputStream(path))) {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            this.classNode.accept(classWriter);
            jarOutputStream.putNextEntry(new JarEntry(CLASS_NAME + ".class"));
            jarOutputStream.write(classWriter.toByteArray());
            jarOutputStream.closeEntry();
            jarOutputStream.flush();
            jarOutputStream.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            jarOutputStream.write("Manifest-Version: 1.0\n".getBytes());
            jarOutputStream.write("Main-Class: %s\n".formatted(CLASS_NAME).getBytes());
            jarOutputStream.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write class file to " + path, e);
        }
    }

    //region Method Instructions Generation
    private void generateMethodInstructions(@NotNull MethodNode methodNode, @NotNull List<Token> tokens) {
        InsnList list = new InsnList();

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            System.out.printf("%s\t%s%n", token.lineNumber(), token.statement()); // TODO: Remove in production

            LabelNode labelNode = new LabelNode(new Label());
            LineNumberNode lineNumberNode = new LineNumberNode(token.lineNumber(), labelNode);

            this.labelNodes.put(token.lineNumber(), labelNode);

            InsnList labelList = new InsnList();

            switch (token.statement()) {
                case LET -> this.generateLetInstructions(token, labelList);
                case PRINT -> this.generatePrintInstructions(token, labelList);
                case INPUT -> this.generateInputInstructions(token, labelList);
                case IF -> this.generateIfInstructions(token, labelList);
                case FOR -> this.generateForInstructions(token, labelList);
                case NEXT -> this.generateNextInstructions(labelList);
                case GOTO -> this.generateGotoInstructions(token, labelList);
                case GOSUB -> this.generateGosubInstructions(tokens, token, labelList);
                case RETURN -> this.generateReturnInstructions(labelList);
                case END -> this.generateEndInstructions(labelList);
                default -> {}
            }

            list.add(labelNode);
            list.add(lineNumberNode);
            list.add(labelList);
        }

        list.add(new InsnNode(RETURN));
        methodNode.instructions = list;
    }

    private void generateLetInstructions(@NotNull Token token, @NotNull InsnList list) {
        LETExpression letExpression = (LETExpression) token.expression();
        assert letExpression != null : "LET expression should not be null";

        String variableName = letExpression.getVariableName();
        this.generateVariable(variableName);

        list.add(this.generateValueExpression(letExpression.getValue()));
        list.add(new FieldInsnNode(PUTSTATIC, CLASS_NAME, variableName, "I"));
    }

    private void generatePrintInstructions(@NotNull Token token, @NotNull InsnList list) {
        PRINTExpression printExpression = (PRINTExpression) token.expression();
        assert printExpression != null : "PRINT expression should not be null";

        if (printExpression.getValues().length > 1) {
            // Create the string builder
            list.add(new TypeInsnNode(NEW, "java/lang/StringBuilder"));
            list.add(new InsnNode(DUP));
            list.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V"));
            list.add(new VarInsnNode(ASTORE, 1));

            // Append each value to the StringBuilder
            for (Expression expression : printExpression.getValues()) {
                list.add(new VarInsnNode(ALOAD, 1));
                if (expression instanceof ValueExpression valueExpression) {
                    list.add(this.generateValueExpression(valueExpression));
                    list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false));
                    list.add(new LdcInsnNode(" "));
                    list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
                } else if (expression instanceof StringValueExpression stringValueExpression) {
                    list.add(new LdcInsnNode(stringValueExpression.getValue() + " "));
                    list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
                } else {
                    throw new IllegalStateException("Unsupported expression type: " + expression);
                }
                list.add(new InsnNode(POP));
            }

            // TODO: Remove the last space added by the loop
//            list.add(new VarInsnNode(ALOAD, 1));
//            list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "length", "()I", false));
//            list.add(new InsnNode(ICONST_1));
//            list.add(new InsnNode(ISUB));
//            list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "setLength", "(I)V", false));

            // Print the StringBuilder content
            list.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
            list.add(new VarInsnNode(ALOAD, 1));
            list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false));
            list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        } else {
            Expression expression = printExpression.getValues()[0];
            if (expression instanceof ValueExpression) {
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                list.add(this.generateValueExpression((ValueExpression) expression));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false));
            } else if (expression instanceof StringValueExpression) {
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                list.add(new LdcInsnNode(((StringValueExpression) expression).getValue()));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
            } else {
                throw new IllegalStateException("Unsupported expression type: " + expression);
            }
        }
    }

    private void generateInputInstructions(@NotNull Token token, @NotNull InsnList list) {
        this.usesScanner = true; // We will use Scanner for input

        VariableNameExpression variableNameExpression = (VariableNameExpression) token.expression();
        assert variableNameExpression != null : "Variable name expression should not be null";

        String variableName = variableNameExpression.getVariableName();
        this.generateVariable(variableName);

        list.add(new FieldInsnNode(GETSTATIC, CLASS_NAME, SCANNER_FIELD_NAME, "Ljava/util/Scanner;"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false));
        list.add(new FieldInsnNode(PUTSTATIC, CLASS_NAME, variableName, "I"));
    }

    private void generateIfInstructions(@NotNull Token token, @NotNull InsnList list) { // TODO: Verify
        IFExpression ifExpression = (IFExpression) token.expression();
        assert ifExpression != null : "If expression should not be null";

        // Load the values to compare
        list.add(this.generateValueExpression(ifExpression.getValueA()));
        list.add(this.generateValueExpression(ifExpression.getValueB()));

        // Create conditional jump
        JumpInsnNode jumpIfTrue;
        switch (ifExpression.getRelationalOperator()) {
            case EQUAL -> jumpIfTrue = new JumpInsnNode(IF_ICMPEQ, null);
            case NOT_EQUAL -> jumpIfTrue = new JumpInsnNode(IF_ICMPNE, null);
            case LESS_THAN -> jumpIfTrue = new JumpInsnNode(IF_ICMPLT, null);
            case GREATER_THAN -> jumpIfTrue = new JumpInsnNode(IF_ICMPGT, null);
            case LESS_THAN_OR_EQUAL -> jumpIfTrue = new JumpInsnNode(IF_ICMPLE, null);
            case GREATER_THAN_OR_EQUAL -> jumpIfTrue = new JumpInsnNode(IF_ICMPGE, null);
            default -> throw new IllegalStateException("Unexpected relational operator: " + ifExpression.getRelationalOperator());
        }

        this.needLabelNodes.add(new LabelPlacement(
                ifExpression.getLineNumberToGoto(), jumpIfTrue
        ));
        list.add(jumpIfTrue);
    }

    private void generateForInstructions(@NotNull Token token, @NotNull InsnList list) {
        FORExpression forExpression = (FORExpression)token.expression();
        assert forExpression != null;

        String variableName = forExpression.getVariableName();
        this.generateVariable(variableName);

        // init: var = start
        list.add(this.generateValueExpression(forExpression.getStartValue()));
        list.add(new FieldInsnNode(PUTSTATIC, CLASS_NAME, variableName, "I"));

        // labels & push
        LabelNode start = new LabelNode(new Label());
        LabelNode end   = new LabelNode(new Label());
        loopStack.push(new LoopContext(variableName, end, start, forExpression.getStepValue()));

        list.add(start);
        // if var > end goto end
        list.add(new FieldInsnNode(GETSTATIC, CLASS_NAME, variableName, "I"));
        list.add(this.generateValueExpression(forExpression.getEndValue()));
        JumpInsnNode exit = new JumpInsnNode(IF_ICMPGT, null);
        list.add(exit);
        needLabelNodes.add(new LabelPlacement(-1, exit, end));
    }

    private void generateNextInstructions(@NotNull InsnList list) {
        LoopContext ctx = loopStack.pop();
        String var = ctx.variableName;
        this.generateVariable(var);

        // var += step
        list.add(new FieldInsnNode(GETSTATIC, CLASS_NAME, var, "I"));
        list.add(this.generateValueExpression(ctx.step));
        list.add(new InsnNode(IADD));
        list.add(new FieldInsnNode(PUTSTATIC, CLASS_NAME, var, "I"));

        // jump back & end label
        list.add(new JumpInsnNode(GOTO, ctx.start));
        list.add(ctx.end);
    }

    private void generateGotoInstructions(@NotNull Token token, @NotNull InsnList list) {
        LineNumberExpression lineNumberExpression = (LineNumberExpression) token.expression();
        assert lineNumberExpression != null : "LineNumberExpression should not be null";

        JumpInsnNode jumpInsnNode = new JumpInsnNode(GOTO, null);
        list.add(jumpInsnNode);
        this.needLabelNodes.add(new LabelPlacement(
                lineNumberExpression.getLineNumber(), jumpInsnNode
        ));
    }

    private void generateGosubInstructions(@NotNull List<Token> tokens, @NotNull Token token, @NotNull InsnList list) {
        LineNumberExpression lineNumberExpression = (LineNumberExpression) token.expression();
        assert lineNumberExpression != null : "LineNumberExpression should not be null";

        int startLineNumber = lineNumberExpression.getLineNumber();
        String subroutineName = "subroutine_" + startLineNumber;
        if (!this.subroutineNames.contains(subroutineName)) { // Create the subroutine if needed
            this.subroutineNames.add(subroutineName);

            // Create a method for the subroutine
            MethodNode subroutineMethod = new MethodNode(
                    ACC_PRIVATE | ACC_STATIC,
                    subroutineName,
                    "()V",
                    null,
                    null
            );

            // TODO: Clean up
            List<Token> subroutineTokens = new ArrayList<>(); // Tokens of line number + 1 -> return
            for (Token token1 : tokens) {
                if (token1.lineNumber() >= startLineNumber) {
                    subroutineTokens.add(token1);
                    if (token1.statement().equals(LStatement.RETURN)) break;
                }
            }

            this.generateMethodInstructions(subroutineMethod, subroutineTokens);
            this.classNode.methods.add(subroutineMethod);
        } else {
            throw new IllegalStateException("Subroutine with name " + subroutineName + " already exists.");
        }

        // Call the subroutine
        list.add(new MethodInsnNode(INVOKESTATIC, CLASS_NAME, subroutineName, "()V", false));
    }

    private void generateReturnInstructions(@NotNull InsnList list) {
        list.add(new InsnNode(RETURN));
    }

    private void generateEndInstructions(@NotNull InsnList list) {
        list.add(new InsnNode(ICONST_0));
        list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "exit", "(I)V", false));
    }
    //endregion

    //region Helper Methods
    private InsnList generateMathematicalEquation(MathematicalExpression mathematicalExpression) {
        InsnList list = new InsnList();

        ValueExpression[] values = mathematicalExpression.getValueExpressions();
        LArithmeticOperator[] operators = mathematicalExpression.getOperators();

        if (values.length == 0) {
            return list;
        }

        list.add(this.generateValueExpression(values[0]));

        for (int i = 1; i < values.length; i++) {
            list.add(this.generateValueExpression(values[i]));

            switch (operators[i - 1]) {
                case ADD -> list.add(new InsnNode(IADD));
                case SUBTRACT -> list.add(new InsnNode(ISUB));
                case MULTIPLY -> list.add(new InsnNode(IMUL));
                case DIVIDE -> list.add(new InsnNode(IDIV));
            }
        }

        return list;
    }

    private InsnList generateValueExpression(ValueExpression valueExpression) {
        if (valueExpression.getMathematicalExpression() != null) { // redirect to generateMathematicalEquation
            return this.generateMathematicalEquation(valueExpression.getMathematicalExpression());
        }
        InsnList list = new InsnList();
        if (valueExpression.getVariableName() != null) {
            String variableName = valueExpression.getVariableName();
            this.generateVariable(variableName);
            list.add(new FieldInsnNode(GETSTATIC, CLASS_NAME, variableName, "I"));
        } else {
            list.add(new LdcInsnNode(valueExpression.getLiteralNumberValue()));
        }
        return list;
    }

    private void generateVariable(String variableName) {
        if (!this.variableNames.contains(variableName)) {
            this.variableNames.add(variableName);
            FieldNode fieldNode = new FieldNode(
                    ACC_PRIVATE | ACC_STATIC,
                    variableName,
                    "I",
                    null,
                    null
            );
            this.classNode.fields.add(fieldNode);
        }
    }
    //endregion

    private record LabelPlacement(int lineNumber, JumpInsnNode jumpInsnNode, LabelNode targetLabel) {
        LabelPlacement(int lineNumber, JumpInsnNode jumpInsnNode) {
            this(lineNumber, jumpInsnNode, null);
        }
    }

    private record LoopContext(String variableName, LabelNode end, LabelNode start, ValueExpression step) {
    }
}
