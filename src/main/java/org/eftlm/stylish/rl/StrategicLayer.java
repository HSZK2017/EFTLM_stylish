package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.item.ItemStack;
import org.eftlm.stylish.strategy.CombatActions;
import org.eftlm.stylish.strategy.StyleState;
import org.eftlm.stylish.strategy.WeaponArsenal;
import org.eftlm.stylish.util.AnimKit;

/**
 * P4 战略层（低频规则，报告 3.5）：覆盖/修正战术层 RL 的长期倾向，初始为纯规则，
 * 逐步可 RL 化。每 {@link #INTERVAL} tick 运行一次，输出低频修正：
 * <ol>
 *     <li><b>方块武器</b>（P5.5）：主手方块=方块武器（仿 AV Him克隆），目标近身时
 *         主动放墙（{@link ItemCombat#tryActiveBlockWall}），且不参与武器轮换；</li>
 *     <li><b>武器适配</b>：目标霸体（{@link TargetTracker.TrackedTarget#isHyperarmor}）
 *         且主手为远程 → 切回近战（高冲击武器才打得动霸体目标）；
 *         目标远距离放风筝（距离 &gt; {@link #KITE_RANGE}）且背包有远程武器 →
 *         切远程压制（与 StylishCombatSkill 2.6 的 4.5 格规则互补，阈值更高防冲突）；</li>
 *     <li><b>评分策略</b>：华丽度低（&lt; {@link #FLAIR_SWAP_TRIGGER}）→ 切换连携
 *         （轮换近战武器，DMC 式切换评分激励）；华丽度溢出 → 保守输出（不动作）；</li>
 *     <li><b>资源提示</b>：技能层数满（{@link StyleState#MAX_SKILL_STACK}）→ 记录
 *         strategic_ult_ready 事件（大招释放仍由战术层 RL 决策，战略层只提示）；</li>
 * </ol>
 * 全部动作记录 {@code strategic_*} trace 事件供观测；不参与训练数据（规则层）。
 */
public final class StrategicLayer {

    /** 战略层运行间隔（tick） */
    static final int INTERVAL = 40;
    /** 放风筝判定距离（格；StylishCombatSkill 2.6 的 4.5 格之上的补充阈值） */
    static final double KITE_RANGE = 8.0;
    /** 低华丽度切换连携触发阈值 */
    static final float FLAIR_SWAP_TRIGGER = 15.0F;

    private StrategicLayer() {
    }

    /** 每 tick 调用（内部按 INTERVAL 节流）；RlBrain 决策链内低频执行 */
    public static void tick(MaidPatch<?> patch, EntityMaid maid, int tick) {
        if (tick % INTERVAL != 0) {
            return;
        }
        if (patch.getEntityState().knockDown()) {
            return; // 被击倒不干预
        }
        TargetTracker.TrackedTarget t = TargetTracker.getNearestSolidTarget(maid);
        if (t == null || t.getEntity() == null) {
            return;
        }
        int tickSinceSwap = tick - StyleState.getTick(maid, StyleState.LAST_SWAP);

        // ---- 0. P5.5 方块武器：主手方块=方块武器（仿 AV Him克隆），不参与武器轮换 ----
        boolean blockWeapon = BlockWeaponRegistry.isHoldingBlockWeapon(maid.getMainHandItem());
        if (blockWeapon && t.getDistance() <= ItemCombat.ACTIVE_WALL_RANGE) {
            if (ItemCombat.tryActiveBlockWall(maid, t.getEntity())) {
                return; // 主动放置战技已放墙，本周期不再干预
            }
        }

        // ---- 1. 武器适配 ----
        WeaponArsenal.Kind kind = WeaponArsenal.classify(maid.getMainHandItem());
        boolean mainIsRanged = kind == WeaponArsenal.Kind.BOW
                || kind == WeaponArsenal.Kind.CROSSBOW || kind == WeaponArsenal.Kind.GUN;
        if (tickSinceSwap > WeaponArsenal.SWAP_COOLDOWN && !blockWeapon) {
            // 1a. 霸体目标：主手远程 → 切回近战（高冲击才能打出伤害窗口）
            if (t.isHyperarmor() && mainIsRanged) {
                var melee = WeaponArsenal.scanMelee(maid);
                if (!melee.isEmpty()) {
                    WeaponArsenal.forceHand(maid, melee.get(0));
                    StyleState.setTick(maid, StyleState.LAST_SWAP, tick);
                    RlTrace.event(maid, "strategic_weapon_melee",
                            "hyperarmor target, swap back to melee: " + melee.get(0).getHoverName().getString());
                    return;
                }
            }
            // 1b. 放风筝目标：距离远且主手近战且背包有远程 → 切远程压制
            if (t.getDistance() > KITE_RANGE && !mainIsRanged && WeaponArsenal.hasUsableRanged(maid)) {
                if (CombatActions.startRangedAim(patch)) {
                    RlTrace.event(maid, "strategic_weapon_ranged",
                            String.format("kite target at %.1f blocks, switch to ranged", t.getDistance()));
                    return;
                }
            }
        }

        // ---- 2. 评分策略：低华丽度 → 切换连携（轮换近战武器；方块武器不轮换） ----
        if (StyleState.getFlair(maid) < FLAIR_SWAP_TRIGGER
                && !mainIsRanged
                && !blockWeapon
                && tickSinceSwap > WeaponArsenal.SWAP_COOLDOWN) {
            if (CombatActions.cycleWeapon(patch)) {
                RlTrace.event(maid, "strategic_style_swap",
                        String.format("low flair %.1f, weapon swap combo", StyleState.getFlair(maid)));
                return;
            }
        }

        // ---- 3. 资源提示：技能层数满（大招就绪，释放仍由 RL 决策） ----
        if (StyleState.getSkillStack(maid) >= StyleState.MAX_SKILL_STACK) {
            RlTrace.event(maid, "strategic_ult_ready", "skill stack full, ultimate available");
        }
    }
}
