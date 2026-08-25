package org.eftlm.stylish.compat.wom;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.eftlm.stylish.EFTLMStylish;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.api.animation.ServerAnimator;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mod.EventBusSubscriber(modid = org.eftlm.stylish.EFTLMStylish.MODID)
public class SolarModelHandler {
    @SubscribeEvent
    public static void onMaidTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof EntityMaid maid)) return;
        if (maid.level().isClientSide()) return;
        // WOM 为可选依赖（mods.toml 未声明）：未安装时 WOMItems 引用解析抛
        // NoClassDefFoundError，会刷爆事件总线错误日志，必须先守卫
        if (!WomSkillChecks.LoadedWOM()) return;

        MaidPatch patch = EpicFightCapabilities.getEntityPatch(maid, MaidPatch.class);
        if (patch == null) return;

        ItemStack stack = maid.getMainHandItem();
        boolean hasTag = stack.hasTag() && stack.getTag().contains("obscuridad");

        if (stack.getItem() != WOMItems.SOLAR.get()) {
            if (hasTag) {
                stack.getTag().remove("obscuridad");
                maid.setItemSlot(EquipmentSlot.MAINHAND, stack);
            }
            return;
        }

        boolean isObscuridad = false;
        if (patch.getAnimator() instanceof ServerAnimator s) {
            var accessor = s.animationPlayer.getAnimation();
            isObscuridad = accessor != null && accessor.isPresent()
                && accessor.registryName() != null
                && accessor.registryName().getPath().contains("obscuridad");
        }

        if (isObscuridad != hasTag) {
            if (isObscuridad) {
                stack.getOrCreateTag().putInt("obscuridad", 1);
            } else {
                stack.getTag().remove("obscuridad");
            }
            maid.setItemSlot(EquipmentSlot.MAINHAND, stack);
        }
    }
}
