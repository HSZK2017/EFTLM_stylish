package com.Yujin.onegradefixer.epicmoonmod.item;

import com.Yujin.onegradefixer.epicmoonmod.client.LeiHengArmorRenderer;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.Animation.LoopType;
import software.bernie.geckolib.core.object.PlayState;

public class LeiHengArmorItem extends ArmorItem implements GeoItem {
   private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

   public LeiHengArmorItem(final ArmorMaterial pMaterial, Type pType, Properties pProperties) {
      super(new ArmorMaterial() {
         public int m_266425_(Type pType) {
            return pMaterial.m_266425_(pType);
         }

         public int m_7366_(Type pType) {
            return switch (pType) {
               case HELMET -> 4;
               case CHESTPLATE -> 9;
               case LEGGINGS -> 7;
               case BOOTS -> 4;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_6646_() {
            return pMaterial.m_6646_();
         }

         public SoundEvent m_7344_() {
            return pMaterial.m_7344_();
         }

         public Ingredient m_6230_() {
            return pMaterial.m_6230_();
         }

         public String m_6082_() {
            return pMaterial.m_6082_();
         }

         public float m_6651_() {
            return 4.0F;
         }

         public float m_6649_() {
            return 0.15F;
         }
      }, pType, pProperties);
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         private LeiHengArmorRenderer leiHengArmorRenderer;

         @NotNull
         public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
            if (this.leiHengArmorRenderer == null) {
               this.leiHengArmorRenderer = new LeiHengArmorRenderer();
            }

            this.leiHengArmorRenderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
            return this.leiHengArmorRenderer;
         }
      });
   }

   private PlayState predicate(AnimationState animationState) {
      animationState.getController().setAnimation(RawAnimation.begin().then("idle", LoopType.LOOP));
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "controller", 0, this::predicate)});
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
