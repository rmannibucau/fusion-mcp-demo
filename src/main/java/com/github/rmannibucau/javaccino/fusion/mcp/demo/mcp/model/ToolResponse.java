package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;
import io.yupiik.fusion.json.JsonMapper;

import java.util.List;

@JsonModel
public record ToolResponse(
        @JsonProperty("_meta") Metadata metadata,
        boolean isError,
        List<Content> content,
        Object structuredContent // must be a JSON object
) {
    public static ToolResponse structure(final JsonMapper jsonMapper, final Object data) {
        return new ToolResponse(null, false, List.of(Content.text(jsonMapper.toString(data))), data);
    }
}
