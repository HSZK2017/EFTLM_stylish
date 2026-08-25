package org.eftlm.stylish.rl;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * P5.5 方块武器注册表（仿 AV "Him克隆"（{@code HerobrineCloneEntity}）的黑曜石方块武器体系）。
 * <p>
 * AV 侧机制（反编译 {@code HerobrineCloneEntity / HerobrineObsidianWeapon / ShadowHerobrineEntity}）：
 * 克隆体主手持有专用物品 {@code OBSIDIAN_WEAPON}（EpicFight 注册为 SWORD 类别），Patch 把该类别
 * 绑定 CombatEvolution 行为表 {@code HerobrineObsidianWeapon.OBSIDIAN_WEAPON}（黑曜石拳击 + 格挡 +
 * 换武器），黑曜石"放置技能"由 {@code ShadowHerobrineEntity.spawnDarkObEntities()}（上/左/右三块
 * 影黑曜石短柱 {@code BlockProjectileEntity} 环绕后齐射）与 {@code CombatCommon.placeRandomFrontWall()}
 * （身前直接放墙）实现。即 AV 用"专用物品 + 行为表 + 放置技能"组合出"方块武器"语义。
 * <p>
 * 本注册表把该语义泛化为<b>主手任意方块 = 方块武器</b>：放置技能放出的方块 = 主手方块本身
 * （圆石主手→速放圆石，深板岩主手→速放深板岩，黑曜石主手→黑曜石墙/柱），且与克隆体一样
 * <b>不消耗主手物品</b>（方块是武器不是弹药）。核心是运行时检测（{@code specOf}），
 * <b>不需要逐个方块注册</b>：通用模板覆盖一切 BlockItem，圆石/深板岩/黑曜石作为
 * 用户指定的三个注册项提供 per-block 覆盖（黑曜石墙冷却更快，仿 Herobrine 的暗影黑曜石）。
 * <p>
 * 全部为规则层（不进 RL 状态/训练数据），动作记录 {@code item_* / strategic_*} trace 事件。
 */
public final class BlockWeaponRegistry {

    /** 方块武器规格：三级格挡各自冷却（tick）+ 放墙尺寸 */
    public record BlockWeaponSpec(int wallCooldown, int blockParryCooldown, int pillarCooldown,
                                  int wallRows, int wallHeight) {

        /** 通用模板：任意 BlockItem 主手的默认规格（与现有 ItemCombat 三级策略一致） */
        public static final BlockWeaponSpec GENERIC = new BlockWeaponSpec(80, 60, 120, 2, 2);

        /** 黑曜石主手：墙冷却减半（仿 Herobrine 暗影黑曜石墙的积极放置） */
        public static final BlockWeaponSpec OBSIDIAN = new BlockWeaponSpec(40, 60, 120, 2, 2);
    }

    /** 用户指定的三个注册项（圆石 / 深板岩圆石 / 黑曜石）；其余 BlockItem 走通用模板 */
    private static final Map<Item, BlockWeaponSpec> SPECIAL = new HashMap<>();

    static {
        SPECIAL.put(Items.COBBLESTONE, BlockWeaponSpec.GENERIC);
        SPECIAL.put(Items.COBBLED_DEEPSLATE, BlockWeaponSpec.GENERIC);
        SPECIAL.put(Items.OBSIDIAN, BlockWeaponSpec.OBSIDIAN);
    }

    private BlockWeaponRegistry() {
    }

    /**
     * 主手方块武器判定与规格查询：主手为 BlockItem 返回对应规格
     * （注册项优先，否则通用模板）；非方块主手返回 null。
     */
    @Nullable
    public static BlockWeaponSpec specOf(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
            return null;
        }
        BlockWeaponSpec spec = SPECIAL.get(stack.getItem());
        return spec != null ? spec : BlockWeaponSpec.GENERIC;
    }

    /** 主手是否处于"方块武器"状态（供策略层判断：方块武器不参与武器轮换） */
    public static boolean isHoldingBlockWeapon(ItemStack mainHand) {
        return specOf(mainHand) != null;
    }
}
