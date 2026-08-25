package org.eftlm.stylish.util;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories.*;

/**
 * 动画库：按武器类别 + 当前风格（剑圣 SWORDMASTER / 枪神 GUNSLINGER）查询
 * 对应的攻击 / 空中 JC / 格挡 / 弹反 / 闪避动画。
 * <p>
 * 招式全部复用 Epic Fight 本体动画资产，与 EFTLM 内置行为一致的注册方式。
 */
public final class AnimKit {

    /** 风格：剑圣（大范围、高伤害招式） */
    public static final int STYLE_SWORDMASTER = 0;
    /** 风格：枪神（快速突进、远程射击收尾） */
    public static final int STYLE_GUNSLINGER = 1;

    private AnimKit() {
    }

    /**
     * 当前主手武器的 Epic Fight 武器类别。
     */
    public static WeaponCategory categoryOf(LivingEntityPatch<?> patch) {
        CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(patch.getOriginal().getMainHandItem());
        if (cap == null) {
            return CapabilityItem.WeaponCategories.FIST;
        }
        return cap.getWeaponCategory();
    }

    /**
     * 将 WeaponCategory（可扩展接口）转为内置枚举，供 switch 使用；
     * 第三方扩展类别返回 null。
     */
    private static CapabilityItem.WeaponCategories asEnum(WeaponCategory category) {
        return category instanceof CapabilityItem.WeaponCategories c ? c : null;
    }

    /**
     * 空中攻击（JC 取消）：敌人浮空时使用，1.5 倍伤害修正。
     */
    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> airSlash(WeaponCategory category) {
        CapabilityItem.WeaponCategories c = asEnum(category);
        if (c == null) {
            return Animations.SWORD_AIR_SLASH;
        }
        return switch (c) {
            case SWORD -> Animations.SWORD_AIR_SLASH;
            case LONGSWORD -> Animations.LONGSWORD_AIR_SLASH;
            case GREATSWORD -> Animations.GREATSWORD_AIR_SLASH;
            case UCHIGATANA -> Animations.UCHIGATANA_AIR_SLASH;
            case SPEAR -> Animations.SPEAR_TWOHAND_AIR_SLASH;
            case DAGGER -> Animations.DAGGER_AIR_SLASH;
            case AXE -> Animations.AXE_AIRSLASH;
            case FIST -> Animations.FIST_AIR_SLASH;
            // TACHI / TRIDENT / 其它没有专属空中动画，回退为太刀或拳
            case TACHI -> Animations.UCHIGATANA_AIR_SLASH;
            default -> Animations.FIST_AIR_SLASH;
        };
    }

    /**
     * 格挡受击动画（防守判定：防御）。
     */
    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> guardHit(WeaponCategory category) {
        CapabilityItem.WeaponCategories c = asEnum(category);
        if (c == null) {
            return Animations.SWORD_GUARD_HIT;
        }
        return switch (c) {
            case SWORD -> Animations.SWORD_GUARD_HIT;
            case LONGSWORD -> Animations.LONGSWORD_GUARD_HIT;
            case GREATSWORD -> Animations.GREATSWORD_GUARD_HIT;
            case UCHIGATANA -> Animations.UCHIGATANA_GUARD_HIT;
            case SPEAR -> Animations.SPEAR_GUARD_HIT;
            case TACHI -> Animations.LONGSWORD_GUARD_HIT;
            case DAGGER -> Animations.SWORD_DUAL_GUARD_HIT;
            default -> Animations.SWORD_GUARD_HIT;
        };
    }

    /**
     * 弹反（华丽度下降时插入）：优先使用带 ACTIVE 标记的 GuardAnimation。
     */
    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> parry(WeaponCategory category) {
        CapabilityItem.WeaponCategories c = asEnum(category);
        if (c == null) {
            return Animations.SWORD_GUARD_ACTIVE_HIT1;
        }
        return switch (c) {
            case LONGSWORD, TACHI, GREATSWORD -> Animations.LONGSWORD_GUARD_ACTIVE_HIT1;
            case UCHIGATANA, SPEAR, DAGGER, AXE, FIST, TRIDENT -> Animations.SWORD_GUARD_ACTIVE_HIT2;
            default -> Animations.SWORD_GUARD_ACTIVE_HIT1;
        };
    }

