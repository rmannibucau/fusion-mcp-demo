package com.github.rmannibucau.javaccino.fusion.mcp.demo.test;

import io.yupiik.fusion.framework.api.scope.ApplicationScoped;
import io.yupiik.fusion.framework.api.scope.DefaultScoped;
import io.yupiik.fusion.framework.build.api.scanning.Bean;
import io.yupiik.fusion.http.server.api.WebServer;

import java.net.URI;
import java.net.http.HttpClient;

@DefaultScoped
public class TestBeans {
    @Bean
    @ApplicationScoped
    public URI uri(final WebServer.Configuration configuration) {
        return URI.create("http://localhost:" + configuration.port() + "/mcp");
    }

    @Bean
    @ApplicationScoped
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
