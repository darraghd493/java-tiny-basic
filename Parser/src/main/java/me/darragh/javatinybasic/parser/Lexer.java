package me.darragh.javatinybasic.parser;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the tokenisation of the source.
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
