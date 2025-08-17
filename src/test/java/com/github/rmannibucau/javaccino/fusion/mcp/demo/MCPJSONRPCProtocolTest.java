package com.github.rmannibucau.javaccino.fusion.mcp.demo;

import io.yupiik.fusion.testing.Fusion;
import io.yupiik.fusion.testing.FusionSupport;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static com.github.rmannibucau.javaccino.fusion.mcp.demo.test.JsonAsserts.assertJsonEquals;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@FusionSupport
class MCPJSONRPCProtocolTest {
    @Test
    void initializeUnsupported(@Fusion final URI mcpEndpoint, @Fusion final HttpClient http) throws IOException, InterruptedException {
        final var res = http.send(HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {
                                  "jsonrpc": "2.0",
                                  "id": 1,
                                  "method": "initialize",
                                  "params": {
                                    "protocolVersion": "2024-11-05",
                                    "capabilities": {
                                      "roots": {
                                        "listChanged": true
                                      },
                                      "sampling": {},
                                      "elicitation": {}
                                    },
                                    "clientInfo": {
                                      "name": "ExampleClient",
                                      "title": "Example Client Display Name",
                                      "version": "1.0.0"
                                    }
                                  }
                                }"""))
                        .uri(mcpEndpoint)
                        .header("accept", "application/json")
                        .header("content-type", "application/json")
                        .build(),
                ofString());

        assertEquals(200, res.statusCode());
        assertJsonEquals(
                """
                        {
                          "jsonrpc": "2.0",
                          "id": 1,
                          "error": {
                            "code": -32602,
                            "message": "Unsupported protocol version",
                            "data": {
                              "supported": [
                                "2025-06-18"
                              ],
                              "requested": "2024-11-05"
                            }
                          }
                        }""",
                res.body());
    }

    @Test
    void initializeSupported(@Fusion final URI mcpEndpoint, @Fusion final HttpClient http) throws IOException, InterruptedException {
        final var res = http.send(HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {
                                  "jsonrpc": "2.0",
                                  "id": 1,
                                  "method": "initialize",
                                  "params": {
                                    "protocolVersion": "2025-06-18",
                                    "capabilities": {
                                      "roots": {
                                        "listChanged": true
                                      },
                                      "sampling": {},
                                      "elicitation": {}
                                    },
                                    "clientInfo": {
                                      "name": "ExampleClient",
                                      "title": "Example Client Display Name",
                                      "version": "1.0.0"
                                    }
                                  }
                                }"""))
                        .uri(mcpEndpoint)
                        .header("accept", "application/json")
                        .header("content-type", "application/json")
                        .build(),
                ofString());

        assertEquals(200, res.statusCode());
        assertJsonEquals(
                """
                        {
                          "jsonrpc": "2.0",
                          "id": 1,
                          "result": {
                            "capabilities": {
                              "prompts": {
                                "listChanged": false
                              },
                              "tools": {
                                "listChanged": false
                              }
                            },
                            "instructions": "Use demo tool",
                            "protocolVersion": "2025-06-18",
                            "serverInfo": {
                              "name": "fusion-demo",
                              "title": "Fusion Demo",
                              "version": "1.0.0"
                            }
                          }
                        }""",
                res.body());
    }

    @Test
    void listTools(@Fusion final URI mcpEndpoint, @Fusion final HttpClient http) throws IOException, InterruptedException {
        final var res = http.send(HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {
                                  "jsonrpc": "2.0",
                                  "id": 1,
                                  "method": "tools/list",
                                  "params": {}
                                }"""))
                        .uri(mcpEndpoint)
                        .header("accept", "application/json")
                        .header("content-type", "application/json")
                        .build(),
                ofString());

        assertEquals(200, res.statusCode());
        assertJsonEquals(
                """
                        {
                          "jsonrpc": "2.0",
                          "id": 1,
                          "result": {
                            "tools": [
                              {
                                "description": "Demo.",
                                "inputSchema": {
                                  "description": "Input request for demo/tool",
                                  "nullable": true,
                                  "properties": {},
                                  "required": [],
                                  "type": "object"
                                },
                                "name": "demo/tool",
                                "outputSchema": {
                                  "properties": {
                                    "greeting": {
                                      "nullable": true,
                                      "type": "string"
                                    }
                                  },
                                  "required": [],
                                  "type": "object"
                                }
                              }
                            ]
                          }
                        }""",
                res.body());
    }

    @Test
    void call(@Fusion final URI mcpEndpoint, @Fusion final HttpClient http) throws IOException, InterruptedException {
        final var res = http.send(HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {
                                  "jsonrpc": "2.0",
                                  "id": 1,
                                  "method": "tools/call",
                                  "params": {
                                    "name": "demo/tool",
                                    "arguments": {}
                                  }
                                }"""))
                        .uri(mcpEndpoint)
                        .header("accept", "application/json")
                        .header("content-type", "application/json")
                        .build(),
                ofString());

        assertEquals(200, res.statusCode());
        assertJsonEquals(
                """
                        {
                          "jsonrpc": "2.0",
                          "id": 1,
                          "result": {
                            "content": [
                              {
                                "text": "{\\"greeting\\":\\"hello fusion!\\"}",
                                "type": "text"
                              }
                            ],
                            "isError": false,
                            "structuredContent": {
                              "greeting": "hello fusion!"
                            }
                          }
                        }""",
                res.body());
    }

    @Test
    void ping(@Fusion final URI mcpEndpoint, @Fusion final HttpClient http) throws IOException, InterruptedException {
        final var res = http.send(HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {
                                  "jsonrpc": "2.0",
                                  "id": 1,
                                  "method": "ping",
                                  "params": {
                                    "name": "demo",
                                    "arguments": {}
                                  }
                                }"""))
                        .uri(mcpEndpoint)
                        .header("accept", "application/json")
                        .header("content-type", "application/json")
                        .build(),
                ofString());

        assertEquals(200, res.statusCode());
        assertJsonEquals(
                """
                        {
                          "jsonrpc": "2.0",
                          "id": 1,
                          "result": {}
                        }""",
                res.body());
    }
}
