package me.darragh.javatinybasic.ast.expression.statement;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.darragh.javatinybasic.ast.expression.Expression;
import me.darragh.javatinybasic.ast.expression.StringValueExpression;
import me.darragh.javatinybasic.ast.expression.ValueExpression;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.PRINT_STATEMENT_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Represents a PRINT expression, which consists of one or more values to be printed.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class PRINTExpression extends Expression {
    @Serial
    private static final long serialVersionUID = PRINT_STATEMENT_EXPRESSION_SERIAL_VERSION_UID;

    /**
     * @apiNote Should be either a {@link ValueExpression} or a {@link StringValueExpression}.
     */
    // TODO: Validation
    private final @NotNull Expression[] values;
}
