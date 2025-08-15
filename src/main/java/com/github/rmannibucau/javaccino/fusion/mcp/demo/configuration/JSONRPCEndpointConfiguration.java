package com.github.rmannibucau.javaccino.fusion.mcp.demo.configuration;

import io.yupiik.fusion.framework.api.configuration.ConfigurationSource;
import io.yupiik.fusion.framework.api.scope.DefaultScoped;

@DefaultScoped
public class JSONRPCEndpointConfiguration implements ConfigurationSource {
    @Override
    public String get(final String key) {
        return "fusion.jsonrpc.binding".equals(key) ? "/mcp" : null;
    }
}
