package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

import java.util.List;

@JsonModel
public record ListToolsResponse(
        List<Tool> tools,
        String nextCursor
) {
    @JsonModel
    public record Tool(
            String name,
            String description,
            JsonSchema inputSchema,
            JsonSchema outputSchema
    ) {
    }
}
