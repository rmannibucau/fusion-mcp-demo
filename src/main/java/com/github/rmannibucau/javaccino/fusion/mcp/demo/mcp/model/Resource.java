package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;

import java.util.Base64;

@JsonModel
public record Resource(
        @JsonProperty("_meta") Metadata metadata,
        String uri, String mimeType,
        String text,
        String blob // base64, required
) {
    public static Resource text(final Metadata metadata, final String uri, final String mimeType, final String text) {
        return new Resource(metadata, uri, mimeType, text, null);
    }

    public static Resource blob(final Metadata metadata, final String uri, final String mimeType, final byte[] content) {
        return new Resource(metadata, uri, mimeType, null, Base64.getEncoder().encodeToString(content));
    }
}
