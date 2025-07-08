package me.darragh.javatinybasic.interpreter;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.darragh.javatinybasic.ast.Token;

import java.util.List;

/**
 * Interprets tokenised Tiny BASIC code.
 */
@Data
@RequiredArgsConstructor
public class Interpreter {
    private final List<Token> tokens;


    public void interpret() {

    }
}
