package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.protocol;

import io.yupiik.fusion.framework.api.scope.ApplicationScoped;
import io.yupiik.fusion.framework.build.api.http.HttpMatcher;
import io.yupiik.fusion.http.server.api.Request;
import io.yupiik.fusion.http.server.api.Response;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

import static java.util.concurrent.CompletableFuture.completedFuture;

@ApplicationScoped
public class MCPSSEProtocol {
    @HttpMatcher(methods = "GET", path = "/mcp")
    public CompletionStage<Response> sse(final Request request) {
        request.unwrap(HttpServletRequest.class).getAsyncContext().setTimeout(Long.MAX_VALUE);
        return completedFuture(Response.of()
                .status(200)
                .header("content-type", "text/event-stream")
                .body((Flow.Publisher<ByteBuffer>) subscriber -> {
                    // for now we do not do anything since we do not emit anything and request will just be closed by the client
                })
                .build());
    }
}
