package com.github.mcmodderanchor.simplebedrockmodel.v2.particle.data.event;

/**
 * 日志事件节点。触发时输出日志消息。
 *
 * @param message 日志消息
 */
public record EventLog(String message) implements IEventNode {
}
