package me.darragh.javatinybasic.ast.expression;

import java.io.Serial;
import java.io.Serializable;

import static me.darragh.javatinybasic.ast.Serials.EXPRESSION_SERIAL_VERSION_UID;

/**
 * An abstract base class for expressions in Java Tiny BASIC.
 *
 * @author darraghd493
 * @since 1.0.0
 */
public abstract class Expression implements Serializable {
    @Serial
    private static final long serialVersionUID = EXPRESSION_SERIAL_VERSION_UID;
}
