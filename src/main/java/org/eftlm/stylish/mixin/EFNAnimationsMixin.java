package org.eftlm.stylish.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * EFN LinkAnimation NPE 竞态根因修复（对应 docs/EFN_NPE豁免技术报告_20260823.md）。
 * <p>
 * 缺陷链（反编译证据）：
 * <pre>
 *   EFNAnimations.calculateWeaponSpeedWithCap(...)   [EFN 3.4.0]
 *     if (animation instanceof AttackAnimation && entitypatch instanceof PlayerPatch) { ... }
 *     return animation.isLinkAnimation() ? animation.getPlaySpeed(entitypatch, animation) : 1.0F;
 *                                         ^^^^^^^^^^^ LinkAnimation.getPlaySpeed 无条件解引用 toAnimation
 *   StaticAnimation.setLinkAnimation(...)            [EpicFight 内核]
 *     float playTime = this.getPlaySpeed(entitypatch, dest);   // ← EFN mixin 在此注入速度计算
 *     ...
 *     dest.setConnectedAnimations(from, this);                  // ← toAnimation 在此才赋值
 * </pre>
 * 即：EFN 的播放速度计算在 {@code dest.toAnimation == null} 的窗口内被触发时 NPE。
 * 此前本模组以"播放纪律"（空闲才播）豁免；此处做防御性 mixin 根因修复——
 * 拦截 {@code getPlaySpeed} 调用，LinkAnimation 的 toAnimation 未设置时返回默认速度 1.0F，
 * 修复后高频无缝连击（行为表 / RL / 未来意图队列 reserveAnimation 路径）同样安全。
 * <p>
 * 目标类为第三方闭源模组：使用 {@code targets} 字符串（无编译期依赖）＋ {@link Pseudo}
 * （EFN 未安装时该 mixin 静默跳过，配合 mixins.json {@code required:false}）。
 * 映射机制：Forge 1.20.1 生产环境以 SRG 名运行，但 EFN 的
 * {@code calculateWeaponSpeedWithCap} 与 EpicFight 的 {@code getPlaySpeed} 均为
 * <b>模组自定义方法（无 SRG 名）</b>，运行时保持原名，故 remap=false 直接匹配。
 */
@Pseudo
@Mixin(targets = "com.hm.efn.gameasset.EFNAnimations", remap = false)
public abstract class EFNAnimationsMixin {

    @Redirect(
            method = "calculateWeaponSpeedWithCap",
            at = @At(value = "INVOKE",
                    target = "Lyesman/epicfight/api/animation/types/DynamicAnimation;getPlaySpeed(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lyesman/epicfight/api/animation/types/DynamicAnimation;)F"),
            remap = false
    )
    private static float eftlm$safeLinkAnimationSpeed(DynamicAnimation instance,
                                                      LivingEntityPatch<?> entitypatch,
                                                      DynamicAnimation animation) {
        if (instance instanceof LinkAnimation link && link.getNextAnimation() == null) {
            // 竞态窗口：LinkAnimation.toAnimation 尚未设置，返回默认播放速度
            return 1.0F;
        }
        return instance.getPlaySpeed(entitypatch, animation);
    }
}
