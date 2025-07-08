package me.darragh.javatinybasic.parser;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.darragh.javatinybasic.ast.Token;
import me.darragh.javatinybasic.ast.TokenFactory;
import me.darragh.javatinybasic.ast.expression.MathematicalExpression;
import me.darragh.javatinybasic.ast.expression.ValueExpression;
import me.darragh.javatinybasic.ast.langauge.LArithmeticOperator;
import me.darragh.javatinybasic.ast.langauge.LRelationalOperator;
import me.darragh.javatinybasic.ast.langauge.LReservedKeyword;
import me.darragh.javatinybasic.ast.langauge.LStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parses the source of a Tiny BASIC program into a list of tokens.
 */
@Data
@RequiredArgsConstructor
public class Parser {
    //region Patterns
    private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern ENTIRE_LINE_COMMENT_PATTERN = Pattern.compile("^\"[^\"\\r\\n]*\"$");
    private static final Pattern POSITIVE_NUMBER_PATTERN = Pattern.compile("^\\d+");
    private static final Pattern VALID_VARIABLE_NAME_PATTERN = Pattern.compile("[A-Z]"); // Classic tiny BASIC variable names are single uppercase letters
    //endregion

    public static final String CHARACTER_START_END_CHAR = "\"";

    private final @NotNull String source;
    private final @NotNull List<Token> tokens = new ArrayList<>();

