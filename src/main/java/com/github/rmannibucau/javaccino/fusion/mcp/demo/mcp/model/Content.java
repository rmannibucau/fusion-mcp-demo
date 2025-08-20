package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;
import io.yupiik.fusion.framework.build.api.json.JsonProperty;

import java.util.Base64;

@JsonModel
public record Content(
        @JsonProperty("_meta") Metadata metadata,
        Annotations annotations,
        Type type,
        String text,
        String data, // base64
        String mimetype,
        Resource resource) {
    @JsonModel
    public enum Type {
        text, image, audio, resource
    }

    public static Content text(final String text) {
        return new Content(null, null, Type.text, text, null, null, null);
    }

    public static Content image(final String mimeType, final byte[] content) {
        return new Content(null, null, Type.image, null, Base64.getEncoder().encodeToString(content), mimeType, null);
    }

    public static Content audio(final String mimeType, final byte[] content) {
        return new Content(null, null, Type.audio, null, Base64.getEncoder().encodeToString(content), mimeType, null);
    }

    public static Content resource(final Resource resource) {
        return new Content(null, null, Type.resource, null, null, null, resource);
    }
}