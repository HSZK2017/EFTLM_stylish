package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 阎魔刀（DMC）行为表：技能表 EFN_SKILLS.md §1。
 * <p>
 * 普攻：slasher 3 段 + crosscut 3 段；空中 JC：aerialrave；
 * 大招：次元斩（judgementcut_all），资源门控满层释放。
 */
public final class Yamato {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/yamato/dmcyamato_aerialrave_1")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/yamato/dmcyamato_slasher_1"),
                        EfnAnim.byKey("biped/yamato/dmcyamato_slasher_2"),
                        EfnAnim.byKey("biped/yamato/dmcyamato_slasher_3"),
                        EfnAnim.byKey("biped/yamato/dmcyamato_slasher_crosscut_1"),
                        EfnAnim.byKey("biped/yamato/dmcyamato_slasher_crosscut_2"),
                        EfnAnim.byKey("biped/yamato/dmcyamato_slasher_crosscut_3"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/yamato/dmcyamato_judgementcut_all"), 60));
    }

    private Yamato() {
    }
}
