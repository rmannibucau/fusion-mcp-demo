package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.protocol;

import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.LoggingLevel;
import io.yupiik.fusion.http.server.api.Request;
import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import static java.util.Optional.ofNullable;

public class MCPSession implements Serializable {
    private LoggingLevel loggingLevel = LoggingLevel.info;

    // todo: ensure there is some session affinity otherwise this will fail
    private volatile SseBus sse;

    public void setLoggingLevel(final LoggingLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    public LoggingLevel getLoggingLevel() {
        return loggingLevel;
    }

    // todo: add a session listener to auto disconnect
    public SseBus newSse() {
        if (sse != null) {
            sse.cancel();
        }
        return sse = new SseBus();
    }

    public SseBus sse() {
        return sse;
    }

    public static class Accessor {
        private Accessor() {
            // no-op
        }

        public static MCPSession get(final Request request) {
            return ofNullable(request.unwrap(HttpServletRequest.class).getSession(false))
                    .map(s -> s.getAttribute(MCPSession.class.getName()))
                    .map(MCPSession.class::cast)
                    .orElseThrow(() -> new IllegalStateException("No session"));
        }

        public static void create(final Request request) {
            request
                    .unwrap(HttpServletRequest.class)
                    .getSession(true)
                    .setAttribute(MCPSession.class.getName(), MCPSession.class);
        }
    }
}
