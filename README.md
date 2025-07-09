# java-tiny-basic

A Tiny BASIC parser, interpreter and transpiler (to Java bytecode) written in Java.

**Note:** This project is more-or-less very experimental and does not have no-where near complete support for the Tiny BASIC language. I.e., it's tokenisation process is very basic and not suitable for this language.

# Features:

- Core Tiny BASIC language support
- Parser and interpreter
- Transpiler to Java bytecode (BETA)
  - very rudimentary and not fully functional
  - if statements are prone to breaking due to the contrast in handling of control flow in Java vs Tiny BASIC
- **TODO** Unit tests for parser and interpreter
- **TODO** Example programs (low priority)

# Usage

## Parser

**Note:** The parser is not yet complete and does not support all Tiny BASIC syntax.

```java
import me.darragh.javatinybasic.ast.Token;
import me.darragh.javatinybasic.parser.Parser;
import me.darragh.javatinybasic.parser.ParserInvalidLineException;

import java.util.List;

public class ParserDemo {
    public static void main(String[] args) throws ParserInvalidLineException {
        List<Token> tokens = Parser.parse("""
            10 REM This is a comment
            20 LET A = 5 + 5
            30 LET B = 10
            40 PRINT "A + B =", A + B
            50 INPUT C
            60 PRINT C
            70 END
            """);
        for (Token token : tokens) {
            System.out.printf("%s\t%s\t%s%n", token.lineNumber(), token.statement(), token.expression());
        }
    }
}
```

[View Example](Example/src/test/java/ParserDemo.java)

## Interpreter

```java
import me.darragh.javatinybasic.ast.Token;
import me.darragh.javatinybasic.interpreter.Interpreter;
import me.darragh.javatinybasic.parser.Parser;
import me.darragh.javatinybasic.parser.ParserInvalidLineException;

import java.util.List;
import java.util.Scanner;

public class InterpreterDemo {
    public static void main(String[] args) throws ParserInvalidLineException {
        Scanner scanner = new Scanner(System.in);
        List<Token> tokens = Parser.parse("""
                10 LET A = 5
                20 LET B = 10
                30 PRINT "Initial A and B:", A, B
                40 INPUT C
                50 IF C > B THEN 80
                60 PRINT "C is less or equal to B"
                70 GOTO 90
                80 PRINT "C is greater than B"
                90 FOR I = 1 TO 3 STEP 1
                100 PRINT "Loop iteration:", I
                110 NEXT I
                120 GOSUB 150
                130 PRINT "Back from subroutine"
                140 END
                150 PRINT "In subroutine"
                160 RETURN
            """);
        Interpreter interpreter = new Interpreter(tokens,
                /* input */ scanner::nextInt,
                /* output */ System.out::println,
                /* finished */ () -> System.out.println("Finished!")
        );
        interpreter.run();
        scanner.close();
    }
}
```

[View Example](Example/src/test/java/InterpreterDemo.java)

## Transpiler

**TODO**

# Syntax

```
{line number}   REM {comment (ignored line)}
{line number}   LET {variable} = {expression (current value of variable|literal number|arithmetic expression)}
{line number}   PRINT {expression (current value of variable|literal number|arithmetic expression|"string")}, {...}
{line number}   INPUT {variable}
{line number}   IF {expression (current value of variable|literal number|arithmetic expression)} {comparison operator} {expression (current value of variable|literal number|arithmetic expression)} THEN {line number}
{line number}   FOR {variable} = {expression (current value of variable|literal number|arithmetic expression)} TO {expression (current value of variable|literal number)} STEP {expression (current value of variable|literal number)}
{line number}   NEXT {variable}
{line number}   GOTO {line number}
{line number}   GOSUB {line number}
{line number}   RETURN (exits out of the GOSUB)
{line number}   END
```

This attempts to be faithful to the [original Tiny BASIC syntax](http://tinybasic.cyningstan.org.uk/page/12/tiny-basic-manual), but may not support all features or edge cases. The parser is designed to be simple and easy to understand, so it may not handle all possible syntax errors gracefully.

## Comparison Operators

```
=   EQUAL
<>  NOT_EQUAL
<   LESS_THAN
>   GREATER_THAN
<=  LESS_THAN_OR_EQUAL
>=  GREATER_THAN_OR_EQUAL
```
