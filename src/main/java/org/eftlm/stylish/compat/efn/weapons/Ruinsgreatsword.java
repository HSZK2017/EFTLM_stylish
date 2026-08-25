package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 废墟大剑（Ruins Greatsword）行为表：技能表 EFN_SKILLS.md §4。
 * <p>
 * 普攻：AUTO1/2/3 + 冲刺斩收尾；空中劈：AIRSLASH；
 * 大招：斩击·弹刀（skill_clash），资源门控满层释放。
 */
public final class Ruinsgreatsword {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/ng_greatsword/ng_great_airslash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/ng_greatsword/ng_great_auto1"),
                        EfnAnim.byKey("biped/ng_greatsword/ng_great_auto2"),
                        EfnAnim.byKey("biped/ng_greatsword/ng_great_auto3"),
                        EfnAnim.byKey("biped/ng_greatsword/ng_great_dash"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/ng_greatsword/ng_great_skill_clash"), 60));
    }

    private Ruinsgreatsword() {
    }
}
