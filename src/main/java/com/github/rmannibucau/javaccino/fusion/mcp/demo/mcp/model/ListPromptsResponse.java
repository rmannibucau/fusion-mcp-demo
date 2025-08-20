package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;

import java.util.List;

@JsonModel
public record ListPromptsResponse(
        List<Prompt> prompts,
        String nextCursor
) {
    @JsonModel
    public record Prompt(
            @JsonProperty("_meta") Metadata metadata,
            String title,
            String name,
            String description,
            List<Argument> arguments
    ) {
        @JsonModel
        public record Argument(
                String title,
                String name,
                String description,
                Boolean required
        ) {
        }
    }
}
