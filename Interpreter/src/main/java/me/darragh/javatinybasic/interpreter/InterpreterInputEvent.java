package me.darragh.javatinybasic.interpreter;

/**
 * Represents an event that is triggered when the interpreter requires input.
 * This can be used to provide input to the interpreter during its execution.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@FunctionalInterface
public interface InterpreterInputEvent {
    /**
     * Handles the input request from the interpreter.
     *
     * @return The input value provided by the user or system.
     */
    int getInput();
}
