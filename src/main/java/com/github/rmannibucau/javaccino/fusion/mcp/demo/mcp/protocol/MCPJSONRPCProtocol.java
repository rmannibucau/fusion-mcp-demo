package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.protocol;

import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.Capabilities;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.ClientInfo;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.InitializeResponse;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.JsonSchema;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.ListToolsResponse;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.ToolResponse;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.model.fusion.OpenRpc;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.service.OpenRpcService;
import io.yupiik.fusion.framework.api.scope.ApplicationScoped;
import io.yupiik.fusion.framework.build.api.jsonrpc.JsonRpc;
import io.yupiik.fusion.framework.build.api.jsonrpc.JsonRpcParam;
import io.yupiik.fusion.http.server.api.Request;
import io.yupiik.fusion.json.JsonMapper;
import io.yupiik.fusion.jsonrpc.JsonRpcException;
import io.yupiik.fusion.jsonrpc.JsonRpcHandler;
import io.yupiik.fusion.jsonrpc.JsonRpcRegistry;
import io.yupiik.fusion.jsonrpc.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class MCPJSONRPCProtocol {
    private final ListToolsResponse tools;
    private final InitializeResponse initializeResponse;
    private final JsonRpcHandler handler;
    private final JsonMapper jsons;

    // for subclassing proxies
    protected MCPJSONRPCProtocol() {
        tools = null;
        initializeResponse = null;
        handler = null;
        jsons = null;
    }

    public MCPJSONRPCProtocol(final OpenRpcService openRpcService,
                              final JsonMapper jsons,
                              final JsonRpcHandler handler,
                              final JsonRpcRegistry registry) {
        final var openrpc = openRpcService.load();
        final var schemas = openRpcService.resolveSchemas(openrpc);

        this.handler = handler;
        this.jsons = jsons;

        this.tools = new ListToolsResponse(openrpc.methods().values().stream()
                .filter(it -> !isMcp(it.name()))
                .map(it -> new ListToolsResponse.Tool(
                        it.name(),
                        it.description(),
                        new JsonSchema(
                                it.params().isEmpty(),
                                "Input request for " + it.name(),
                                it.params().stream()
                                        .collect(toMap(
                                                OpenRpc.JsonRpcMethod.Parameter::name,
                                                p -> toMcpSchema(
                                                        ofNullable(openRpcService.resolveRefs(schemas, p.schema()))
                                                                .orElse(p.schema())))),
                                List.of() // todo: add in @JsonRpcParam
                        ),
                        registry.methods().get(it.name()).isNotification() || it.result() == null || it.result().schema() == null || "null".equals(it.result().schema().type()) ?
                                null :
                                toMcpSchema(openRpcService.resolveRefs(schemas, it.result().schema()))))
                .toList(),
                // no pagination since we have a few tools for now
                null);
        initializeResponse = new InitializeResponse(
                "2025-06-18",
                new InitializeResponse.Capabilities(
                        null,
                        null,
                        null,
                        new InitializeResponse.Tools(false)),
                new InitializeResponse.ServerInfo("fusion-demo", "Fusion Demo", "1.0.0"),
                "Use demo tool");
    }

    // https://modelcontextprotocol.io/specification/2025-06-18/basic/lifecycle
    @JsonRpc("initialize")
    public InitializeResponse initialize(
            @JsonRpcParam final String protocolVersion,
            @JsonRpcParam final Capabilities capabilities,
            @JsonRpcParam final ClientInfo clientInfo
    ) {
        if (!initializeResponse.protocolVersion().equals(protocolVersion)) {
            throw new JsonRpcException(-32602, "Unsupported protocol version", Map.of(
                    "supported", List.of(initializeResponse.protocolVersion()),
                    "requested", protocolVersion
            ), null);
        }
        return initializeResponse;
    }

    @JsonRpc("notifications/initialized")
    public void initialized() {
        // no-op
    }

    // https://modelcontextprotocol.io/specification/2025-06-18/basic/utilities/cancellation
    @JsonRpc("notifications/cancelled")
    public void cancel(
            @JsonRpcParam final String requestId,
            @JsonRpcParam final String reason
    ) {
        // no-op
    }

    // https://modelcontextprotocol.io/specification/2025-06-18/basic/utilities/ping
    @JsonRpc("ping")
    public Map<String, String> ping() {
        return Map.of();
    }

    @JsonRpc("tools/list")
    public ListToolsResponse listTools(
            @JsonRpcParam final String cursor) {
        return tools;
    }

    @JsonRpc("tools/call")
    public CompletionStage<ToolResponse> callTool(@JsonRpcParam final String name,
                                                  @JsonRpcParam final Object arguments,
                                                  final Request httpRequest) {
        return handler
                .execute(Map.of(
                        "jsonrpc", "2.0",
                        "method", name,
                        "params", arguments
                ), httpRequest)
                .thenApply(res -> res instanceof Response jsonRpcResponse && jsonRpcResponse.result() != null ?
                        ToolResponse.structure(jsons, jsonRpcResponse.result())
                        : new ToolResponse(true, List.of(), null));
    }

    private JsonSchema toMcpSchema(final OpenRpc.JsonSchema schema) {
        if (schema == null) {
            return null;
        }
        return new JsonSchema(
                schema.type(), schema.nullable(), schema.description(), schema.format(), schema.pattern(),
                schema.properties() == null ? null : schema.properties().entrySet().stream()
                        .collect(toMap(Map.Entry::getKey, it -> toMcpSchema(it.getValue()))),
                schema.additionalProperties() instanceof Map<?, ?> ?
                        toMcpSchema(jsons.fromString(OpenRpc.JsonSchema.class, jsons.toString(schema.additionalProperties()))) :
                        schema.additionalProperties(),
                toMcpSchema(schema.items()), schema.enumeration(),
                schema.properties() == null ?
                        null :
                        schema.properties()
                                .entrySet().stream()
                                .filter(it -> it.getValue().nullable() != null && !it.getValue().nullable())
                                .map(Map.Entry::getKey)
                                .toList());
    }

    private boolean isMcp(final String key) {
        return key.startsWith("tools/") ||
                key.startsWith("notifications/") ||
                "initialize".equals(key) ||
                "ping".equals(key);
    }
}
