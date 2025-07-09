package me.darragh.javatinybasic.ast.langauge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An enumeration representing the arithmetic operators in the Tiny BASIC language.
 * These operators are used in expressions for arithmetic calculations.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum LArithmeticOperator {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/");

    private final String symbol;

    /**
     * Converts a string symbol to the corresponding {@link LArithmeticOperator}.
     *
     * @param symbol The string representation of the arithmetic operator.
     * @return The corresponding LArithmeticOperator enum value.
     */
    public static LArithmeticOperator fromSymbol(String symbol) {
        for (LArithmeticOperator expression : values()) {
            if (expression.symbol.equals(symbol)) {
                return expression;
            }
        }
        throw new IllegalArgumentException("Unknown expression symbol: " + symbol);
    }
}
