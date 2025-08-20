package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

import java.util.List;

@JsonModel
public record ModelPreferences(
        Integer costPriority,
        List<ModelHint> hints,
        Integer intelligencePriority,
        Integer speedPriority
) {
    public static final int NOT_IMPORTANT_COST = 0;
    public static final int MOST_IMPORTANT_COST = 1;

    public static final int NOT_IMPORTANT_INTELLIGENCE = 0;
    public static final int MOST_IMPORTANT_INTELLIGENCE = 1;

    public static final int NOT_IMPORTANT_SPEED = 0;
    public static final int MOST_IMPORTANT_SPEED = 1;
}
