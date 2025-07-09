import me.darragh.javatinybasic.ast.Token;
import me.darragh.javatinybasic.interpreter.Interpreter;
import me.darragh.javatinybasic.parser.Parser;
import me.darragh.javatinybasic.parser.ParserInvalidLineException;

import java.util.List;
import java.util.Scanner;

/**
 * An example demonstrating how to use the Java Tiny Basic interpreter to run a block of code.
 *
 * @author darraghd493
 * @since 1.0.0
 */
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
