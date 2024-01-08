package com.uthef.expression_parser;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Namespace {
    protected final Map<String, Double> variables = new HashMap<>();
    protected final Map<String, Function> functions = new HashMap<>();

    public void putVariable(String name, double value) {
        if (isInvalidIdentifier(name)) throw new InvalidIdentifierException(name);

        variables.put(name, value);
    }

    public void putFunction(String name, Function function) {
        if (isInvalidIdentifier(name)) throw new InvalidIdentifierException(name);

        functions.put(name, function);
    }

    public boolean removeVariable(String name) {
        return variables.remove(name) != null;
    }

    public boolean removeFunction(String name) {
        return functions.remove(name) != null;
    }

    @Nullable
    public Function getFunction(String name) {
        return functions.get(name);
    }

    @Nullable
    public Double getVariable(String name) {
        return variables.get(name);
    }

    public void clearFunctions() {
        functions.clear();
    }

    public void clearVariables() {
        variables.clear();
    }

    public void clear() {
        functions.clear();
        variables.clear();
    }

    public int countVariables() {
        return variables.size();
    }

    public int countFunctions() {
        return functions.size();
    }

    public static boolean isInvalidIdentifier(String identifier) {
        if (identifier.isEmpty()) return true;

        char firstChar = identifier.charAt(0);

        if (Character.isWhitespace(firstChar) || !Character.isLetter(firstChar)) return true;

        return identifier.chars().anyMatch(
                c -> Character.isWhitespace(c) || (!Character.isDigit(c) && !Character.isLetter(c))
        );
    }
}