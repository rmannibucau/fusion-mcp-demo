package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

import java.util.Base64;

@JsonModel
public record Content(
        String type,
        String text,
        String data,
        String mimetype,
        Resource resource) {
    public static Content text(final String text) {
        return new Content("text", text, null, null, null);
    }

    public static Content image(final String mimeType, final byte[] content) {
        return new Content("image", null, Base64.getEncoder().encodeToString(content), mimeType, null);
    }

    public static Content audio(final String mimeType, final byte[] content) {
        return new Content("audio", null, Base64.getEncoder().encodeToString(content), mimeType, null);
    }

    public static Content resource(final Resource resource) {
        return new Content("resource", null, null, null, resource);
    }
}