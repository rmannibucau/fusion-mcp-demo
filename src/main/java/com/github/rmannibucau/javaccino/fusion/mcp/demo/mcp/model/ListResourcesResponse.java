package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;

import java.util.List;

@JsonModel
public record ListResourcesResponse(
        List<Resource> resources,
        String nextCursor
) {
    @JsonModel
    public record Resource(
            @JsonProperty("_meta") Metadata metadata,
            Annotations annotations,
            String description,
            String mimeType,
            String name,
            String title,
            String uri,
            Long size
    ) {}
}
