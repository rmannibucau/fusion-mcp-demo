package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

import java.util.List;

@JsonModel
public record ListPromptsResponse(
        List<Prompt> prompts,
        String nextCursor
) {
    @JsonModel
    public record Prompt(
            String name,
            String description,
            List<Argument> arguments
    ) {
        @JsonModel
        public record Argument(
                String name,
                String description,
                Boolean required
        ) {
        }
    }
}
