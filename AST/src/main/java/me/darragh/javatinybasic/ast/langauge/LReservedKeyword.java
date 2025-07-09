package me.darragh.javatinybasic.ast.langauge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An enumeration representing the reserved keywords in the Tiny BASIC language.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum LReservedKeyword {
    THEN("THEN"), // for IF statement
    TO("TO"), // for FOR statement
    STEP("STEP"); // for FOR statement

    private final String token;

    /**
     * Converts a string token to the corresponding {@link LReservedKeyword}.
     *
     * @param token The string representation of the reserved keyword.
     * @return The corresponding LReservedKeyword enum value.
     * @throws IllegalArgumentException if the token does not match any reserved keyword.
     */
    public static LReservedKeyword fromToken(String token) {
        for (LReservedKeyword keyword : values()) {
            if (keyword.token.equalsIgnoreCase(token)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("Unknown reserved keyword token: " + token);
    }
}
