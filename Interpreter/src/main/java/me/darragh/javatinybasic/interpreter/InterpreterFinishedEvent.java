package me.darragh.javatinybasic.interpreter;

/**
 * Represents an event that is triggered when the interpreter finishes executing.
 * This can be used to perform cleanup or notify other components that the
 * execution has completed.
 *
 * @author darraghd493
 * @since 1.0.0
 */
@FunctionalInterface
public interface InterpreterFinishedEvent {
    /**
     * Called when the interpreter has finished executing.
     */
    void onFinished();
}
