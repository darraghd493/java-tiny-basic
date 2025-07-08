package me.darragh.javatinybasic.ast.langauge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LArithmeticOperator {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/");

    private final String symbol;

    public static LArithmeticOperator fromSymbol(String symbol) {
        for (LArithmeticOperator expression : values()) {
            if (expression.symbol.equals(symbol)) {
                return expression;
            }
        }
        throw new IllegalArgumentException("Unknown expression symbol: " + symbol);
    }
}
