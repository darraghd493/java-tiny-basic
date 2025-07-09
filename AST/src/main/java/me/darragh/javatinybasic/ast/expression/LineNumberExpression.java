package me.darragh.javatinybasic.ast.expression;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.LINE_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Explicitly represents a line number expression.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LineNumberExpression extends Expression { // Used for explicit definition
    @Serial
    private static final long serialVersionUID = LINE_EXPRESSION_SERIAL_VERSION_UID;

    private final int lineNumber;
}
