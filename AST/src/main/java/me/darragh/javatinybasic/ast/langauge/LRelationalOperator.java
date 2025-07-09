package me.darragh.javatinybasic.ast.langauge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An enumeration representing the relational operators in the Tiny BASIC language.
 * These operators are used in expressions for comparisons.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum LRelationalOperator {
    EQUAL("="),
    NOT_EQUAL("<>"),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_OR_EQUAL("<="),
    GREATER_THAN_OR_EQUAL(">=");

    private final String symbol;

    /**
     * Converts a string symbol to the corresponding {@link LRelationalOperator}.
     *
     * @param symbol The string representation of the relational operator.
     * @return The corresponding LRelationalOperator enum value.
     */
    public static LRelationalOperator fromSymbol(String symbol) {
        for (LRelationalOperator expression : values()) {
            if (expression.symbol.equals(symbol)) {
                return expression;
            }
        }
        throw new IllegalArgumentException("Unknown expression symbol: " + symbol);
    }
}
