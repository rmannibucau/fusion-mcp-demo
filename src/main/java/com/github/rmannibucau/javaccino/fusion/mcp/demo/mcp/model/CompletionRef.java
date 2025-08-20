package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

@JsonModel
public record CompletionRef(
        String type, // ref/prompt or ref/resource
        String name, // ref/prompt
        String title, // ref/prompt
        String uri // ref/resource
) {
}
