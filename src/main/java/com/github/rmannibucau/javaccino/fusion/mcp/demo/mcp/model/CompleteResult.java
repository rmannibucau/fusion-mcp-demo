package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;

import java.util.List;

@JsonModel
public record CompleteResult(
        @JsonProperty("_meta") Metadata metadata,
        Completion completion
) {
    @JsonModel
    public record Completion(
            boolean hasMore,
            int total,
            List<String> values
    ) {
    }
}
