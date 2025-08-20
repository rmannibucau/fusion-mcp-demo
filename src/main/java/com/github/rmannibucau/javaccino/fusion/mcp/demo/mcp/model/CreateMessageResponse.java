package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;

@JsonModel
public record CreateMessageResponse(
        @JsonProperty("_meta") Metadata metadata,
        Content content,
        String model,
        Role role,
        String stopReason
) {
}
