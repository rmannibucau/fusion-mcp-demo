package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

import java.util.Map;

@JsonModel
public record InitializeResponse(
        String protocolVersion,
        Capabilities capabilities,
        ServerInfo serverInfo,
        String instructions
) {
    @JsonModel
    public record Capabilities(
            Map<String, Object> logging,
            Prompts prompts,
            Resources resources,
            Tools tools,
            Map<String, Object> completions,
            Map<String, Object> experimental
    ) {
    }

    @JsonModel
    public record Prompts(
            boolean listChanged
    ) {
    }

    @JsonModel
    public record Resources(
            boolean subscribe,
            boolean listChanged
    ) {
    }

    @JsonModel
    public record Tools(
            boolean listChanged
    ) {
    }

    @JsonModel
    public record ServerInfo(
            String name,
            String title,
            String version
    ) {
    }
}
