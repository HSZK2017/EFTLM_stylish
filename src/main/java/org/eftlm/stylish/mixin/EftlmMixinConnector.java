package org.eftlm.stylish.mixin;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

/**
 * mixin 早期注册连接器（META-INF/mixin-connector.json）。
 * <p>
 * 背景：mods.toml 的 {@code mixinConfigs} 在 mod 构造阶段才注册 mixin 配置，
 * 而 {@code MinecraftServer} 等原版类可能在更早（Forge 加载第三方 mod 注册逻辑时）
 * 已被类加载——mixin 对已加载类不再 transform，导致 {@link TickrateMixin}
 * （目标 MinecraftServer）静默失效（无注入也无报错）。
 * <p>
 * 本连接器在 FML 早期（ModLauncher 启动阶段，任何原版类 transform 之前）注册
 * mixin 配置，等价于 1.12 TickrateChanger 用 {@code IFMLLoadingPlugin} 在加载早期
 * 注册 ASM transformer 的机制（思路来源：《TickrateChanger》/《TickrateChanger:Reborn》，
 * MegaDarkness，独立实现）。
 * <p>
 * 注意：注册后 mods.toml 必须移除 {@code mixinConfigs} 条目，避免同一配置重复注册。
 */
public class EftlmMixinConnector implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfiguration("eftlm_stylish.mixins.json");
    }
}
