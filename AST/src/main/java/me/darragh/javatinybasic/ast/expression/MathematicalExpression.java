package me.darragh.javatinybasic.ast.expression;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.darragh.javatinybasic.ast.langauge.LArithmeticOperator;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.MATHEMATICAL_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Represents a mathematical expression, composed of a chain of: literal -> operator -> literal, ...
 * Must always end with a literal.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MathematicalExpression extends Expression {
    @Serial
    private static final long serialVersionUID = MATHEMATICAL_EXPRESSION_SERIAL_VERSION_UID; // Serial version UID for this class

    /**
     * @apiNote No {@link ValueExpression} should contain another {@link MathematicalExpression} as
     * brackets do not exist in the core language.
     */
    private final ValueExpression[] valueExpressions;
    private final LArithmeticOperator[] operators;

    /**
     * Constructs a MathematicalExpression with the given value expressions and operators.
     *
     * @param valueExpressions An array of ValueExpression objects representing the values in the expression.
     * @param arithmeticOperators An array of strings representing the arithmetic operators between the values.
     */
    public MathematicalExpression(ValueExpression[] valueExpressions, LArithmeticOperator[] arithmeticOperators) {
        this.valueExpressions = valueExpressions;
        this.operators = arithmeticOperators;
    }
}
