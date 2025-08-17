package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.api;

import io.yupiik.fusion.framework.build.api.metadata.BeanMetadataAlias;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@BeanMetadataAlias(name = "mcp.type", value = "prompt")
public @interface MCPPrompt {
}
