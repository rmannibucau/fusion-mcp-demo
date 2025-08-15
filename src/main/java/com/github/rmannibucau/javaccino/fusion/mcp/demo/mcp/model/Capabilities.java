package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

// note: this is not complete, more for demo purposes
@JsonModel
public record Capabilities(
        Roots roots,
        Sampling sampling,
        Elicitation elicitation
) {
    @JsonModel
    public record Roots(
            boolean listChanged
    ) {
    }

    @JsonModel
    public record Sampling() {
    }

    @JsonModel
    public record Elicitation() {
    }
}