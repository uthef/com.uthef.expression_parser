package com.uthef.expression_parser;

class OperatorState {
    final int leftIndex;
    int rightIndex;
    final int position;
    final char character;
    final Operation operation;

    OperatorState(int leftIndex, char character, int position, Operation operation) {
        this.leftIndex = leftIndex;
        this.character = character;
        this.rightIndex = -1;
        this.position = position;
        this.operation = operation;
    }
}
