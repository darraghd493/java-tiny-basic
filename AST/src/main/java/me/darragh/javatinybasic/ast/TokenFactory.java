package me.darragh.javatinybasic.ast;

import lombok.experimental.UtilityClass;
import me.darragh.javatinybasic.ast.expression.Expression;
import me.darragh.javatinybasic.ast.expression.LineNumberExpression;
import me.darragh.javatinybasic.ast.expression.ValueExpression;
import me.darragh.javatinybasic.ast.expression.VariableNameExpression;
import me.darragh.javatinybasic.ast.expression.statement.FORExpression;
import me.darragh.javatinybasic.ast.expression.statement.IFExpression;
import me.darragh.javatinybasic.ast.expression.statement.LETExpression;
import me.darragh.javatinybasic.ast.expression.statement.PRINTExpression;
import me.darragh.javatinybasic.ast.langauge.LRelationalOperator;
import me.darragh.javatinybasic.ast.langauge.LStatement;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class that standardises the creation of {@link Token}s.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@UtilityClass
public class TokenFactory {
    /**
     * Creates a {@link Token} for a {@link LStatement#REM} statement.
     *
     * @param lineNumber The line number of the statement.
     * @param variableName The variable name for the REM statement.
     * @param valueExpression The value expression for the REM statement.
     * @return A {@link Token} representing the REM statement.
     */
    public static Token createLetToken(int lineNumber, @NotNull String variableName, @NotNull ValueExpression valueExpression) {
        return new Token(
                lineNumber,
                LStatement.LET,
                new LETExpression(
                        variableName,
                        valueExpression
                )
        );
    }

    /**
     * Creates a {@link Token} for a {@link LStatement#PRINT} statement.
     *
     * @param lineNumber The line number of the statement.
     * @param expressions The expressions to print.
     * @return A {@link Token} representing the PRINT statement.
     */
    public static Token createPrintToken(int lineNumber, @NotNull Expression... expressions) {
        return new Token(
                lineNumber,
                LStatement.PRINT,
                new PRINTExpression(
                        expressions
                )
        );
    }

    /**
     * Creates a {@link Token} for a {@link LStatement#INPUT} statement.
     *
     * @param lineNumber The line number of the statement.
     * @param variableName The variable name to input into.
     * @return A {@link Token} representing the INPUT statement.
     */
    public static @NotNull Token createInputToken(int lineNumber, String variableName) {
        return new Token(
                lineNumber,
                LStatement.INPUT,
                new VariableNameExpression(
                        variableName
                )
        );
    }

    /**
     * Creates a {@link Token} for a {@link LStatement#IF} statement.
     *
     * @param lineNumber The line number of the statement.
     * @param valueA The first value expression to compare.
     * @param valueB The second value expression to compare.
     * @param relationalOperator The relational operator to use for comparison.
     * @param lineNumberToGoto The line number to go to if the condition is true.
     * @return A {@link Token} representing the IF statement.
     */
    public static @NotNull Token createIfToken(int lineNumber, @NotNull ValueExpression valueA, @NotNull ValueExpression valueB, @NotNull LRelationalOperator relationalOperator, int lineNumberToGoto) {
        return new Token(
                lineNumber,
                LStatement.IF,
                new IFExpression(
                        valueA,
                        valueB,
                        relationalOperator,
                        lineNumberToGoto
                )
        );
    }

    /**
     * Creates a {@link Token} for a {@link LStatement#FOR} statement.
     *
     * @param lineNumber The line number of the statement.
     * @param variableName The variable name to use in the FOR loop.
     * @param startValue The starting value of the loop.
     * @param endValue The ending value of the loop.
     * @param stepValue The step value of the loop.
     * @return A {@link Token} representing the FOR statement.
     */
    public static @NotNull Token createForToken(int lineNumber, String variableName, ValueExpression startValue, ValueExpression endValue, ValueExpression stepValue) {
        return new Token(
                lineNumber,
                LStatement.FOR,
                new FORExpression(
                        variableName,
                        startValue,
                        endValue,
                        stepValue
                )
        );
    }

    /**
     * Creates a {@link Token} for a {@link LStatement#NEXT} statement.
     *
     * @param lineNumber The line number of the statement.
     * @param variableName The variable name to use in the NEXT statement.
     * @return A {@link Token} representing the NEXT statement.
     */
    public static @NotNull Token createNextToken(int lineNumber, String variableName) {
        return new Token(
                lineNumber,
                LStatement.NEXT,
                new VariableNameExpression(
                        variableName
                )
        );
    }

    /**
     * Creates a {@link Token} for a {@link LStatement#GOTO} statement.
     *
     * @param lineNumber The line number of the statement.
     * @param lineNumberToGoto The line number to go to.
     * @return A {@link Token} representing the GOTO statement.
     */
    public static @NotNull Token createGotoToken(int lineNumber, int lineNumberToGoto) {
        return new Token(
                lineNumber,
                LStatement.GOTO,
                new LineNumberExpression(lineNumberToGoto)
        );
    }

    /**
     * Creates a {@link Token} for a {@link LStatement#GOSUB} statement.
     *
     * @param lineNumber The line number of the statement.
     * @param lineNumberToGoto The line number to go to in the subroutine.
     * @return A {@link Token} representing the GOSUB statement.
     */
    public static @NotNull Token createGosubToken(int lineNumber, int lineNumberToGoto) {
        return new Token(
                lineNumber,
                LStatement.GOSUB,
                new LineNumberExpression(lineNumberToGoto)
        );
    }

    /**
     * Creates a {@link Token} for a {@link LStatement#RETURN} statement.
     *
     * @param lineNumber The line number of the statement.
     * @return A {@link Token} representing the RETURN statement.
     */
    public static @NotNull Token createReturnToken(int lineNumber) {
        return new Token(
                lineNumber,
                LStatement.RETURN,
                null
        );
    }

    /**
     * Creates a {@link Token} for a {@link LStatement#END} statement.
     *
     * @param lineNumber The line number of the statement.
     * @return A {@link Token} representing the END statement.
     */
    public static @NotNull Token createEndToken(int lineNumber) {
        return new Token(
                lineNumber,
                LStatement.END,
                null
        );
    }
}
