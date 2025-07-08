package me.darragh.javatinybasic.parser;

import jdk.jshell.spi.ExecutionControl;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.darragh.javatinybasic.ast.Token;
import me.darragh.javatinybasic.ast.langauge.LStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parses the source of a Tiny BASIC program into a list of tokens.
 */
@Data
@RequiredArgsConstructor
public class Parser {
    private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern ENTIRE_LINE_COMMENT_PATTERN = Pattern.compile("^\"[^\"\\r\\n]*\"$");
    private static final Pattern POSITIVE_NUMBER_PATTERN = Pattern.compile("^\\d+");

    public static final String CHARACTER_START_END_CHAR = "\"";

    private final @NotNull String source;
    private final @NotNull List<Token> tokens = new ArrayList<>();

    public void parse() throws ParserInvalidLineException {
        List<String> lines = List.of(source.split("\n"));
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() // skip empty lines
                    || line.matches(ENTIRE_LINE_COMMENT_PATTERN.pattern()) // skip *entire* comment lines
            ) {
                continue;
            }
            this.tokens.add(parseLine(line));
        }
    }

    /**
     * Parses a single line of Tiny BASIC code into a Token.
     *
     * @param line The line to parse.
     * @return A Token representing the parsed line.
     * @throws ParserInvalidLineException If the line is invalid or cannot be parsed.
     */
    public static @NotNull Token parseLine(@NotNull String line) throws ParserInvalidLineException {
        String tokens = MULTI_SPACE_PATTERN.matcher(line).replaceAll(" ").trim();
        String[] parts = tokens.split(" ");
        if (parts.length == 0) {
            throw ParserInvalidLineException.create("Line cannot be empty: ", line);
        }

        // Fetch line number
        String lineNumberStr = parts[0];
        if (!lineNumberStr.matches(POSITIVE_NUMBER_PATTERN.pattern())) {
            throw ParserInvalidLineException.create("Line number must be a positive integer: ", line);
        }

        int lineNumber;
        try {
            lineNumber = Integer.parseInt(lineNumberStr);
        } catch (NumberFormatException e) {
            throw ParserInvalidLineException.create("Line number must be a positive integer: ", line, e);
        }

        if (lineNumber < 1) {
            throw ParserInvalidLineException.create("Line number must be a positive integer: ", line);
        }

        // Identify the statement type
        LStatement statement = LStatement.fromToken(parts[1]);
        // TODO: Finish
        //noinspection DataFlowIssue
        return null;
    }
}
