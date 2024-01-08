package com.uthef.expression_parser;

public class ParsingException extends Exception {
    public final int position;
    ParsingException(String message) {
        super(message);
        this.position = -1;
    }

    ParsingException(String message, int position) {
        super(message);
        this.position = position;
    }
}
