package me.darragh.javatinybasic.ast.expression;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.STRING_VALUE_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Explicitly represents a string expression.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class StringValueExpression extends Expression {
    @Serial
    private static final long serialVersionUID = STRING_VALUE_EXPRESSION_SERIAL_VERSION_UID;

    private final @NotNull String value;
}
