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

```java
import me.darragh.javatinybasic.ast.Token;
import me.darragh.javatinybasic.parser.Parser;
import me.darragh.javatinybasic.parser.ParserInvalidLineException;
import me.darragh.javatinybasic.transpiler.Transpiler;

import java.nio.file.Paths;
import java.util.List;

public class TranspilerDemo {
    public static void main(String[] args) throws ParserInvalidLineException {
        List<Token> tokens = Parser.parse("""
            10 LET A = 5
            20 IF A = 5 THEN 50
            30 GOTO 60
            50 PRINT "= PASS"
            51 GOTO 70
            60 PRINT "= FAIL"
            61 GOTO 70
            
            70 LET A = 5
            80 IF A <> 3 THEN 110
            90 GOTO 120
            110 PRINT "<> PASS"
            111 GOTO 130
            120 PRINT "<> FAIL"
            121 GOTO 130
            
            130 LET A = 2
            140 IF A < 3 THEN 170
            150 GOTO 180
            170 PRINT "< PASS"
            171 GOTO 190
            180 PRINT "< FAIL"
            181 GOTO 190
            
            190 LET A = 7
            200 IF A > 3 THEN 230
            210 GOTO 240
            230 PRINT "> PASS"
            231 GOTO 250
            240 PRINT "> FAIL"
            241 GOTO 250
            
            250 LET A = 4
            260 IF A <= 4 THEN 290
            270 GOTO 300
            290 PRINT "<= PASS"
            291 GOTO 310
            300 PRINT "<= FAIL"
            301 GOTO 310
            
            310 LET A = 6
            320 IF A >= 6 THEN 350
            330 GOTO 360
            350 PRINT ">= PASS"
            351 GOTO 370
            360 PRINT ">= FAIL"
            361 GOTO 370
            
            370 FOR I = 1 TO 5 STEP 1
            380 PRINT I
            390 NEXT I
            400 END
            """);
        Transpiler transpiler = new Transpiler(tokens);
        transpiler.toFile(Paths.get("example.jar"));
    }
}
```

[View Example](Example/src/test/java/TranspilerDemo.java)

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
