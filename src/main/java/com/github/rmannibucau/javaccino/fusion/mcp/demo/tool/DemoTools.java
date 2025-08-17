package com.github.rmannibucau.javaccino.fusion.mcp.demo.tool;

import com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.PromptResponse;
import com.github.rmannibucau.javaccino.fusion.mcp.demo.tool.model.Demo;
import io.yupiik.fusion.framework.api.scope.ApplicationScoped;
import io.yupiik.fusion.framework.build.api.jsonrpc.JsonRpc;
import io.yupiik.fusion.framework.build.api.jsonrpc.JsonRpcParam;
import io.yupiik.fusion.framework.build.api.metadata.BeanMetadata;

import java.util.List;

import static com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.model.Content.text;

@ApplicationScoped
public class DemoTools {
    @BeanMetadata(name = "mcp.type", value = "tool")
    @JsonRpc(value = "demo/tool", documentation = "Demo.")
    public Demo demoTool() {
        return new Demo("hello fusion!");
    }

    @BeanMetadata(name = "mcp.type", value = "prompt")
    @JsonRpc(value = "demo/prompt", documentation = "Demo.")
    public PromptResponse demoPrompt(@JsonRpcParam final String code) {
        return new PromptResponse("hello fusion!", List.of(new PromptResponse.Message(
                PromptResponse.Role.user,
                text("hello sir! your code is <" + code + '>'))));
    }
}
