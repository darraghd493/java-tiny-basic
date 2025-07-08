package me.darragh.javatinybasic.ast.langauge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LReservedKeyword {
    THEN("THEN"); // only reserved in IF statement

    private final String token;

    public static LReservedKeyword fromToken(String token) {
        for (LReservedKeyword keyword : values()) {
            if (keyword.token.equalsIgnoreCase(token)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("Unknown reserved keyword token: " + token);
    }
}
