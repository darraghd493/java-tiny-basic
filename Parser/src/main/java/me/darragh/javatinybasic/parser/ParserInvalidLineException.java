package me.darragh.javatinybasic.parser;

public class ParserInvalidLineException extends Exception {
    private ParserInvalidLineException(String message, String line) {
        super(message + " " + line);
    }

    public static ParserInvalidLineException create(String message, String line) {
        return new ParserInvalidLineException(message, line);
    }

    public static ParserInvalidLineException create(String message, String line, Throwable cause) {
        ParserInvalidLineException exception = new ParserInvalidLineException(message, line);
        exception.initCause(cause);
        return exception;
    }
}