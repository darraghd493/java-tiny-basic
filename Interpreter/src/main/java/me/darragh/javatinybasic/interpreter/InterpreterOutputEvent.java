package me.darragh.javatinybasic.interpreter;

@FunctionalInterface
public interface InterpreterOutputEvent {
    void onOutput(String output);
}
