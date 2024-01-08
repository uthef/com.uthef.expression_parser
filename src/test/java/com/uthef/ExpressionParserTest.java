package com.uthef;

import com.uthef.expression_parser.*;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionParserTest {

    ExpressionParser expParser = new ExpressionParser();
    
    @Test
    public void parenthesisAndDifferentStylesOfNumbers() throws ParsingException {
        double result  = expParser.evaluate("30 - 5. * (02.5 + (2.000 + .5)) + 0,000");
        assertEquals(5.0, result);
    }

    @Test
    public void lowPriorityOperators() throws ParsingException {
        double result = expParser.evaluate("30 + 30 + 2 + 0.8 - 1");
        assertEquals(61.8, result);
    }

    @Test
    public void highPriorityOperators() throws ParsingException {
        double result = expParser.evaluate("((20 * 5)) / 2 - 4 / 2 * 1.");
        assertEquals(48.0, result);
    }

    @Test
    public void multipleNegations() throws ParsingException {
        double result = expParser.evaluate("-(-(-50))+(-(-6/2))");
        assertEquals(-47.0, result);
    }

    @Test
    public void variable() throws ParsingException {
        expParser.getNamespace().putVariable("PI", Math.PI);

        double result = expParser.evaluate("PI * 2");
        assertEquals(Math.PI * 2, result);

        expParser.getNamespace().clear();
    }

    @Test
    public void functions() throws ParsingException {
        expParser.getNamespace().putVariable("x", 12);

        expParser.getNamespace().putFunction("neg", x -> -x);
        expParser.getNamespace().putFunction("abs", Math::abs);

        double result = expParser.evaluate("1 + x * abs(neg(x))");
        assertEquals(145.0, result);

        expParser.getNamespace().clear();
    }

    @Test
    public void functionAndVariables() throws ParsingException {
        expParser.getNamespace().putVariable("x", 48.0);
        expParser.getNamespace().putFunction("sqrt", Math::sqrt);



        double result = expParser.evaluate("(x + 1) / (sqrt(x + 1) + (sqrt((x + 1) / 1.0)))");
        assertEquals(3.5, result);

        expParser.getNamespace().clear();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-50 + +",
            "+8",
            "10 * 2 + (300",
            "12abcd",
            "50 * 5 - )",
            "-",
            "-2332 + -5",
            "1 + .2." })
    public void exceptionCase(String expression) {
        assertThrows(ParsingException.class, () -> expParser.evaluate(expression));
    }

    @Test
    public void setNamespace() {
        Namespace namespace = new Namespace();
        expParser.setNamespace(namespace);

        assertEquals(namespace, expParser.getNamespace());
    }

    @Test
    public void setNullNamespace() {
        expParser.setNamespace(null);
        assertNotEquals(null, expParser.getNamespace());
    }
}
