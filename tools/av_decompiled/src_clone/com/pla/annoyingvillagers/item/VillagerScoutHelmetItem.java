package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.client.model.ModelVillagerScoutHelmet;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
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

public abstract class VillagerScoutHelmetItem extends ArmorItem {
   public VillagerScoutHelmetItem(Type type, Properties properties) {
      super(new ArmorMaterial() {
         public int m_266425_(Type type) {
            return switch (type) {
               case BOOTS -> 325;
               case LEGGINGS -> 375;
               case CHESTPLATE -> 400;
               case HELMET -> 275;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_7366_(Type type) {
            return switch (type) {
               case BOOTS -> 1;
               case LEGGINGS -> 3;
               case CHESTPLATE -> 5;
               case HELMET -> 4;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_6646_() {
            return 9;
         }

         public SoundEvent m_7344_() {
            return SoundEvents.f_11675_;
         }

         public Ingredient m_6230_() {
            return Ingredient.m_151265_();
         }

         public String m_6082_() {
            return "villager_scout_helmet";
         }

         public float m_6651_() {
            return 1.0F;
         }

         public float m_6649_() {
            return 0.0F;
         }
      }, type, properties);
   }

   public static class Helmet extends VillagerScoutHelmetItem {
      public Helmet() {
         super(Type.HELMET, new Properties());
      }

      public void initializeClient(Consumer<IClientItemExtensions> consumer) {
         consumer.accept(
            new IClientItemExtensions() {
               HumanoidModel<LivingEntity> armorModel = null;

               @NotNull
               public HumanoidModel<?> getHumanoidArmorModel(
                  LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original
               ) {
                  if (this.armorModel == null) {
                     ModelVillagerScoutHelmet<?> helmetModel = new ModelVillagerScoutHelmet(
                        Minecraft.m_91087_().m_167973_().m_171103_(ModelVillagerScoutHelmet.LAYER_LOCATION)
                     );
                     ModelPart root = new ModelPart(
                        Collections.emptyList(),
                        Map.of(
                           "head",
                           helmetModel.Head,
                           "hat",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "body",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_leg",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_leg",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap())
                        )
                     );
                     this.armorModel = new HumanoidModel(root);
                  }

                  this.armorModel.f_102817_ = livingEntity.m_6047_();
                  this.armorModel.f_102609_ = livingEntity.m_20159_();
                  this.armorModel.f_102610_ = livingEntity.m_6162_();
                  return this.armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/villager_scout_layer.png";
      }
   }
}
