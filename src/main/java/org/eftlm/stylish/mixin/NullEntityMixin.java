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
 * AV 授权防御 mixin：NullEntity 的 EpicFight patch 缺失防护。
 * <p>
 * 部署验证 2026-08-26 崩溃（crash-2026-08-25_17.44.37）：`NullEntity.m_6075_`
 * （= Entity.travel 的 SRG 名，NullEntity.java:511）中
 * `!this.getLivingEntityPatch().isStunned()` 对 {@code getLivingEntityPatch() == null}
 * 无空检查 → NPE（实体 patch 偶发缺失，与模组加载时序/实体生成时机相关）。
 * 作者已授权 mixin 修复：{@code m_6075_} 开头检查 patch 缺失 → 取消本 tick 的
 * travel（实体保持存活与 tick，patch 恢复后自然恢复），避免整个实体崩溃服务器。
 * <p>
 * 映射机制（2026-08-25 修复注入失败）：Forge 1.20.1 生产环境以 <b>SRG 名</b>运行
 * （本体 server-...-srg.jar + 模组 reobf 后 SRG 字节码，崩溃栈证实方法名为
 * {@code m_6075_}）。mixin 应用时看到的就是 SRG 名字节码，因此原版方法（travel）
 * 必须写 SRG 名 {@code m_6075_} + remap=false；此前的 official 名 {@code travel}
 * 导致 "could not find any targets matching 'travel'" 注入失败（防御静默失效）。
 * 模组自定义方法无 SRG 名（运行时保持原名），不受此规则影响。
 * <p>
 * 目标类为第三方闭源模组：{@code targets} 字符串 + {@link Pseudo}
 * （AV 未安装时静默跳过，配合 mixins.json {@code required:false}）。
 */
@Pseudo
@Mixin(targets = "com.pla.annoyingvillagers.entity.NullEntity", remap = false)
public abstract class NullEntityMixin {

    @Inject(method = "m_6075_", at = @At("HEAD"), cancellable = true, remap = false)
    private void eftlm$guardMissingEpicFightPatch(Vec3 vec3, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        LivingEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(self, LivingEntityPatch.class);
        if (patch == null) {
            // 跳过本 tick travel（内含多处无条件 patch 解引用）；
            // 不主动打断实体：下 tick patch 恢复后移动自然恢复
            ci.cancel();
        }
    }
}
