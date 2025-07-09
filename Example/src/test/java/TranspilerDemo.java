import me.darragh.javatinybasic.ast.Token;
import me.darragh.javatinybasic.parser.Parser;
import me.darragh.javatinybasic.parser.ParserInvalidLineException;
import me.darragh.javatinybasic.transpiler.Transpiler;

import java.nio.file.Paths;
import java.util.List;

/**
 * An example demonstrating how to use the Java Tiny Basic transpiler to transpile a block of code into a JAR file.
 *
 * @author darraghd493
 * @since 1.0.0
 */
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
