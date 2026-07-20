package com.github.mcmodderanchor.simplebedrockmodel.v2.particle.data.event;

/**
 * 音效事件节点。触发时播放音效。
 *
 * @param eventName 音效事件名称
 */
public record SoundEffectEvent(String eventName) implements IEventNode {
}
