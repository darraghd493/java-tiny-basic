package me.darragh.javatinybasic.parser;

/**
 * A generic exception for invalid lines in the {@link Parser}.
 * This exception is thrown when a line in the source code does not conform to the expected format or syntax.
 *
 * @author darraghd493
 * @since 1.0.0
 */
public class ParserInvalidLineException extends Exception {
    private ParserInvalidLineException(String message, String line) {
        super(message + " " + line);
    }

    /**
     * Creates a new instance of {@link ParserInvalidLineException} with the specified message and line.
     *
     * @param message The error message describing the invalid line.
     * @param line The line/content that caused the exception.
     * @return A new instance of {@link ParserInvalidLineException}.
     */
    public static ParserInvalidLineException create(String message, String line) {
        return new ParserInvalidLineException(message, line);
    }

    /**
     * Creates a new instance of {@link ParserInvalidLineException} with the specified message, line, and cause.
     *
     * @param message The error message describing the invalid line.
     * @param line The line/content that caused the exception.
     * @param cause The underlying cause of the exception.
     * @return A new instance of {@link ParserInvalidLineException}.
     */
    public static ParserInvalidLineException create(String message, String line, Throwable cause) {
        ParserInvalidLineException exception = new ParserInvalidLineException(message, line);
        exception.initCause(cause);
        return exception;
    }
}