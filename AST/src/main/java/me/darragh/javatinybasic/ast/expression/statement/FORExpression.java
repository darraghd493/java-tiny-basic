package me.darragh.javatinybasic.ast.expression.statement;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.darragh.javatinybasic.ast.expression.Expression;
import me.darragh.javatinybasic.ast.expression.ValueExpression;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.FOR_STATEMENT_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Represents a FOR expression, which consists of the loop variable name, two boundary value expressions, and a step value expression.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class FORExpression extends Expression {
    @Serial
    private static final long serialVersionUID = FOR_STATEMENT_EXPRESSION_SERIAL_VERSION_UID;

    private final @NotNull String variableName;
    private final @NotNull ValueExpression startValue;
    private final @NotNull ValueExpression endValue;
    private final @NotNull ValueExpression stepValue;
}
