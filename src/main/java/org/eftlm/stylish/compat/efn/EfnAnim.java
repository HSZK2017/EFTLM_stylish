package org.eftlm.stylish.compat.efn;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.HashMap;
import java.util.Map;

/**
 * EFN（史诗战斗·夜幕）动画查找。
 * <p>
 * 编译期不依赖 EFN：按注册名从 AnimationManager 运行时查找动画，
 * 未安装 EFN 时查找结果为 null，调用方自动回退 / 跳过。
 */
public final class EfnAnim {

    private static final Map<String, AnimationManager.AnimationAccessor<? extends StaticAnimation>> CACHE = new HashMap<>();

    private EfnAnim() {
    }

    /**
     * 查找 EFN 动画（路径为 EFN 动画注册类的 nextAccessor 路径，如
     * "biped/yamato/dmcyamato_judgementcut_all"），找不到返回 null。
     */
    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> byKey(String path) {
        String key = "efn:" + path;
        return CACHE.computeIfAbsent(key, k -> {
            try {
                return AnimationManager.byKey(net.minecraft.resources.ResourceLocation.parse(key));
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * 按完整动画键（含命名空间，如 "efn:biped/yamato/dmcyamato_drive" 或
     * "efn_enhance:biped/sekiro/kusabimaru/ichimonji_1"）查找动画，找不到返回 null。
     */
    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> byFullKey(String fullKey) {
        return CACHE.computeIfAbsent(fullKey, k -> {
            try {
                return AnimationManager.byKey(net.minecraft.resources.ResourceLocation.parse(k));
            } catch (Exception e) {
                return null;
            }
        });
    }
}
