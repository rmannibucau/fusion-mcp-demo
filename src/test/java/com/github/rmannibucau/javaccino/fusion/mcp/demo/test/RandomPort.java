package com.github.rmannibucau.javaccino.fusion.mcp.demo.test;

import io.yupiik.fusion.framework.api.configuration.ConfigurationSource;
import io.yupiik.fusion.framework.api.scope.DefaultScoped;

@DefaultScoped
public class RandomPort implements ConfigurationSource {
    @Override
    public String get(final String key) {
        return switch (key) {
            case "fusion.http-server.port" -> "0";
            case "fusion.http-server.host" -> "localhost";
            default -> null;
        };
    }
}
