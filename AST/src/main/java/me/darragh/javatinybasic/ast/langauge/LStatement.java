package me.darragh.javatinybasic.ast.langauge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An enumeration representing the different types of statements in the Tiny BASIC language.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum LStatement {
    REM("REM"), // {line number} REM {comment} - not generated in AST
    LET("LET"), // {line number} LET {variable} = {expression}
    PRINT("PRINT"), // {line number} PRINT {expression}
    INPUT("INPUT"), // {line number} INPUT {variable}
    IF("IF"), // {line number} IF {condition} THEN {line number}
    FOR("FOR"), // {line number} FOR {variable} = {start} TO {end} STEP {step}
    NEXT("NEXT"), // {line number} NEXT {variable} (loop control)
    GOTO("GOTO"), // {line number} GOTO {line number}
    GOSUB("GOSUB"), // {line number} GOSUB {line number}
    RETURN("RETURN"), // {line number} RETURN (GOSUB only)
    END("END"); // {line number} END

    private final String token;

    /**
     * Converts a string token to the corresponding {@link LStatement}.
     *
     * @param token The string representation of the statement.
     * @return The corresponding LStatement enum value.
     * @throws IllegalArgumentException if the token does not match any statement.
     */
    public static LStatement fromToken(String token) {
        for (LStatement statement : values()) {
            if (statement.token.equalsIgnoreCase(token)) {
                return statement;
            }
        }
        throw new IllegalArgumentException("Unknown statement token: " + token);
    }
}