    /**
     * 闪避动画（四种方向，索引：0前 1后 2左 3右）。
     */
    public static List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> dodgeMoves() {
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> list = new ArrayList<>(4);
        list.add(Animations.BIPED_STEP_FORWARD);
        list.add(Animations.BIPED_STEP_BACKWARD);
        list.add(Animations.BIPED_STEP_LEFT);
        list.add(Animations.BIPED_STEP_RIGHT);
        return list;
    }

    /**
     * 剑圣风格招式：大范围挥砍 / 强力突进 / 连段终结技。
     */
    public static List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> swordmasterMoves(WeaponCategory category) {
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> list = new ArrayList<>(4);
        CapabilityItem.WeaponCategories c = asEnum(category);
        if (c == null) {
            list.add(Animations.SWEEPING_EDGE);
            return list;
        }
        switch (c) {
            case SWORD -> {
                list.add(Animations.SWEEPING_EDGE);
                list.add(Animations.SWORD_DASH);
                list.add(Animations.SWORD_AUTO2);
                list.add(Animations.SWORD_AUTO3);
            }
            case LONGSWORD -> {
                list.add(Animations.SHARP_STAB);
                list.add(Animations.EVISCERATE_SECOND);
                list.add(Animations.LONGSWORD_AUTO2);
            }
            case GREATSWORD -> {
                list.add(Animations.GREATSWORD_AUTO1);
                list.add(Animations.GREATSWORD_AUTO2);
                list.add(Animations.GREATSWORD_DASH);
            }
            case UCHIGATANA -> {
                list.add(Animations.BATTOJUTSU);
                list.add(Animations.BATTOJUTSU_DASH);
                list.add(Animations.UCHIGATANA_AUTO2);
            }
            case TACHI -> {
                list.add(Animations.RUSHING_TEMPO1);
                list.add(Animations.RUSHING_TEMPO2);
                list.add(Animations.RUSHING_TEMPO3);
            }
            case SPEAR -> {
                list.add(Animations.GRASPING_SPIRAL_FIRST);
                list.add(Animations.HEARTPIERCER);
                list.add(Animations.SPEAR_TWOHAND_AUTO2);
            }
            case DAGGER -> {
                list.add(Animations.DAGGER_DUAL_AUTO3);
                list.add(Animations.DAGGER_DASH);
            }
            case AXE -> {
                list.add(Animations.AXE_AUTO2);
                list.add(Animations.AXE_DASH);
            }
            default -> {
                list.add(Animations.SWEEPING_EDGE);
                list.add(Animations.SWORD_DASH);
            }
        }
        return list;
    }

    /**
     * 枪神风格地面招式（无远程武器时的近战回退）：以快速突进 / 高频连击为主。
     */
    public static List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> gunslingerMoves(WeaponCategory category) {
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> list = new ArrayList<>(3);
        CapabilityItem.WeaponCategories c = asEnum(category);
        if (c == null) {
            list.add(Animations.SWORD_DASH);
            return list;
        }
        switch (c) {
            case SWORD -> {
                list.add(Animations.SWORD_DASH);
                list.add(Animations.SWORD_AUTO1);
            }
            case LONGSWORD -> {
                list.add(Animations.LONGSWORD_DASH);
                list.add(Animations.LONGSWORD_AUTO1);
            }
            case GREATSWORD -> {
                list.add(Animations.GREATSWORD_DASH);
                list.add(Animations.GREATSWORD_AUTO1);
            }
            case UCHIGATANA -> {
                list.add(Animations.UCHIGATANA_DASH);
                list.add(Animations.UCHIGATANA_AUTO1);
            }
            case TACHI -> {
                list.add(Animations.TACHI_DASH);
                list.add(Animations.TACHI_AUTO1);
            }
            case SPEAR -> {
                list.add(Animations.SPEAR_DASH);
                list.add(Animations.SPEAR_TWOHAND_AUTO1);
            }
            case DAGGER -> {
                list.add(Animations.DAGGER_DUAL_DASH);
                list.add(Animations.DAGGER_DUAL_AUTO1);
            }
            case AXE -> {
                list.add(Animations.AXE_DASH);
                list.add(Animations.AXE_AUTO1);
            }
            default -> {
                list.add(Animations.SWORD_DASH);
                list.add(Animations.SWORD_AUTO1);
            }
        }
        return list;
    }

