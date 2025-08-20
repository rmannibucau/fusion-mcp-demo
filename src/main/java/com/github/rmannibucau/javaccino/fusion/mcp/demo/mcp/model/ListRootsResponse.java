package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;

import java.util.List;

@JsonModel
public record ListRootsResponse(
        @JsonProperty("_meta") Metadata metadata,
        List<Root> roots
) {
    @JsonModel
    public record Root(
            @JsonProperty("_meta") Metadata metadata,
            String name,
            String uri
    ) {
    }
}
