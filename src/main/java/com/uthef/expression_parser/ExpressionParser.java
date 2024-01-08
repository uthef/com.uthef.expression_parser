package com.uthef.expression_parser;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static com.uthef.expression_parser.ExpressionParserUtils.*;

public class ExpressionParser {
    private Namespace namespace;
    private final HashMap<Character, Operation> operatorMap = getOperatorMap();
    public ExpressionParser() {
        namespace = new Namespace();
    }

    public double evaluate(String expression) throws ParsingException{
        AtomicInteger cursor = new AtomicInteger(0);
        return evaluate(expression, cursor);
    }

    private double evaluate(String expression, AtomicInteger cursor) throws ParsingException {
        List<Double> numbers = new ArrayList<>(expression.length() - cursor.get());
        List<OperatorState> states = new ArrayList<>((expression.length() - cursor.get()) / 2);

        int index = cursor.get(), start = index;

        boolean negateFirst = false,
                missingParenthesis = index > 0,
                expectation = false;

        for (; index < expression.length(); index = cursor.incrementAndGet() ) {
            char c = expression.charAt(index);

            if (Character.isWhitespace(c)) continue;

            if (isArabicDigit(c) || c == '.') {
                if (expectation) throw new ParsingException("An operator is expected", index);

                double number = parseNumber(expression, cursor);

                addNumberAndPrecalculate(number, numbers, states, negateFirst);
                expectation = true;

                continue;
            }

            if (numbers.isEmpty() && !negateFirst && c == '-') {
                negateFirst = true;
                continue;
            }

            if (Character.isLetter(c)) {
                if (expectation) throw new ParsingException("An operator is expected", index);

                addNumberAndPrecalculate(parseName(expression, cursor), numbers, states, negateFirst);
                expectation = true;

                continue;
            }

            if (c == ')') {
                if (!missingParenthesis) throw new ParsingException("Unexpected closing parenthesis", index);
                missingParenthesis = false;
                break;
            }

            if (c == '(') {
                if (expectation) throw new ParsingException("An operator is expected", index);

                cursor.incrementAndGet();
                double number = evaluate(expression, cursor);

                addNumberAndPrecalculate(number, numbers, states, negateFirst);

                expectation = true;
                continue;
            }

            Operation operation = operatorMap.get(c);

            if (operation != null) {
                if (!expectation) throw new ParsingException("An operand is expected", index);

                states.add(new OperatorState(numbers.size() - 1, c, index, operation));
                expectation = false;
                continue;
            }

            throw new ParsingException(String.format("Unexpected character \"%s\"", c), index);
        }

        if (missingParenthesis)
            throw new ParsingException("Parenthesis is never closed", start - 1);
        if (negateFirst && numbers.isEmpty())
            throw new ParsingException("Operator lacks both operands", cursor.get()- 1);

        while (!states.isEmpty()) {
            OperatorState state = states.remove(0);

            if (state.leftIndex == -1)
                throw new ParsingException("Operator lacks left operand", state.position);
            if (state.rightIndex == -1)
                throw new ParsingException("Operator lacks right operand", state.position);

            double value = state.operation.perform(numbers.get(state.leftIndex), numbers.get(state.rightIndex));
            numbers.set(state.rightIndex, value);
        }

        return numbers.isEmpty() ? 0 : numbers.get(numbers.size() - 1);
    }

    private double parseName(String expression, AtomicInteger cursor) throws ParsingException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(expression.charAt(cursor.get()));

        int index = cursor.incrementAndGet(), start = index - 1, pIndex = -1;

        for (; index < expression.length(); index = cursor.incrementAndGet()) {
            char c = expression.charAt(index);

            if (c == '(') {
                pIndex = index;
                break;
            }

            if (!Character.isLetter(c) && !Character.isDigit(c)) break;

            stringBuilder.append(c);
        }

        String name = stringBuilder.toString();

        if (pIndex != -1) {
            cursor.incrementAndGet();

            double value = evaluate(expression, cursor);

            Function function = namespace.getFunction(name);

            if (function == null) {
                throw new ParsingException(String.format("Unknown function \"%s\"", name), start);
            }

            return function.call(value);
        }

        cursor.decrementAndGet();

        Double value = namespace.getVariable(name);

        if (value == null) {
            throw new ParsingException(String.format("Unknown variable \"%s\"", name), start);
        }

        return value;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {

        if (namespace == null) {
            namespace = new Namespace();
        }

        this.namespace = namespace;
    }
}