package me.darragh.javatinybasic.ast.langauge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    END("END"); // {line number} END

    private final String token;

    public static LStatement fromToken(String token) {
        for (LStatement statement : values()) {
            if (statement.token.equalsIgnoreCase(token)) {
                return statement;
            }
        }
        throw new IllegalArgumentException("Unknown statement token: " + token);
    }
}
