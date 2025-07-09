package me.darragh.javatinybasic.ast;

import me.darragh.javatinybasic.ast.expression.Expression;
import me.darragh.javatinybasic.ast.langauge.LStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;

import static me.darragh.javatinybasic.ast.Serials.TOKEN_SERIAL_VERSION_UID;

/**
 * Embodies a single line of code in the Java Tiny Basic language.
 * This is effectively the 'head' of the AST (Abstract Syntax Tree) for a single line.
 * <b>Note:</b> ASTs are not strictly defined.
 *
 * @author darraghd493
 * @since 1.0.0
 */
public record Token(
        int lineNumber, // required, represents the line number of the statement
        @NotNull LStatement statement, // required, represents the type of statement
        @Nullable Expression expression // optional, represents the {condition} or {expression}
) implements Serializable {
    @Serial
    private static final long serialVersionUID = TOKEN_SERIAL_VERSION_UID;
}
