package me.darragh.javatinybasic.ast.expression.statement;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.darragh.javatinybasic.ast.expression.Expression;
import me.darragh.javatinybasic.ast.expression.ValueExpression;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static me.darragh.javatinybasic.ast.Serials.LET_STATEMENT_EXPRESSION_SERIAL_VERSION_UID;

/**
 * Represents an LET expression, which consists of a variable name and the value.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class LETExpression extends Expression {
    @Serial
    private static final long serialVersionUID = LET_STATEMENT_EXPRESSION_SERIAL_VERSION_UID;

    private final @NotNull String variableName;
    private final @NotNull ValueExpression value;
}