    /**
     * 远程收尾动画：弓瞄准（Hold）与射击（Rebound）。
     */
    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> bowAim() {
        return Animations.BIPED_BOW_AIM;
    }

    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> bowShot() {
        return Animations.BIPED_BOW_SHOT;
    }

    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> crossbowShot() {
        return Animations.BIPED_CROSSBOW_SHOT;
    }

    // ------------------------------------------------------------------
    // 枪械（WOM 奇迹武器 Ender_Blaster / Nova 等）
    // 编译期不依赖 WOM：运行时按注册名从 AnimationManager 查找动画，
    // 未安装 WOM 时查找结果为 null，调用方自动回退。
    // ------------------------------------------------------------------

    private static final Map<String, AnimationManager.AnimationAccessor<? extends StaticAnimation>> GUN_ANIM_CACHE = new HashMap<>();

    /**
     * 查找枪械射击动画（按注册名），找不到返回 null。
     */
    private static AnimationManager.AnimationAccessor<? extends StaticAnimation> gunAnim(String path) {
        String key = "wom:" + path;
        return GUN_ANIM_CACHE.computeIfAbsent(key, k -> {
            try {
                return AnimationManager.byKey(net.minecraft.resources.ResourceLocation.parse(key));
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * 枪神风格枪械射击招式池（单手/双手两套，含多段射击）。
     * 覆盖 EnderBlaster（shoot 系列）与 Nova（attack 系列，注册为 TACHI 类别的枪械）。
     * 未安装 WOM（或动画未注册）时返回空列表。
     */
    public static List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> gunMoves(boolean twoHand) {
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> list = new ArrayList<>(4);
        if (twoHand) {
            addIfPresent(list, "biped/skill/enderblaster_twohand_shoot_2");
            addIfPresent(list, "biped/skill/enderblaster_twohand_shoot_3");
            addIfPresent(list, "biped/skill/enderblaster_twohand_shoot_4");
            addIfPresent(list, "biped/skill/enderblaster_twohand_shoot_dash");
        } else {
            addIfPresent(list, "biped/skill/enderblaster_onehand_shoot_2");
            addIfPresent(list, "biped/skill/enderblaster_onehand_shoot_3");
            addIfPresent(list, "biped/skill/enderblaster_onehand_shoot_2_forward");
            addIfPresent(list, "biped/skill/enderblaster_onehand_shoot_dash");
        }
        // Nova（单手/双手共用 attack 系列）
        addIfPresent(list, "biped/combat/nova_attack_2");
        addIfPresent(list, "biped/combat/nova_attack_3");
        addIfPresent(list, "biped/combat/nova_attack_dash");
        return list;
    }

    /**
     * 枪械收尾射击动画：优先 EnderBlaster 单发 / 双发，其次 Nova 攻击动画，找不到返回 null。
     */
    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> gunShot(boolean twoHand) {
        AnimationManager.AnimationAccessor<? extends StaticAnimation> shot = gunAnim(
                twoHand ? "biped/skill/enderblaster_twohand_shoot_2" : "biped/skill/enderblaster_onehand_shoot_2");
        if (shot == null) {
            shot = gunAnim("biped/skill/enderblaster_onehand_shoot_1");
        }
        if (shot == null) {
            shot = gunAnim("biped/combat/nova_attack_1");
        }
        return shot;
    }

    /**
     * 枪械近战体术招式池（EnderBlaster 的 AUTO / DASH 近战动作，非射击）。
     * 持枪械时"近战攻击"行动使用这套动画，而不是通用剑招。
     */
    public static List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> gunMeleeMoves(boolean twoHand) {
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> list = new ArrayList<>(4);
        if (twoHand) {
            addIfPresent(list, "biped/combat/enderblaster_twohand_auto_1");
            addIfPresent(list, "biped/combat/enderblaster_twohand_auto_2");
            addIfPresent(list, "biped/combat/enderblaster_twohand_auto_4");
            addIfPresent(list, "biped/combat/enderblaster_twohand_dash");
        } else {
            addIfPresent(list, "biped/combat/enderblaster_onehand_auto_1");
            addIfPresent(list, "biped/combat/enderblaster_onehand_auto_2");
            addIfPresent(list, "biped/combat/enderblaster_onehand_auto_4");
            addIfPresent(list, "biped/combat/enderblaster_onehand_dash");
        }
        return list;
    }

    private static void addIfPresent(List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> list, String path) {
        AnimationManager.AnimationAccessor<? extends StaticAnimation> anim = gunAnim(path);
        if (anim != null) {
            list.add(anim);
        }
    }
}
