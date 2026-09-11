package org.eftlm.stylish.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * AV 防御 mixin（思路来源：AnnoyingVillagers，源码 GPL-3.0 开源）：NullEntity 的
 * EpicFight patch 缺失防护。
 * <p>
 * 部署验证 2026-08-26 崩溃（crash-2026-08-25_17.44.37）：`NullEntity.m_6075_`
 * （SRG 名，无参 = {@code aiStep}，反编译 NullEntity.java:575 起）中
 * `!this.getLivingEntityPatch().isStunned()` 对 {@code getLivingEntityPatch() == null}
 * 无空检查 → NPE（实体 patch 偶发缺失，与模组加载时序/实体生成时机相关）。
 * 依据其开源许可做防御性修复：{@code m_6075_} 开头检查 patch 缺失 → 取消本 tick 的
 * aiStep（实体保持存活与 tick，patch 恢复后自然恢复），避免整个实体崩溃服务器。
 * <p>
 * 映射与签名（2026-08-26 修正）：Forge 1.20.1 生产环境以 SRG 名运行（mixin 处理
 * SRG 字节码），method 写 {@code m_6075_} + remap=false；javap 实证
 * {@code public void m_6075_()}（无参，非 travel）——处理器签名必须为
 * {@code (CallbackInfo)}。此前误带 {@code Vec3} 导致 "Invalid descriptor:
 * Expected (CallbackInfo)V" 注入失败（防御静默失效，已修复）。
 * <p>
 * 目标类为第三方模组：{@code targets} 字符串 + {@link Pseudo}
 * （AV 未安装时静默跳过，配合 mixins.json {@code required:false}）。
 */
@Pseudo
@Mixin(targets = "com.pla.annoyingvillagers.entity.NullEntity", remap = false)
public abstract class NullEntityMixin {

    @Inject(method = "m_6075_", at = @At("HEAD"), cancellable = true, remap = false)
    private void eftlm$guardMissingEpicFightPatch(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        LivingEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(self, LivingEntityPatch.class);
        if (patch == null) {
            // 跳过本 tick aiStep（内含多处无条件 patch 解引用）；
            // 不主动打断实体：下 tick patch 恢复后行为自然恢复
            ci.cancel();
        }
    }
}
