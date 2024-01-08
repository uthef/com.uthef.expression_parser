package com.uthef;

import com.uthef.expression_parser.Namespace;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class NamespaceTest {
    @ParameterizedTest
    @ValueSource(strings = {  "name", "X", "var1" })
    public void validIdentifiers(String identifier) {
        assertFalse(Namespace.isInvalidIdentifier(identifier));
    }

    @ParameterizedTest
    @ValueSource(strings = {  "  ", " a", "a ", "10a", "+d", "a.(" })
    public void invalidIdentifiers(String identifier) {
        assertTrue(Namespace.isInvalidIdentifier(identifier));
    }
}
