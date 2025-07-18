package me.darragh.javatinybasic.ast.expression.statement;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.darragh.javatinybasic.ast.expression.Expression;
import me.darragh.javatinybasic.ast.expression.ValueExpression;
import me.darragh.javatinybasic.ast.langauge.LRelationalOperator;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.IF_STATEMENT_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Represents an IF expression, which consists of two value expressions, a relational operator to compare them and a line number to jump to if the condition is true.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class IFExpression extends Expression {
    @Serial
    private static final long serialVersionUID = IF_STATEMENT_EXPRESSION_SERIAL_VERSION_UID;

    private final @NotNull ValueExpression valueA;
    private final @NotNull ValueExpression valueB;
    private final @NotNull LRelationalOperator relationalOperator;
    private final int lineNumberToGoto;
}
