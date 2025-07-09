package me.darragh.javatinybasic.interpreter;

@FunctionalInterface
public interface InterpreterFinishedEvent {
    void onFinished();
}
