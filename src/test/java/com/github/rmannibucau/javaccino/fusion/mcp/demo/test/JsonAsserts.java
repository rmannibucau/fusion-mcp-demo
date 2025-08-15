package com.github.rmannibucau.javaccino.fusion.mcp.demo.test;

import io.yupiik.fusion.json.internal.JsonMapperImpl;
import io.yupiik.fusion.json.pretty.PrettyJsonMapper;
import org.opentest4j.AssertionFailedError;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class JsonAsserts {
    private JsonAsserts() {
        // no-op
    }

    // avoid issues with random attributes order
    public static void assertJsonEquals(final String expected, final String actual) {
        try (final var mapper = new JsonMapperImpl(List.of(), key -> Optional.empty())) {
            try {
                assertEquals(mapper.fromString(Object.class, expected), mapper.fromString(Object.class, actual));
            } catch (final AssertionFailedError afe) {
                final var prettifier = new PrettyJsonMapper(mapper);
                try {
                    assertEquals(  // nicer to read
                            prettifier.toString(prettifier.fromString(Object.class, expected)),
                            prettifier.toString(prettifier.fromString(Object.class, actual)));
                } catch (final AssertionFailedError afe2) {
                    throw afe2; // the one we want
                } catch (final RuntimeException re) { // something failed - maybe not even a json, just rethrow
                    throw afe;
                }
            }
        }
    }
}
