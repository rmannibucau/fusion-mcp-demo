package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;

import java.util.List;
import java.util.Map;

// simplified JSON-Schema model for MCP - should be enhanced if set in a lib
@JsonModel
public record JsonSchema(
        String type,
        Boolean nullable,
        String description,
        String format,
        String pattern,
        Map<String, JsonSchema> properties,
        Object additionalProperties,
        JsonSchema items,
        @JsonProperty("enum") List<String> enumeration,
        List<String> required) {
    // generic primitive
    public JsonSchema(final String type, final Boolean nullable, final String description) {
        this(type, nullable, description, null, null, null, null, null, null, null);
    }

    // string
    public JsonSchema(final Boolean nullable, final String description,
                      final String format, final String pattern) {
        this("string", nullable, description, format, pattern, null, null, null, null, null);
    }

    // enumeration
    public JsonSchema(final Boolean nullable, final String description,
                      final String format, final String pattern, final List<String> enumeration) {
        this("string", nullable, description, format, pattern, null, null, null, enumeration, null);
    }

    // object
    public JsonSchema(final Boolean nullable, final String description,
                      final Map<String, JsonSchema> properties, final Object additionalProperties,
                      final List<String> required) {
        this("object", nullable, description, null, null, properties, additionalProperties, null, null, required);
    }

    public JsonSchema(final Boolean nullable, final String description, final Map<String, JsonSchema> properties, final List<String> required) {
        this("object", nullable, description, null, null, properties, null, null, null, required);
    }

    // array
    public JsonSchema(final Boolean nullable, final String description, final JsonSchema items) {
        this("array", nullable, description, null, null, null, null, items, null, null);
    }
}
