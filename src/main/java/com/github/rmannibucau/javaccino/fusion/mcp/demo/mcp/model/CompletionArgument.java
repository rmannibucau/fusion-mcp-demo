package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

@JsonModel
public record CompletionArgument(
        String name, String value
) {
}
