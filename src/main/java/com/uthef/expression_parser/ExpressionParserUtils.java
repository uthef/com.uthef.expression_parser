package com.uthef.expression_parser;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ExpressionParserUtils {
    static void addNumberAndPrecalculate(double number,
                                                 List<Double> numbers,
                                                 List<OperatorState> operations,
                                                 boolean negateFirst) throws ParsingException {
        if (numbers.isEmpty() && negateFirst)
            number = -number;

        numbers.add(number);

        if (numbers.size() == 1) return;
        if (operations.isEmpty()) throw new ParsingException("Operator missing");

        OperatorState lastOp = operations.get(operations.size() - 1);

        if (lastOp.leftIndex == -1) throw new ParsingException("Left operand missing");
        if (lastOp.rightIndex == -1) {
            lastOp.rightIndex = numbers.size() - 1;
        }
        if (!lastOp.operation.isPrioritized()) return;

        double value = lastOp.operation.perform(numbers.get(lastOp.leftIndex), numbers.get(lastOp.rightIndex));

        numbers.set(lastOp.leftIndex, value);
        numbers.remove(numbers.size() - 1);
        operations.remove(operations.size() - 1);
    }

   static double parseNumber(String expression, AtomicInteger cursor) throws ParsingException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(expression.charAt(cursor.get()));

        boolean metExp = false, metExpOp = false;

        int index = cursor.incrementAndGet(), start = index - 1;

        for (; index < expression.length(); index = cursor.incrementAndGet()) {
            char c = expression.charAt(index);

            if (c == ',') continue;

            if (c == 'e' && !metExp) metExp = true;
            else if (metExp && !metExpOp && (c == '+' || c == '-')) metExpOp = true;
            else if (!isArabicDigit(c) && c != '.' ) break;

            stringBuilder.append(c);
        }

        cursor.decrementAndGet();

        double number;

        String numberStr = stringBuilder.toString();

        try {
            number = Double.parseDouble(numberStr);
        }
        catch (NumberFormatException e) {
            throw new ParsingException(String.format("Badly formatted number \"%s\"", numberStr), start);
        }

        return number;
    }

    static HashMap<Character, Operation> getOperatorMap() {
        return new HashMap<>() {{
            put('+',  new Operation() {
                @Override
                public boolean isPrioritized() {
                    return false;
                }

                @Override
                public double perform(double a, double b) {
                    return a + b;
                }
            });
            put('-', new Operation() {
                @Override
                public boolean isPrioritized() {
                    return false;
                }

                @Override
                public double perform(double a, double b) {
                    return a - b;
                }
            });
            put('*', new Operation() {
                @Override
                public boolean isPrioritized() {
                    return true;
                }

                @Override
                public double perform(double a, double b) {
                    return a * b;
                }
            });
            put('/', new Operation() {
                @Override
                public boolean isPrioritized() {
                    return true;
                }

                @Override
                public double perform(double a, double b) {
                    return a / b;
                }
            });
        }};
    }

    static boolean isArabicDigit(char c) {
        return c > 47 && c < 58;
    }
}
