package me.darragh.javatinybasic.parser;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lexically analyses a line of code in the Java Tiny Basic language, producing a list of tokens.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@UtilityClass
public class Lexer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "\"[^\"]*\"" + // String literals
                    "|[A-Z]+" + // Keywords/Variables
                    "|\\d+" + // Numbers
                    "|<=|>=|<>|=|<|>|\\+|-|\\*|/" + // Operators
                    "|\\(|\\)|," // Special symbols
    );

    public static List<String> tokenise(String line) {
        Matcher matcher = TOKEN_PATTERN.matcher(line);
        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }
}
