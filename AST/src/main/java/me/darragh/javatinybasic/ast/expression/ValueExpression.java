package me.darragh.javatinybasic.ast.expression;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.VALUE_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Represents a value expression which can either be a variable name, a mathematical expression, or a literal value.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ValueExpression extends Expression {
    @Serial
    private static final long serialVersionUID = VALUE_EXPRESSION_SERIAL_VERSION_UID;

    private final @Nullable String variableName;
    private final @Nullable Integer literalValue;
    private final @Nullable MathematicalExpression mathematicalExpression;

    public ValueExpression(@NotNull String variableName) {
        this.variableName = variableName;
        this.literalValue = null;
        this.mathematicalExpression = null;
    }

    public ValueExpression(int value) {
        this.variableName = null;
        this.literalValue = value;
        this.mathematicalExpression = null;
    }

    public ValueExpression(@NotNull MathematicalExpression mathematicalExpression) {
        this.variableName = null;
        this.literalValue = null;
        this.mathematicalExpression = mathematicalExpression;
    }
}
