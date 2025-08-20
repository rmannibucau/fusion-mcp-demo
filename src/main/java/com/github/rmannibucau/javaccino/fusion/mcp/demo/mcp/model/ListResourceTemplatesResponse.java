package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;
import io.yupiik.fusion.framework.build.api.jsonrpc.JsonRpcParam;

import java.util.List;

@JsonModel
public record ListResourceTemplatesResponse(
        List<ResourceTemplate> resources,
        String nextCursor
) {
    @JsonModel
    public record ResourceTemplate(
            @JsonProperty("_meta") Metadata metadata,
            Annotations annotations,
            String description,
            String mimeType,
            String name,
            String title,
            String uriTemplate
    ) {}
}
