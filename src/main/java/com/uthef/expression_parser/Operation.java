package com.uthef.expression_parser;

public interface Operation {
    boolean isPrioritized();
    double perform(double a, double b);
}
