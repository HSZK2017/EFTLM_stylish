package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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
 * 每个危险区登记 {归属女仆, 维度, 中心, 半径, 到期时刻}；到期项在查询与
 * {@link #prune} 时清理。
 *
 * <h2>P2-6 修复（2026-09-11，审查报告 AR-9）</h2>
 * 原实现是 {@code static final List<Hazard>}，只有 {中心, 半径, until} 三个字段，存在两个问题：
 * <ol>
 *     <li><b>时钟混用</b>：{@code until} 用<b>登记方</b>女仆的 {@code tickCount} 盖章
 *         （{@code ItemCombat:232} 等），而查询/清理用<b>调用方</b>女仆的 {@code tickCount}
 *         （{@code isHazardous:44}、{@code prune}）。不同女仆的 {@code tickCount} 起点不同
 *         （实体刚加载时归零），于是"谁先 tick 谁说了算"：条目可能瞬间过期（无害）或
 *         <b>长期滞留</b>（把别处的安全格判成危险区，闪避方向选反）。现改为统一的
 *         <b>维度游戏刻</b> {@link Level#getGameTime()}——同维度内所有女仆共享同一时钟。</li>
 *     <li><b>无归属、无维度</b>：A 女仆放的墙会让 B 女仆（甚至另一维度同坐标）改变闪避方向。
 *         影子 A/B 评估因此互相污染（AR-9）。现按 {@code 归属 UUID + 维度} 过滤。</li>
 * </ol>
 * 保留 {@code static} 存储（方块是世界实体，与具体女仆无关），但语义上按归属隔离。
 */
public final class SpatialMap {

    /** 危险区条目（P2-6：新增 owner 与 dimension） */
    record Hazard(UUID owner, ResourceKey<Level> dim, BlockPos pos, int radius, long until) {
    }

    private static final List<Hazard> HAZARDS = new ArrayList<>();

    /** 单维度内最多保留的危险区条目（防极端情况下的无界增长） */
    private static final int MAX_HAZARDS = 512;

    private SpatialMap() {
    }

    /**
     * 登记危险区：归属当前女仆、当前维度，按<b>游戏刻</b>到期自动清理。
     *
     * @param durationTicks 存活刻数（调用方给时长而不是绝对到期刻，避免各自算时钟）
     */
    public static void registerHazard(EntityMaid maid, BlockPos pos, int radius, int durationTicks) {
        if (maid == null || pos == null || maid.level() == null) {
            return;
        }
        long now = maid.level().getGameTime();
        if (HAZARDS.size() >= MAX_HAZARDS) {
            prune(maid); // 先做一次全量过期清理
            if (HAZARDS.size() >= MAX_HAZARDS) {
                HAZARDS.remove(0); // 仍满则淘汰最旧
            }
        }
        HAZARDS.add(new Hazard(maid.getUUID(), maid.level().dimension(), pos.immutable(), radius,
                now + Math.max(1, durationTicks)));
    }

    /** 当前位置是否处于<b>该女仆自己</b>登记的危险区（同维度） */
    public static boolean isHazardous(EntityMaid maid, BlockPos pos) {
        if (maid == null || pos == null || maid.level() == null) {
            return false;
        }
        long now = maid.level().getGameTime();
        UUID owner = maid.getUUID();
        ResourceKey<Level> dim = maid.level().dimension();
        boolean hit = false;
        Iterator<Hazard> it = HAZARDS.iterator();
        while (it.hasNext()) {
            Hazard h = it.next();
            if (now >= h.until()) {
                it.remove(); // 顺手清理（不再依赖"某个女仆恰好调用 prune"）
                continue;
            }
            if (hit || !h.owner().equals(owner) || !h.dim().equals(dim)) {
                continue;
            }
            if (pos.distToCenterSqr(h.pos().getX() + 0.5, h.pos().getY() + 0.5, h.pos().getZ() + 0.5)
                    <= (double) h.radius() * h.radius()) {
                hit = true; // 继续迭代以完成清理
            }
        }
        return hit;
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

    /** 当前未过期危险区数量（诊断） */
    public static int hazardCount() {
        return HAZARDS.size();
    }

    /** 清理过期危险区（每 tick 由 {@link ItemCombat#tick} 调用；时钟取自该女仆所在维度） */
    public static void prune(EntityMaid maid) {
        if (maid == null || maid.level() == null) {
            return;
        }
        long now = maid.level().getGameTime();
        HAZARDS.removeIf(h -> now >= h.until());
    }

    /** 清空全部危险区（每女仆状态清理/关服用） */
    public static void clear() {
        HAZARDS.clear();
    }
}
