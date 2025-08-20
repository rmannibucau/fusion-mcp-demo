package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

import java.util.Map;

// note: this is not complete, more for demo purposes
@JsonModel
public record Capabilities(
        Roots roots,
        Map<String, Object> sampling,
        Map<String, Object> elicitation,
        Map<String, Object> experimental
) {
    @JsonModel
    public record Roots(
            boolean listChanged
    ) {
    }
}