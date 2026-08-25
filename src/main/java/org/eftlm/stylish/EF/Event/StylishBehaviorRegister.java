package org.eftlm.stylish.EF.Event;

import com.google.common.collect.ImmutableMap;
import net.EFTLM.EF.API.Event.CombatBehaviorsEvent;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.compat.wom.WomCompat;
import org.eftlm.stylish.compat.wom.WomSkillChecks;
import org.eftlm.stylish.EFTLMStylish;
import org.eftlm.stylish.compat.efn.EfnCompat;
import org.eftlm.stylish.strategy.CombatActions;
import org.eftlm.stylish.strategy.SkillGate;
import org.eftlm.stylish.strategy.StylishConditions;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.Map;

/**
 * 华丽连段作战逻辑注册（EFTLM 官方扩展 API：CombatBehaviorsEvent）。
 * <p>
 * 为各武器类别注册自定义攻击行为表，替换 EFTLM 默认战斗方法：
 * <ul>
 *     <li>防守系列：敌人攻击时弹反（华丽度低）/ 闪避 / 格挡</li>
 *     <li>浮空系列：目标浮空时先换武器再 JC 取消空中连段</li>
 *     <li>剑圣连段：3 招大范围招式后切换风格</li>
 *     <li>枪神连段：3 招快速突进后切换风格并标记连段结束（轮换武器 / 远程收尾）</li>
 *     <li>枪神点射：目标较远时切换远程武器瞄准收尾</li>
 * </ul>
 * 行为表由 EpicFight 的 AnimatedAttackGoal 自动驱动，无需任何 mixin / 补丁覆写。
 */
@Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
public class StylishBehaviorRegister {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    // ------------------------------------------------------------------
    // 武器类别 → 行为表（按持握风格）
    // ------------------------------------------------------------------

    @SubscribeEvent
    public static void onCombatBehavior(CombatBehaviorsEvent event) {
        try {
            doRegister(event);
        } catch (Throwable t) {
            // 注册失败会导致 EFTLM MaidPatch 构造失败（渲染 / 技能全部失效），必须记录
            LOGGER.error("[DIAG] CombatBehaviorsEvent register FAILED!", t);
        }
    }

