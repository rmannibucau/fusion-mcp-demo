package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

import java.time.OffsetDateTime;
import java.util.List;

@JsonModel
public record Annotations(
        List<Role> audience,
        OffsetDateTime lastModified,
        Integer priority // min=0, max=1
) {
    public static int MOST_IMPORTANT_PRIOTITY = 1;
    public static int LEAST_IMPORTANT_PRIOTITY = 0;
}
