package me.darragh.javatinybasic.ast.langauge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    public static LRelationalOperator fromSymbol(String symbol) {
        for (LRelationalOperator expression : values()) {
            if (expression.symbol.equals(symbol)) {
                return expression;
            }
        }
        throw new IllegalArgumentException("Unknown expression symbol: " + symbol);
    }
}