    private static void doRegister(CombatBehaviorsEvent event) {
        // 注意：EFTLM 1.3.2 的 MaidCombatBehaviors 会对相同类别执行
        // computeIfAbsent(...).putAll(...) 合并数据包行为（BehaviorReloadListener），
        // 因此 value 必须是可变 Map，否则在其上 putAll 会抛 UnsupportedOperationException
        // 并导致 MaidPatch 构造失败（渲染 / 技能全部失效）。
        Map<WeaponCategory, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> map = event.getWeaponStyleAttackMotions();

        register(map, CapabilityItem.WeaponCategories.SWORD, ImmutableMap.of(
                CapabilityItem.Styles.ONE_HAND, sword(Animations.SWEEPING_EDGE, Animations.SWORD_DASH,
                        Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_AIR_SLASH),
                CapabilityItem.Styles.TWO_HAND, sword(Animations.DANCING_EDGE, Animations.SWORD_DUAL_DASH,
                        Animations.SWORD_DUAL_AUTO2, Animations.SWORD_DUAL_AUTO3, Animations.SWORD_DUAL_AIR_SLASH)));

        register(map, CapabilityItem.WeaponCategories.LONGSWORD, ImmutableMap.of(
                CapabilityItem.Styles.ONE_HAND, sword(Animations.SHARP_STAB, Animations.LONGSWORD_DASH,
                        Animations.LONGSWORD_AUTO2, Animations.LONGSWORD_AUTO3, Animations.LONGSWORD_AIR_SLASH),
                CapabilityItem.Styles.TWO_HAND, sword(Animations.SHARP_STAB, Animations.LONGSWORD_DASH,
                        Animations.LONGSWORD_AUTO2, Animations.LONGSWORD_AUTO3, Animations.LONGSWORD_AIR_SLASH)));

        register(map, CapabilityItem.WeaponCategories.GREATSWORD, ImmutableMap.of(
                CapabilityItem.Styles.TWO_HAND, sword(Animations.GREATSWORD_AUTO1, Animations.GREATSWORD_DASH,
                        Animations.GREATSWORD_AUTO2, Animations.GREATSWORD_AUTO2, Animations.GREATSWORD_AIR_SLASH)));

        register(map, CapabilityItem.WeaponCategories.TACHI, ImmutableMap.of(
                CapabilityItem.Styles.TWO_HAND, sword(Animations.RUSHING_TEMPO1, Animations.TACHI_DASH,
                        Animations.RUSHING_TEMPO2, Animations.RUSHING_TEMPO3, Animations.UCHIGATANA_AIR_SLASH)));

        register(map, CapabilityItem.WeaponCategories.UCHIGATANA, ImmutableMap.of(
                CapabilityItem.Styles.TWO_HAND, sword(Animations.BATTOJUTSU, Animations.UCHIGATANA_DASH,
                        Animations.UCHIGATANA_AUTO2, Animations.UCHIGATANA_AUTO3, Animations.UCHIGATANA_AIR_SLASH)));

        register(map, CapabilityItem.WeaponCategories.SPEAR, ImmutableMap.of(
                CapabilityItem.Styles.ONE_HAND, sword(Animations.HEARTPIERCER, Animations.SPEAR_DASH,
                        Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_ONEHAND_AIR_SLASH),
                CapabilityItem.Styles.TWO_HAND, sword(Animations.GRASPING_SPIRAL_FIRST, Animations.SPEAR_DASH,
                        Animations.SPEAR_TWOHAND_AUTO2, Animations.SPEAR_TWOHAND_AUTO2, Animations.SPEAR_TWOHAND_AIR_SLASH)));

        register(map, CapabilityItem.WeaponCategories.DAGGER, ImmutableMap.of(
                CapabilityItem.Styles.ONE_HAND, sword(Animations.DAGGER_DUAL_AUTO3, Animations.DAGGER_DASH,
                        Animations.DAGGER_DUAL_AUTO2, Animations.DAGGER_DUAL_AUTO4, Animations.DAGGER_DUAL_AIR_SLASH),
                CapabilityItem.Styles.TWO_HAND, sword(Animations.DAGGER_DUAL_AUTO3, Animations.DAGGER_DUAL_DASH,
                        Animations.DAGGER_DUAL_AUTO2, Animations.DAGGER_DUAL_AUTO4, Animations.DAGGER_DUAL_AIR_SLASH)));

        register(map, CapabilityItem.WeaponCategories.AXE, ImmutableMap.of(
                CapabilityItem.Styles.TWO_HAND, sword(Animations.AXE_AUTO2, Animations.AXE_DASH,
                        Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_AIRSLASH)));

        // 三叉戟 / 拳套：补全类别覆盖，学习技能后同样解锁大招
        register(map, CapabilityItem.WeaponCategories.TRIDENT, ImmutableMap.of(
                CapabilityItem.Styles.COMMON, sword(Animations.TRIDENT_AUTO1, Animations.TRIDENT_AUTO2,
                        Animations.TRIDENT_AUTO3, Animations.TRIDENT_AUTO3, Animations.FIST_AIR_SLASH)));
        register(map, CapabilityItem.WeaponCategories.FIST, ImmutableMap.of(
                CapabilityItem.Styles.COMMON, sword(Animations.FIST_AUTO1, Animations.FIST_DASH,
                        Animations.FIST_AUTO2, Animations.FIST_AUTO3, Animations.FIST_AIR_SLASH)));

        // 奇迹武器（WOM）行为注册（内置 EFTLM_WOM，适配 EFTLM 1.3 API）
        WomCompat.trySetWeaponMotions(event.getItemAttackMotions(), event.getItemStyleAttackMotions(), event.getItemArmatures());
        // 史诗战斗·夜幕（EFN）武器行为注册（运行时检测，未安装自动跳过）
        EfnCompat.trySetWeaponMotions(event.getItemAttackMotions(), event.getItemStyleAttackMotions(), event.getItemArmatures());
        LOGGER.info("[DIAG] CombatBehaviorsEvent registered: 8 weapon categories + WOM behaviors (wom={})",
                WomSkillChecks.LoadedWOM());
    }

    /**
     * 以可变 Map 作为 value 注册类别行为，并**整体替换**已有条目。
     * <p>
     * 注意：EFTLM 默认行为表（EFTLM_Behaviors.SetWeaponMotions）预置的 value 是
     * ImmutableMap，在其上 computeIfAbsent+putAll 会抛 UnsupportedOperationException；
     * 且 EFTLM 1.3.2 的 MaidCombatBehaviors 会对此处 key 执行 computeIfAbsent(...).putAll(...)，
     * 因此 value 必须是可变 HashMap——put 覆盖为 HashMap 两种场景都安全。
     */
    private static void register(Map<WeaponCategory, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> map,
                                 WeaponCategory category, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>> motions) {
        map.put(category, new java.util.HashMap<>(motions));
    }

    // ------------------------------------------------------------------
    // 行为表构建
    // ------------------------------------------------------------------

