# java-tiny-basic

A Tiny BASIC parser, interpreter and transpiler (to Java bytecode) written in Java.

# Features:

- Core Tiny BASIC language support
- Parser and **TODO** interpreter
- **TODO** Transpiler to Java bytecode
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
            60 IF C > 0 THEN 80
            70 GOTO 100
            80 FOR I = 1 TO C STEP 2
            90 PRINT I
            100 NEXT I
            110 GOSUB 20
            120 GOSUB 30
            130 END
            """);
        for (Token token : tokens) {
            System.out.printf("%s\t%s\t%s%n", token.lineNumber(), token.statement(), token.expression());
        }
    }
}
```

[View Example](Example/src/test/java/ParserDemo.java)

**TODO**

## Interpreter

**TODO**

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
