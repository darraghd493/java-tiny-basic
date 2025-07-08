package me.darragh.javatinybasic.ast;

import lombok.experimental.UtilityClass;
import me.darragh.javatinybasic.ast.expression.LineNumberExpression;
import me.darragh.javatinybasic.ast.expression.ValueExpression;
import me.darragh.javatinybasic.ast.expression.VariableNameNumberExpression;
import me.darragh.javatinybasic.ast.expression.statement.IFExpression;
import me.darragh.javatinybasic.ast.expression.statement.LETExpression;
import me.darragh.javatinybasic.ast.langauge.LRelationalOperator;
import me.darragh.javatinybasic.ast.langauge.LStatement;
import org.jetbrains.annotations.NotNull;

/**
 * Standardises the creation of {@link Token}s.
 */
@UtilityClass
public class TokenFactory {
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

    public static Token createPrintToken(int lineNumber, @NotNull ValueExpression valueExpression) {
        return new Token(
                lineNumber,
                LStatement.PRINT,
                valueExpression
        );
    }

    public static @NotNull Token createInputToken(int lineNumber, String variableName) {
        return new Token(
                lineNumber,
                LStatement.INPUT,
                new VariableNameNumberExpression(
                        variableName
                )
        );
    }

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

    public static @NotNull Token createGotoToken(int lineNumber, int lineNumberToGoto) {
        return new Token(
                lineNumber,
                LStatement.GOTO,
                new LineNumberExpression(lineNumberToGoto)
        );
    }

    public static @NotNull Token createEndToken(int lineNumber) {
        return new Token(
                lineNumber,
                LStatement.END,
                null
        );
    }
}
