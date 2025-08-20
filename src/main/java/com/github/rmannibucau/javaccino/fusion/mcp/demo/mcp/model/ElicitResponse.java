package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;

import java.util.Map;

@JsonModel
public record ElicitResponse(
        @JsonProperty("_meta") Metadata metadata,
        Action action,
        Map<String, Object> content
) {
    @JsonModel
    public enum Action {
        accept, cancel, decline
    }
}
