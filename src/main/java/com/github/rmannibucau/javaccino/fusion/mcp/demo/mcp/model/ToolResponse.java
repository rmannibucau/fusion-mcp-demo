package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.json.JsonMapper;

import java.util.List;

@JsonModel
public record ToolResponse(
        boolean isError,
        List<Content> content,
        Object structuredContent
) {
    public static ToolResponse structure(final JsonMapper jsonMapper, final Object data) {
        return new ToolResponse(false, List.of(Content.text(jsonMapper.toString(data))), data);
    }
}
