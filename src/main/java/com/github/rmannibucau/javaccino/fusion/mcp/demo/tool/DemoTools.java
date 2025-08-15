package com.github.rmannibucau.javaccino.fusion.mcp.demo.tool;

import com.github.rmannibucau.javaccino.fusion.mcp.demo.tool.model.Demo;
import io.yupiik.fusion.framework.api.scope.ApplicationScoped;
import io.yupiik.fusion.framework.build.api.jsonrpc.JsonRpc;

@ApplicationScoped
public class DemoTools {
    @JsonRpc(value = "demo", documentation = "Demo.")
    public Demo call() {
        return new Demo("hello fusion!");
    }
}
