package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 荆棘轮（Thornwheel）行为表：技能表 EFN_SKILLS.md §5。
 * <p>
 * 普攻：thornwheel mob 变体 3 段；空中 / 冲刺复用 EpicFight 大剑动画；
 * 大招：thornwheel_start（技能起手动画），资源门控满层释放。
 */
public final class Thornwheel {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(Animations.GREATSWORD_AIR_SLASH))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/thornwheel/thornwheel_attack1_mob"),
                        EfnAnim.byKey("biped/thornwheel/thornwheel_attack2_mob"),
                        EfnAnim.byKey("biped/thornwheel/thornwheel_attack3_mob"),
                        Animations.GREATSWORD_DASH)))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/thornwheel/thornwheel_start"), 60));
    }

    private Thornwheel() {
    }
}
