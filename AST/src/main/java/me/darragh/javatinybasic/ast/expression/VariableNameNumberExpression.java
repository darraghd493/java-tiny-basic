package me.darragh.javatinybasic.ast.expression;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.VARIABLE_NAME_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Represents a line number as Tiny BASIC does not support variable names/arithmetic in line numbers expressions (GOTO {expression}).
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VariableNameNumberExpression extends Expression { // Used for explicit definition
    @Serial
    private static final long serialVersionUID = VARIABLE_NAME_EXPRESSION_SERIAL_VERSION_UID;

    private final @NotNull String variableName;
}
