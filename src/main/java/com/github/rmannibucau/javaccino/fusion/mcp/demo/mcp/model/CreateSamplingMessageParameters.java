package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model;

import io.yupiik.fusion.framework.build.api.json.JsonModel;

import java.util.List;
import java.util.Map;

// sampling/createMessage params
@JsonModel
public record CreateSamplingMessageParameters(
        SamplingServer includeContext,
        Integer maxTokens,
        List<SamplingMessage> messages,
        Map<String, Object> metadata,
        ModelPreferences modelPreferences,
        List<String> stopSequences,
        String systemPrompt,
        Double temperature
) {
}
