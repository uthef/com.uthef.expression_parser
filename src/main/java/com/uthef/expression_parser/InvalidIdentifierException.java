package com.uthef.expression_parser;

public class InvalidIdentifierException extends RuntimeException {
    InvalidIdentifierException(String identifier) {
        super(String.format("\"%s\" is not a valid identifier", identifier));
    }
}
