package me.darragh.javatinybasic.interpreter;

@FunctionalInterface
public interface InterpreterOutputEvent {
    void output(String output);
}
