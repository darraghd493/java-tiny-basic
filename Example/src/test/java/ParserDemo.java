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
