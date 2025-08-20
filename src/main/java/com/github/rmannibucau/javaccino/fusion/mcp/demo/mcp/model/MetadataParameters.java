package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonProperty;

// used by several server -> client requests like roots/list for ex
public record MetadataParameters(@JsonProperty("_meta") Metadata metadata) {
}
