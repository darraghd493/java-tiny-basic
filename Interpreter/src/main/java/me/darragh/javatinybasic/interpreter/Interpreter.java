package me.darragh.javatinybasic.interpreter;

import lombok.Data;
import me.darragh.javatinybasic.ast.Token;
import me.darragh.javatinybasic.ast.expression.*;
import me.darragh.javatinybasic.ast.expression.statement.FORExpression;
import me.darragh.javatinybasic.ast.expression.statement.IFExpression;
import me.darragh.javatinybasic.ast.expression.statement.LETExpression;
import me.darragh.javatinybasic.ast.expression.statement.PRINTExpression;
import me.darragh.javatinybasic.ast.langauge.LArithmeticOperator;
import me.darragh.javatinybasic.ast.langauge.LRelationalOperator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Interprets tokenised Tiny BASIC code.
 */
@Data
public class Interpreter {
    private static int INVALID_LINE_NUMBER = -1;

    //region Cache
    private final Map<Integer, Token> tokens;
    private final List<Integer> lineNumbers;
    //endregion

    private final Map<String, Integer> variables;
    private final Map<String, ForData> forLoopPointers;
    private final Deque<Integer> returnStack = new ArrayDeque<>();
    private int currentLineNumber;
    private int temporaryLineNumber; // for GOSUB

    //region Constructor
    public Interpreter(List<Token> tokens, InterpreterInputEvent inputEvent,
                       InterpreterOutputEvent outputEvent, InterpreterFinishedEvent finishedEvent) {
        // Cache the tokens and line numbers for quick access
        this.tokens = tokens.stream()
                .sorted(Comparator.comparing(Token::lineNumber))
                .collect(Collectors.toMap(
                        Token::lineNumber,
                        token -> token,
                        (a, b) -> a,
                        LinkedHashMap::new));
        this.lineNumbers = new ArrayList<>(this.tokens.keySet());

        // Prepare the variables map
        this.variables = new HashMap<>();
        this.forLoopPointers = new HashMap<>();

        // Set up the event handlers
        this.inputEvent = inputEvent;
        this.outputEvent = outputEvent;
        this.finishedEvent = finishedEvent;
    }
    //endregion

    //region Events
    private final InterpreterInputEvent inputEvent;
    private final InterpreterOutputEvent outputEvent;
    private final InterpreterFinishedEvent finishedEvent;
    //endregion

    @SuppressWarnings("StatementWithEmptyBody")
    public void run() {
        this.wipe();

        while (this.step())
            ;

        this.finishedEvent.onFinished();
    }

    public boolean step() {
        int lineNumber = this.temporaryLineNumber != INVALID_LINE_NUMBER ? this.getNextLineNumber(this.temporaryLineNumber) : this.currentLineNumber;
        Token token = this.tokens.get(lineNumber);
        if (token == null) {
            throw new IllegalStateException("No token found for line number: " + lineNumber);
        }
        this.temporaryLineNumber = INVALID_LINE_NUMBER; // Reset temporary line number after use

        switch (token.statement()) {
            case LET -> {
                LETExpression letExpression = (LETExpression) token.expression();
                assert letExpression != null;

                String variableName = letExpression.getVariableName();

                int value = this.evaluateValueExpression(letExpression.getValue());
                this.variables.put(variableName, value);
            }
            case PRINT -> {
                PRINTExpression printExpression = (PRINTExpression) token.expression();
                assert printExpression != null;

                List<String> objects = new ArrayList<>();
                for (Expression expression : printExpression.getValues()) {
                    if (expression instanceof ValueExpression valueExpression) {
                        objects.add(String.valueOf(this.evaluateValueExpression(valueExpression)));
                    } else if (expression instanceof StringValueExpression stringValueExpression) {
                        objects.add(stringValueExpression.getValue());
                    } else {
                        throw new IllegalStateException("Unsupported expression type: " + expression);
                    }
                }

                String output = String.join(" ", objects);
                this.outputEvent.onOutput(output);
            }
            case INPUT -> {
                VariableNameExpression variableExpression = (VariableNameExpression) token.expression();
                assert variableExpression != null;
                this.variables.put(
                        variableExpression.getVariableName(),
                        this.inputEvent.getInput()
                );
            }
            case IF -> {
                IFExpression ifExpression = (IFExpression) token.expression();
                assert ifExpression != null;

                int valueA = evaluateValueExpression(ifExpression.getValueA()),
                        valueB = evaluateValueExpression(ifExpression.getValueB());

                if (this.testRelationalOperator(ifExpression.getRelationalOperator(), valueA, valueB)) {
                    this.currentLineNumber = ifExpression.getLineNumberToGoto();
                    return true;
                }
            }
            case FOR -> {
                // TODO: Verify
                FORExpression forExpression = (FORExpression) token.expression();
                assert forExpression != null;

                String variableName = forExpression.getVariableName();

                if (!this.forLoopPointers.containsKey(variableName)) {
                    int startValue = this.evaluateValueExpression(forExpression.getStartValue()),
                            stepValue = this.evaluateValueExpression(forExpression.getStepValue());

                    if (stepValue == 0) {
                        throw new IllegalStateException("Step value cannot be zero in FOR loop: " + forExpression);
                    }

                    this.variables.put(variableName, startValue);
                    this.forLoopPointers.put(variableName, new ForData(
                            this.currentLineNumber,
                            forExpression.getEndValue(),
                            forExpression.getStepValue()
                    ));

                    this.variables.put(variableName, startValue);
                }
            }
            case NEXT -> {
                // TODO: Verify
                VariableNameExpression variableExpression = (VariableNameExpression) token.expression();
                assert variableExpression != null;

                String variableName = variableExpression.getVariableName();
                if (!this.forLoopPointers.containsKey(variableName)) {
                    throw new IllegalStateException("NEXT without matching FOR for variable: " + variableName);
                }

                int currentValue = this.variables.get(variableName),
                        stepValue = this.evaluateValueExpression(this.forLoopPointers.get(variableName).stepValue);

                currentValue += stepValue;
                this.variables.put(variableName, currentValue);

                int endValue = this.evaluateValueExpression(this.forLoopPointers.get(variableName).endValue);

                if ((stepValue > 0 && currentValue > endValue) || (stepValue < 0 && currentValue < endValue)) {
                    this.forLoopPointers.remove(variableName);
                } else {
                    this.currentLineNumber = this.forLoopPointers.get(variableName).lineNumber;
                    return true;
                }
            }
            case GOTO -> {
                LineNumberExpression lineNumberExpression = (LineNumberExpression) token.expression();
                assert lineNumberExpression != null;
                this.currentLineNumber = lineNumberExpression.getLineNumber();
                return true;
            }
            case GOSUB -> {
                LineNumberExpression lineNumberExpression = (LineNumberExpression) token.expression();
                assert lineNumberExpression != null;
                this.returnStack.push(this.getNextLineNumber(this.currentLineNumber));
                this.currentLineNumber = lineNumberExpression.getLineNumber();
                return true;
            }
            case RETURN -> {
                if (returnStack.isEmpty()) {
                    throw new IllegalStateException("RETURN without GOSUB");
                }
                currentLineNumber = returnStack.pop();
                return true;
            }
            case END -> {
                return false;
            }
        }

        // Progress to the next line
        this.currentLineNumber = this.getNextLineNumber(this.currentLineNumber);
        return this.currentLineNumber != INVALID_LINE_NUMBER;
    }

