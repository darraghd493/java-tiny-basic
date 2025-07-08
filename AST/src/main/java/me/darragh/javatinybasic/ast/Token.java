package me.darragh.javatinybasic.ast;

import me.darragh.javatinybasic.ast.expression.Expression;
import me.darragh.javatinybasic.ast.langauge.LStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;

import static me.darragh.javatinybasic.ast.Serials.TOKEN_SERIAL_VERSION_UID;

/**
 * Embodies a complete statement.
 */
public record Token(
        int lineNumber, // required, represents the line number of the statement
        @NotNull LStatement statement, // required, represents the type of statement
        @Nullable Expression expression // optional, represents the {condition} or {expression}
) implements Serializable {
    @Serial
    private static final long serialVersionUID = TOKEN_SERIAL_VERSION_UID;
}
