package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.protocol;

import io.yupiik.fusion.framework.api.scope.ApplicationScoped;
import io.yupiik.fusion.framework.build.api.http.HttpMatcher;
import io.yupiik.fusion.http.server.api.Request;
import io.yupiik.fusion.http.server.api.Response;
import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.completedFuture;

@ApplicationScoped
public class MCPSSEProtocol {
    @HttpMatcher(methods = "GET", path = "/mcp")
    public CompletionStage<Response> sse(final Request request) {
        final var session  = MCPSession.Accessor.get(request);
        request.unwrap(HttpServletRequest.class).getAsyncContext().setTimeout(Long.MAX_VALUE);
        return completedFuture(Response.of()
                .status(200)
                .header("content-type", "text/event-stream")
                .body(session.newSse())
                .build());
    }
}
