package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

import java.util.Map;

@JsonModel
public record CompletionContext(Map<String, String> arguments) {
}