    /**
     * 生成华丽连段行为表。
     *
     * @param heavy   剑圣风格起手大招
     * @param dash    突进（剑圣第 2 招 / 枪神起手）
     * @param auto2   连段第 3 招
     * @param auto3   连段第 4 招（枪神风格使用 dash 后的第 3 招为 auto2）
     * @param airSlash 浮空 JC 空中攻击
     */
    private static CombatBehaviors.Builder<HumanoidMobPatch<?>> sword(
            AnimationManager.AnimationAccessor<? extends StaticAnimation> heavy,
            AnimationManager.AnimationAccessor<? extends StaticAnimation> dash,
            AnimationManager.AnimationAccessor<? extends StaticAnimation> auto2,
            AnimationManager.AnimationAccessor<? extends StaticAnimation> auto3,
            AnimationManager.AnimationAccessor<? extends StaticAnimation> airSlash) {
        return CombatBehaviors.<HumanoidMobPatch<?>>builder()
                // ---- 0. 弹反：华丽度评价下降（低于 C 级）时插入 ----
                .newBehaviorSeries(CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                        .cooldown(6).weight(80.0F).canBeInterrupted(false).looping(false)
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .custom(p -> StylishConditions.enemyAttackingLowFlair((MaidPatch<?>) p))
                                .behavior(p -> CombatActions.parry((MaidPatch<?>) p))))
                // ---- 1. 防守：敌人攻击中 → 闪避或格挡（冷却稍长避免频繁防御打断连段） ----
                .newBehaviorSeries(CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                        .cooldown(10).weight(40.0F).canBeInterrupted(false).looping(false)
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .custom(p -> StylishConditions.enemyAttackingNear((MaidPatch<?>) p))
                                .behavior(p -> CombatActions.dodgeOrBlock((MaidPatch<?>) p))))
                // ---- 2a. 浮空：先切换近战武器（制造"换武器 + JC"衔接） ----
                .newBehaviorSeries(CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                        .cooldown(0).weight(100.0F).canBeInterrupted(false).looping(false)
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .custom(p -> StylishConditions.targetAirborneAndCanSwap((MaidPatch<?>) p))
                                .behavior(p -> CombatActions.cycleWeapon((MaidPatch<?>) p))))
                // ---- 2b. 浮空 JC 取消 → 空中连段（30 tick 冷却避免无限空斩） ----
                .newBehaviorSeries(CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                        .cooldown(30).weight(100.0F).canBeInterrupted(false).looping(false)
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .custom(p -> StylishConditions.targetAirborne((MaidPatch<?>) p))
                                .animationBehavior(airSlash).withinDistance(0.0D, 4.0D)))
                // ---- 3. 剑圣连段：大范围招式 × 3 → 切换风格 ----
                .newBehaviorSeries(CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                        .cooldown(10).weight(100.0F).canBeInterrupted(true).looping(false)
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .custom(p -> StylishConditions.swordmasterInMelee((MaidPatch<?>) p))
                                .animationBehavior(heavy).withinDistance(0.0D, 3.5D))
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .animationBehavior(dash).withinDistance(0.0D, 3.5D))
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .animationBehavior(auto2).withinDistance(0.0D, 3.5D))
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .behavior(p -> CombatActions.comboEnd((MaidPatch<?>) p))))
                // ---- 4. 枪神连段：快速突进 × 3 → 切换风格 + 标记连段结束 ----
                .newBehaviorSeries(CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                        .cooldown(10).weight(100.0F).canBeInterrupted(true).looping(false)
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .custom(p -> StylishConditions.gunslingerInMelee((MaidPatch<?>) p))
                                .animationBehavior(dash).withinDistance(0.0D, 3.5D))
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .animationBehavior(auto2).withinDistance(0.0D, 3.5D))
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .behavior(p -> CombatActions.comboEnd((MaidPatch<?>) p))))
                // ---- 5. 枪神点射：目标较远时切换远程武器瞄准 ----
                .newBehaviorSeries(CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                        .cooldown(20).weight(60.0F).canBeInterrupted(false).looping(false)
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .custom(p -> StylishConditions.gunslingerRangedReady((MaidPatch<?>) p))
                                .behavior(p -> CombatActions.startRangedAim((MaidPatch<?>) p))))
                // ---- 6. 武器技能大招：学习技能 + 攒满技能层数后自动释放（资源型技能循环） ----
                // 两段式：起手大招（heavy）→ 追击段（dash），释放后清空层数并进入物品冷却
                .newBehaviorSeries(CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                        .cooldown(60).weight(25.0F).canBeInterrupted(false).looping(false)
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .custom(p -> SkillGate.canUltimate((MaidPatch<?>) p))
                                .behavior(p -> SkillGate.useSkill((MaidPatch<?>) p, heavy, 60)))
                        .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                .animationBehavior(dash).withinDistance(0.0D, 3.5D)));
    }
}