    public void wipe() {
        this.currentLineNumber = this.lineNumbers.getFirst();
        this.temporaryLineNumber = INVALID_LINE_NUMBER;
        this.variables.clear();
        this.forLoopPointers.clear();
    }

    //region Helper Methods
    private int getNextLineNumber(int lineNumber) {
        int nextLineIndex = this.lineNumbers.indexOf(lineNumber) + 1;
        if (nextLineIndex < this.lineNumbers.size()) {
            return this.lineNumbers.get(nextLineIndex);
        } else {
            return INVALID_LINE_NUMBER;
        }
    }

    private boolean testRelationalOperator(LRelationalOperator relationalOperator, int valueA, int valueB) {
        return switch (relationalOperator) {
            case EQUAL -> valueA == valueB;
            case NOT_EQUAL -> valueA != valueB;
            case LESS_THAN ->  valueA < valueB;
            case LESS_THAN_OR_EQUAL ->  valueA <= valueB;
            case GREATER_THAN ->  valueA > valueB;
            case GREATER_THAN_OR_EQUAL ->  valueA >= valueB;
        };
    }

    private int evaluateValueExpression(ValueExpression expression) {
        if (expression.getVariableName() != null) {
            if (this.variables.containsKey(expression.getVariableName())) {
                return this.variables.get(expression.getVariableName());
            } else {
                throw new IllegalStateException("Variable not found: " + expression.getVariableName());
            }
        } else if (expression.getLiteralNumberValue() != null) {
            return expression.getLiteralNumberValue();
        } else if (expression.getMathematicalExpression() != null) {
            return this.evaluateMathematicalExpression(expression.getMathematicalExpression());
        } else {
            throw new IllegalStateException("Invalid ValueExpression: " + expression);
        }
    }

    private int evaluateMathematicalExpression(MathematicalExpression expression) {
        int result = this.evaluateValueExpression(expression.getValueExpressions()[0]);
        for (int i = 0; i < expression.getOperators().length; i++) {
            LArithmeticOperator operator = expression.getOperators()[i];
            int nextValue = this.evaluateValueExpression(expression.getValueExpressions()[i + 1]);

            switch (operator) {
                case ADD -> result += nextValue;
                case SUBTRACT -> result -= nextValue;
                case MULTIPLY -> result *= nextValue;
                case DIVIDE -> {
                    if (nextValue == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    result /= nextValue;
                }
            }
        }
        return result;
    }
    //endregion

    private record ForData(int lineNumber, ValueExpression endValue, ValueExpression stepValue) { // Used for FOR loop data
    }
}
