package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

import java.util.List;

@JsonModel
public record PromptResponse(
        String description,
        List<Message> messages
) {
    @JsonModel
    public record Message(
            Role role,
            Content content
    ) {}

    @JsonModel
    public enum Role {
        user, assitant
    }
}
