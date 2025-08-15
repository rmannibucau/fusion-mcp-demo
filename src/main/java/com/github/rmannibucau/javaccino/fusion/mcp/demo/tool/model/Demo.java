package com.github.rmannibucau.javaccino.fusion.mcp.demo.tool.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

@JsonModel
public record Demo(
        String greeting
) {
}
