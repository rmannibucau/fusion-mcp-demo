package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonOthers;

import java.util.Map;

@JsonModel
public record Metadata(
        String name,
        String title,
        @JsonOthers Map<String, Object> others
) {
}
