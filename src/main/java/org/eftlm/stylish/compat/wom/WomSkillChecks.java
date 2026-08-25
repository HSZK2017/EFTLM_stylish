package org.eftlm.stylish.compat.wom;

import net.minecraftforge.fml.ModList;

/**
 * WOM 兼容层：WOM 装载检测。
 * <p>
 * WOM 武器技能已全部统一到技能目录门控（{@code skills.json} +
 * {@link org.eftlm.stylish.compat.efn.EfnSkillCatalog}：层数 + 目录冷却），
 * 原物品冷却式 canUseSkill / setCoolDown 行为条目已从各武器行为表移除。
 */
public final class WomSkillChecks {

    private WomSkillChecks() {
    }

    public static boolean LoadedWOM() {
        return ModList.get().isLoaded("wom");
    }
}
