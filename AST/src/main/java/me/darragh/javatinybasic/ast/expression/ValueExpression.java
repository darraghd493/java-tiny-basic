package me.darragh.javatinybasic.ast.expression;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.VALUE_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Represents a dynamic value expression in the AST. It could be either a variable, literal number, or a mathematical expression.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ValueExpression extends Expression {
    @Serial
    private static final long serialVersionUID = VALUE_EXPRESSION_SERIAL_VERSION_UID;

    private final @Nullable String variableName;
    private final @Nullable Integer literalNumberValue;
    private final @Nullable MathematicalExpression mathematicalExpression;

    public ValueExpression(@NotNull String variableName) {
        this.variableName = variableName;
        this.literalNumberValue = null;
        this.mathematicalExpression = null;
    }

    public ValueExpression(int value) {
        this.variableName = null;
        this.literalNumberValue = value;
        this.mathematicalExpression = null;
    }

    public ValueExpression(@NotNull MathematicalExpression mathematicalExpression) {
        this.variableName = null;
        this.literalNumberValue = null;
        this.mathematicalExpression = mathematicalExpression;
    }
}
