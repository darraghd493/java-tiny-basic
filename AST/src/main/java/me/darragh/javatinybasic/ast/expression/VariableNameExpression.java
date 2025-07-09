package me.darragh.javatinybasic.ast.expression;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.VARIABLE_NAME_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Explicitly represents a variable name expression.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VariableNameExpression extends Expression { // Used for explicit definition
    @Serial
    private static final long serialVersionUID = VARIABLE_NAME_EXPRESSION_SERIAL_VERSION_UID;

    private final @NotNull String variableName;
}
