package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.protocol;

import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.Capabilities;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.ClientInfo;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.CompleteResult;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.CompletionArgument;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.CompletionContext;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.CompletionRef;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.InitializeResponse;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.JsonSchema;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.ListPromptsResponse;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.ListResourcesResponse;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.ListToolsResponse;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.LoggingLevel;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.Metadata;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.PromptResponse;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.ReadResourceResponse;
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
    private final InitializeResponse initializeResponse;
    private final JsonRpcHandler handler;
    private final JsonMapper jsons;
    private final ListToolsResponse tools;
    private final ListPromptsResponse prompts;

    // for subclassing proxies
    protected MCPJSONRPCProtocol() {
        tools = null;
        prompts = null;
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
                .filter(it -> "tool".equals(registry.methods().get(it.name()).metadata().getOrDefault("mcp.type", "")))
                .map(it -> new ListToolsResponse.Tool(
                        null,
                        null,
                        it.name(),
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
                                it.params().stream()
                                        .filter(p -> p.required() != null && p.required())
                                        .map(OpenRpc.JsonRpcMethod.Parameter::name)
                                        .sorted()
                                        .toList()
                        ),
                        registry.methods().get(it.name()).isNotification() || it.result() == null || it.result().schema() == null || "null".equals(it.result().schema().type()) ?
                                null :
                                toMcpSchema(openRpcService.resolveRefs(schemas, it.result().schema()))))
                .toList(),
                // no pagination since we have a few tools for now
                null);
        this.prompts = new ListPromptsResponse(openrpc.methods().values().stream()
                .filter(it -> "prompt".equals(registry.methods().get(it.name()).metadata().getOrDefault("mcp.type", "")))
                .map(it -> new ListPromptsResponse.Prompt(
                        null,
                        it.name(),
                        it.name(),
                        it.description(),
                        // there params are only strings!
                        it.params().stream()
                                .map(p -> new ListPromptsResponse.Prompt.Argument(
                                        p.name(), p.name(),
                                        p.schema().description(),
                                        p.schema().nullable() != null && !p.schema().nullable()
                                ))
                                .toList()))
                .toList(),
                // no pagination since we have a few prompts for now
                null);

        initializeResponse = new InitializeResponse(
                "2025-06-18",
                new InitializeResponse.Capabilities(
                        null, // todo
                        prompts.prompts().isEmpty() ? null : new InitializeResponse.Prompts(false),
                        null, // todo: enable user to expose resources
                        tools.tools().isEmpty() ? null : new InitializeResponse.Tools(false),
                        null, // todo
                        null),
                new InitializeResponse.ServerInfo("fusion-demo", "Fusion Demo", "1.0.0"),
                "Use demo tool");
    }

    // https://modelcontextprotocol.io/specification/2025-06-18/basic/lifecycle
    @JsonRpc("initialize")
    public InitializeResponse initialize(
            @JsonRpcParam(required = true) final String protocolVersion,
            @JsonRpcParam final Capabilities capabilities,
            @JsonRpcParam final ClientInfo clientInfo
    ) {
        if (!protocolVersion.startsWith("2025")) {
            throw new JsonRpcException(-32602, "Unsupported protocol version", Map.of(
                    "supported", List.of(initializeResponse.protocolVersion()),
                    "requested", protocolVersion
            ), null);
        }
        if (!initializeResponse.protocolVersion().equals(protocolVersion)) { // minimum compat - to improve
            return new InitializeResponse(protocolVersion, initializeResponse.capabilities(), initializeResponse.serverInfo(), initializeResponse.instructions());
        }
        return initializeResponse;
    }

    @JsonRpc("notifications/initialized")
    public void onInitialize(@JsonRpcParam("_meta") final Metadata metadata, final Request request) {
        MCPSession.Accessor.create(request);
    }

    @JsonRpc("notifications/cancelled")
    public void onCancelled(@JsonRpcParam final String reason,
                            @JsonRpcParam final String requestId,
                            final Request request) {
        final var sse = MCPSession.Accessor.get(request).sse();
        if (sse != null) {
            sse.cancel();
        }
    }

    @JsonRpc("notifications/progress")
    public void onProgress(
            @JsonRpcParam final String message,
            @JsonRpcParam final Double progress,
            @JsonRpcParam final Object progressToken, // int or string
            @JsonRpcParam final Double total
    ) {
        // no-op
    }

    @JsonRpc("notifications/roots/list_changed")
    public void onRootsListChanged(@JsonRpcParam("_meta") final Metadata metadata) {
        // no-op
    }

    @JsonRpc("completion/complete")
    public CompleteResult completion(@JsonRpcParam final CompletionArgument argument,
                                     @JsonRpcParam final CompletionContext context,
                                     @JsonRpcParam final CompletionRef ref) {
        // todo
        return new CompleteResult(null, new CompleteResult.Completion(false, 0, List.of()));
    }

    @JsonRpc("logging/setLevel")
    public void setLoggingLevel(@JsonRpcParam final String level, final Request request) {
        if (initializeResponse.capabilities().logging() != null) {
            MCPSession.Accessor.get(request).setLoggingLevel(LoggingLevel.valueOf(level));
        }
    }

    // https://modelcontextprotocol.io/specification/2025-06-18/basic/utilities/ping
    @JsonRpc("ping")
    public Map<String, String> ping(@JsonRpcParam("_meta") final Metadata metadata) {
        return Map.of();
    }

    @JsonRpc("tools/list")
    public ListToolsResponse listTools(
            @JsonRpcParam final String cursor) {
        return tools;
    }

    @JsonRpc("resources/list")
    public ListResourcesResponse listResources(
            @JsonRpcParam final String cursor) {
        // todo: expose it with a SPI or just let the user define the jsonrpc method if needed?
        return new ListResourcesResponse(List.of(), null);
    }

    @JsonRpc("resources/read")
    public ReadResourceResponse readResource(
            @JsonRpcParam final String uri) {
        // todo: expose it with a SPI or just let the user define the jsonrpc method if needed?
        return new ReadResourceResponse(null, List.of());
    }

    @JsonRpc("resources/subscribe")
    public void subscribeResource(
            @JsonRpcParam final String uri) {
        // no-op: todo
    }

    @JsonRpc("resources/unsubscribe")
    public void unsubscribeResource(
            @JsonRpcParam final String uri) {
        // no-op: todo
    }

    @JsonRpc("resources/templates/list")
    public ListResourcesResponse listResourceTemplates(
            @JsonRpcParam final String cursor) {
        // todo: expose it with a SPI or just let the user define the jsonrpc method if needed?
        return new ListResourcesResponse(List.of(), null);
    }

    @JsonRpc("prompts/list")
    public ListPromptsResponse listPrompts(
            @JsonRpcParam final String cursor) {
        return prompts;
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
                .thenApply(res -> {
                    if (res instanceof Response r && r.result() != null) {
                        if (r.result() instanceof ToolResponse tr) {
                            return tr;
                        }
                        return ToolResponse.structure(jsons, r.result());
                    }
                    return onError(res);
                });
    }

    @JsonRpc("prompts/get")
    public CompletionStage<PromptResponse> callPrompt(@JsonRpcParam final String name,
                                                      @JsonRpcParam final Map<String, Object> arguments,
                                                      final Request httpRequest) {
        return handler
                .execute(Map.of(
                        "jsonrpc", "2.0",
                        "method", name,
                        "params", arguments
                ), httpRequest)
                .thenApply(res -> {
                    if (res instanceof Response r && r.result() instanceof PromptResponse pr) {
                        return pr;
                    }
                    return onError(res);
                });
    }

    /* server -> client (SSE channel)
    @JsonRpc("notifications/resources/list_changed")
    public void onResourcesListChanged(@JsonRpcParam("_meta") final Metadata metadata) {
        // no-op
    }

    @JsonRpc("notifications/resources/updated")
    public void onResourcesUpdated(@JsonRpcParam final String uri) {
        // no-op
    }

    @JsonRpc("notifications/prompts/list_changed")
    public void onPromptsListChanged(@JsonRpcParam("_meta") final Metadata metadata) {
        // no-op
    }

    @JsonRpc("notifications/tools/list_changed")
    public void onToolsUpdated(@JsonRpcParam("_meta") final Metadata uri) {
        // no-op
    }

    @JsonRpc("notifications/messages")
    public void onMessage(@JsonRpcParam final String logger,
                          @JsonRpcParam final LoggingLevel level,
                          // can be string or not
                          @JsonRpcParam final Object data,
                          final Request request) {
        // no-op
    }
    */

    private <T> T onError(final Object res) {
        if (res instanceof Response r && r.error() != null) {
            throw new JsonRpcException(r.error().code(), r.error().message(), r.error().message(), null);
        }

        // unlikely
        throw new JsonRpcException(-32603, "Unexpected result");
    }

    private JsonSchema toMcpSchema(final OpenRpc.JsonSchema schema) {
        if (schema == null) {
            return null;
        }
        return new JsonSchema(
                schema.type(), schema.nullable(), null, schema.description(), schema.format(), schema.pattern(),
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
                                .toList(),
                null);
    }
}