    public void parse() throws ParserInvalidLineException {
        List<String> lines = List.of(source.split("\n"));
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() // skip empty lines
                    || line.matches(ENTIRE_LINE_COMMENT_PATTERN.pattern()) // skip *entire* comment lines
            ) {
                continue;
            }
            this.tokens.add(parseLine(line));
        }
    }

    public static @NotNull List<Token> parse(@NotNull String source) throws ParserInvalidLineException {
        Parser parser = new Parser(source);
        parser.parse();
        return parser.tokens;
    }

    /**
     * Parses a single line of Tiny BASIC code into a Token.
     *
     * @param line The line to parse.
     * @return A Token representing the parsed line.
     * @throws ParserInvalidLineException If the line is invalid or cannot be parsed.
     */
    public static @NotNull Token parseLine(@NotNull String line) throws ParserInvalidLineException {
        String tokens = MULTI_SPACE_PATTERN.matcher(line).replaceAll(" ").trim();
        String[] parts = tokens.split(" ");
        if (parts.length == 0) {
            throw ParserInvalidLineException.create("Line cannot be empty: ", line);
        }
        if (parts.length < 2) {
            throw ParserInvalidLineException.create("Line does not have {line number} {statement}: ", line);
        }

        // Fetch line number
        String lineNumberStr = parts[0];
        if (!lineNumberStr.matches(POSITIVE_NUMBER_PATTERN.pattern())) {
            throw ParserInvalidLineException.create("Line number must be a positive integer: ", line);
        }

        int lineNumber;
        try {
            lineNumber = Integer.parseInt(lineNumberStr);
        } catch (NumberFormatException e) {
            throw ParserInvalidLineException.create("Line number must be a positive integer: ", line, e);
        }

        if (lineNumber < 1) {
            throw ParserInvalidLineException.create("Line number must be a positive integer: ", line);
        }

        // Identify the statement type
        LStatement statement = LStatement.fromToken(parts[1]);
        return switch (statement) {
            case LET -> generateLetToken(lineNumber, parts);
            case PRINT -> generatePrintToken(lineNumber, parts);
            case INPUT -> generateInputToken(lineNumber, parts);
            case IF -> generateIfToken(lineNumber, parts);
            case GOTO -> generateGotoToken(lineNumber, parts);
            case END -> generateEndToken(lineNumber);
        };
    }

    //region Token Generators
    private static @NotNull Token generateLetToken(int lineNumber, String[] parts) throws ParserInvalidLineException {
        if (parts.length < 5 || !parts[3].equals(LRelationalOperator.EQUAL.getSymbol())) {
            throw ParserInvalidLineException.create(
                    "LET statement must be in the form: {line number} LET {variable} %s {expression}".formatted(LRelationalOperator.EQUAL.getSymbol()),
                    String.join(" ", parts)
            );
        }

        String variableName = parts[2];
        if (variableName.isEmpty()) {
            throw ParserInvalidLineException.create("Variable name cannot be empty in LET statement: ", String.join(" ", parts));
        }

        return TokenFactory.createLetToken(
                lineNumber,
                variableName,
                parseValueExpression(
                        Arrays.copyOfRange(parts, 4, parts.length)
                )
        );
    }

    private static @NotNull Token generatePrintToken(int lineNumber, String[] parts) throws ParserInvalidLineException {
        if (parts.length < 3) {
            throw ParserInvalidLineException.create(
                    "PRINT statement must be in the form: {line number} PRINT {expression}",
                    String.join(" ", parts)
                    );
        }

        return TokenFactory.createPrintToken(
                lineNumber,
                parseValueExpression(
                        Arrays.copyOfRange(parts, 2, parts.length)
                )
        );
    }

    private static @NotNull Token generateInputToken(int lineNumber, String[] parts) throws ParserInvalidLineException {
        if (parts.length < 3) {
            throw ParserInvalidLineException.create(
                    "INPUT statement must be in the form: {line number} INPUT {variable}",
                    String.join(" ", parts)
            );
        }

        String variableName = parts[2];
        if (variableName.isEmpty()) {
            throw ParserInvalidLineException.create("Variable name cannot be empty in INPUT statement: ", String.join(" ", parts));
        }

        if (!isValidVariableName(variableName)) {
            throw ParserInvalidLineException.create("Invalid variable name in INPUT statement: ", String.join(" ", parts));
        }

        return TokenFactory.createInputToken(lineNumber, variableName);
    }

    private static @NotNull Token generateIfToken(int lineNumber, String[] parts) throws ParserInvalidLineException {
        if (parts.length < 7) {
            throw ParserInvalidLineException.create(
                    "IF statement must be in the form: {line number} IF {condition} THEN {line number}",
                    String.join(" ", parts)
            );
        }

        // Find the THEN keyword
        int thenIndex = -1;
        for (int i = 2; i < parts.length; i++) {
            if (parts[i].equalsIgnoreCase(LReservedKeyword.THEN.getToken())) {
                thenIndex = i;
                break;
            }
        }

        if (thenIndex < 3 || thenIndex >= parts.length - 1) {
            throw ParserInvalidLineException.create(
                    "IF statement must contain a valid THEN keyword: ",
                    String.join(" ", parts)
            );
        }

        // Find the relational operator
        LRelationalOperator relationalOperator = null;
        int operatorIndex = -1;

        for (int i = 2; i < thenIndex; i++) {
            for (LRelationalOperator operator : LRelationalOperator.values()) {
                if (parts[i].equalsIgnoreCase(operator.getSymbol())) {
                    relationalOperator = operator;
                    operatorIndex = i;
                    break;
                }
            }
            if (relationalOperator != null) break;
        }

        if (relationalOperator == null || operatorIndex == 2 || operatorIndex >= thenIndex - 1) {
            throw ParserInvalidLineException.create(
                    "IF statement must contain a valid relational operator before THEN: ",
                    String.join(" ", parts)
            );
        }

        // Parse the value expressions on either side of the operator
        ValueExpression valueA = parseValueExpression(Arrays.copyOfRange(parts, 2, operatorIndex));
        ValueExpression valueB = parseValueExpression(Arrays.copyOfRange(parts, operatorIndex + 1, thenIndex));

        // Parse the line number to GOTO
        return TokenFactory.createIfToken(
                lineNumber,
                valueA,
                valueB,
                relationalOperator,
                parseTokenGotoLineNumber(parts, parts[thenIndex + 1])
        );
    }

    private static @NotNull Token generateGotoToken(int lineNumber, String[] parts) throws ParserInvalidLineException {
        if (parts.length != 3) {
            throw ParserInvalidLineException.create(
                    "GOTO statement must be in the form: {line number} GOTO {line number}",
                    String.join(" ", parts)
            );
        }

        String lineNumberToGotoStr = parts[2];
        return TokenFactory.createGotoToken(
                lineNumber,
                parseTokenGotoLineNumber(parts, lineNumberToGotoStr)
        );
    }

    private static @NotNull Token generateEndToken(int lineNumber) {
        return TokenFactory.createEndToken(lineNumber);
    }

    private static int parseTokenGotoLineNumber(String[] parts, String lineNumberPart) throws ParserInvalidLineException {
        if (!lineNumberPart.matches(POSITIVE_NUMBER_PATTERN.pattern())) {
            throw ParserInvalidLineException.create("Line number to GOTO must be a positive integer: ", String.join(" ", parts));
        }
        int lineNumberToGoto;
        try {
            lineNumberToGoto = Integer.parseInt(lineNumberPart);
        } catch (NumberFormatException e) {
            throw ParserInvalidLineException.create("Line number to GOTO must be a positive integer: ", String.join(" ", parts), e);
        }
        if (lineNumberToGoto < 1) {
            throw ParserInvalidLineException.create("Line number to GOTO must be a positive integer: ", String.join(" ", parts));
        }
        return lineNumberToGoto;
    }
    //endregion

    //region Expression Generators
    private static ValueExpression parseValueExpression(@NonNull String[] parts) throws ParserInvalidLineException {
        return parseValueExpression(parts, true);
    }

    private static ValueExpression parseValueExpression(@NotNull String[] parts, boolean supportMathematical) throws ParserInvalidLineException {
        return switch (parts.length) {
            case 0 -> throw ParserInvalidLineException.create("Value expression cannot be empty: ", String.join(" ", parts));
            case 1 -> { // Handle variables/literals
                String part = parts[0];
                if (part.matches(POSITIVE_NUMBER_PATTERN.pattern())) {
                    yield new ValueExpression(Integer.parseInt(part));
                } else {
                    if (!isValidVariableName(part)) {
                        throw ParserInvalidLineException.create("Invalid variable name in value expression: ", String.join(" ", parts));
                    }
                    yield new ValueExpression(part);
                }
            }
            default -> {
                if (!supportMathematical) {
                    throw ParserInvalidLineException.create("Mathematical expressions are not supported in this context: ", String.join(" ", parts));
                }
                yield new ValueExpression(parseMathematicalExpression(parts));
            }
        };
    }

    public static MathematicalExpression parseMathematicalExpression(@NotNull String[] parts) throws ParserInvalidLineException {
        if (parts.length < 3) {
            throw ParserInvalidLineException.create("Mathematical expression must have at least 3 parts: ", String.join(" ", parts));
        }

        List<ValueExpression> valueExpressions = new ArrayList<>();
        List<LArithmeticOperator> arithmeticOperators = new ArrayList<>();
        boolean expectingOperator = false;

        for (String s : parts) {
            String part = s.trim();
            if (part.isEmpty()) {
                continue; // Skip empty parts
            }

            if (expectingOperator) {
                LArithmeticOperator operator = LArithmeticOperator.fromSymbol(part);
                arithmeticOperators.add(operator);
                expectingOperator = false;
            } else {
                ValueExpression valueExpression = parseValueExpression(new String[]{part}, false);
                valueExpressions.add(valueExpression);
                expectingOperator = true;
            }
        }

        if (valueExpressions.isEmpty()) {
            throw ParserInvalidLineException.create("Mathematical expression must contain at least one value: ", String.join(" ", parts));
        }

        if (expectingOperator) {
            throw ParserInvalidLineException.create("Mathematical expression cannot end with an operator: ", String.join(" ", parts));
        }

        return new MathematicalExpression(
                valueExpressions.toArray(new ValueExpression[0]),
                arithmeticOperators.toArray(new LArithmeticOperator[0])
        );
    }
    //endregion

    //region Utility Methods
    private static boolean isValidVariableName(@NotNull String variableName) {
        return variableName.length() == 1 && VALID_VARIABLE_NAME_PATTERN.matcher(variableName).matches();
    }
    //endregion
}
