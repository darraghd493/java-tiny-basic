package me.darragh.javatinybasic.ast.expression.statement;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.darragh.javatinybasic.ast.expression.Expression;
import me.darragh.javatinybasic.ast.expression.ValueExpression;
import me.darragh.javatinybasic.ast.langauge.LRelationalOperator;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.CONDITION_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Represents an IF expression, which consists of two value expressions and a relational operator to compare them.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class IFExpression extends Expression {
    @Serial
    private static final long serialVersionUID = CONDITION_EXPRESSION_SERIAL_VERSION_UID;

    private final @NotNull ValueExpression valueA;
    private final @NotNull ValueExpression valueB;
    private final @NotNull LRelationalOperator relationalOperator;
}
