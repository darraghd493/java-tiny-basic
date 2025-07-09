import me.darragh.javatinybasic.ast.Token;
import me.darragh.javatinybasic.interpreter.Interpreter;
import me.darragh.javatinybasic.parser.Parser;
import me.darragh.javatinybasic.parser.ParserInvalidLineException;

import java.util.List;
import java.util.Scanner;

public class TimedInterpreterDemo {
    public static void main(String[] args) throws ParserInvalidLineException {
        long parserStart =  System.currentTimeMillis();
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
        long parserEnd =  System.currentTimeMillis();

        Interpreter interpreter = new Interpreter(tokens,
                /* input */ () -> 3,
                /* output */ (output) -> {},
                /* finished */ () -> {}
        );
        long interpreterStart = System.currentTimeMillis();
        interpreter.run();
        long interpreterEnd =  System.currentTimeMillis();

        System.out.printf("Parser time taken: %sms%n", parserEnd - parserStart);
        System.out.printf("Interpreter time taken: %sms%n", interpreterEnd - interpreterStart);
    }
}
