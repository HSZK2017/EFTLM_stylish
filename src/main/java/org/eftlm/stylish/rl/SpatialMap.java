package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * P5 空间感知（报告 3.2.4 简化版）：危险区登记栅格。
 * <p>
 * 数据源（事件登记制，非方块级扫描）：
 * <ul>
 *     <li>{@link ItemCombat} 放置的临时方块/水/垫高柱（放墙/放水/柱子）；</li>
 *     <li>后续可扩展：AOE 技能落点（EnemySkillDatabase）、岩浆桶、敌方放墙。</li>
 * </ul>
 * 用途：
 * <ul>
 *     <li>反应层弹道/前摇闪避的<b>方向选择</b>：避开危险区（{@link #safeDodgeSide}）；</li>
 *     <li>观测：trace 事件记录危险区命中（可选）。</li>
 * </ul>
 * 每个危险区登记 {中心, 半径, 到期 tick}；每 tick 由 {@link ItemCombat#tick}
 * 调用 {@link #prune} 清理过期项。
 */
public final class SpatialMap {

    /** 危险区条目 */
    record Hazard(BlockPos pos, int radius, int until) {
    }

    private static final List<Hazard> HAZARDS = new ArrayList<>();

    private SpatialMap() {
    }

    /** 登记危险区（tick 到期自动清理） */
    public static void registerHazard(BlockPos pos, int radius, int untilTick) {
        HAZARDS.add(new Hazard(pos, radius, untilTick));
    }

    /** 当前位置是否处于危险区 */
    public static boolean isHazardous(EntityMaid maid, BlockPos pos) {
        int tick = maid.tickCount;
        for (Hazard h : HAZARDS) {
            if (tick < h.until() && pos.distToCenterSqr(h.pos().getX() + 0.5, h.pos().getY() + 0.5, h.pos().getZ() + 0.5)
                    <= (double) h.radius() * h.radius()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 闪避方向选择（反应层弹道拦截用）：prefer=0 左 / 1 右。
     * 返回避开危险区的一侧：{@code -1}=左侧安全，{@code 1}=右侧安全，
     * {@code 0}=两侧都不安全（退化为原随机逻辑）。
     */
    public static int safeDodgeSide(EntityMaid maid, int prefer, double lateralOffset) {
        BlockPos left = maid.blockPosition().offset((int) Math.round(-lateralOffset), 0, 0);
        BlockPos right = maid.blockPosition().offset((int) Math.round(lateralOffset), 0, 0);
        boolean leftSafe = !isHazardous(maid, left);
        boolean rightSafe = !isHazardous(maid, right);
        if (leftSafe && rightSafe) {
            return prefer == 0 ? -1 : 1;
        }
        if (leftSafe) {
            return -1;
        }
        if (rightSafe) {
            return 1;
        }
        return 0;
    }

    /** 当前危险区数量（诊断） */
    public static int hazardCount() {
        return HAZARDS.size();
    }

    /** 清理过期危险区（每 tick 由 ItemCombat.tick 调用） */
    public static void prune(int tick) {
        HAZARDS.removeIf(h -> tick >= h.until());
    }
}
