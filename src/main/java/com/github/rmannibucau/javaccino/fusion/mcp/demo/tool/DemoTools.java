package com.github.rmannibucau.javaccino.fusion.mcp.demo.tool;

import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.api.MCPPrompt;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.api.MCPTool;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.PromptResponse;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.Role;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.tool.model.Demo;
import io.yupiik.fusion.framework.api.scope.ApplicationScoped;
import io.yupiik.fusion.framework.build.api.jsonrpc.JsonRpc;
import io.yupiik.fusion.framework.build.api.jsonrpc.JsonRpcParam;

import java.util.List;

import static com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.Content.text;

@ApplicationScoped
public class DemoTools {
    @MCPTool
    @JsonRpc(value = "demo/tool", documentation = "Demo.")
    public Demo demoTool() {
        return new Demo("hello fusion!");
    }

    @MCPPrompt
    @JsonRpc(value = "demo/prompt", documentation = "Demo.")
    public PromptResponse demoPrompt(@JsonRpcParam final String code) {
        return new PromptResponse(
                null,
                "hello fusion!",
                List.of(new PromptResponse.Message(
                        Role.user,
                        text("hello sir! your code is <" + code + '>'))));
    }
}
