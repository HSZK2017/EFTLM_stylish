package org.merlin204.mimic.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.merlin204.mimic.entity.proteus.ProteusEntity;
import org.merlin204.mimic.entity.shadow.PlayerShadowMimicEntity;
import org.merlin204.mimic.entity.shadow.ShadowMimicEntity;

public class MimicEntities {
   public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "mimic");
   public static final RegistryObject<EntityType<ProteusEntity>> PROTEUS = register(
      "proteus", Builder.m_20704_(ProteusEntity::new, MobCategory.MISC).m_20699_(0.8F, 2.0F).m_20702_(64).m_20717_(1)
   );
   public static final RegistryObject<EntityType<ShadowMimicEntity>> SHADOW_MIMIC = register(
      "shadow_mimic", Builder.m_20704_(ShadowMimicEntity::new, MobCategory.MISC).m_20699_(0.8F, 2.0F).m_20702_(64).m_20717_(1)
   );
   public static final RegistryObject<EntityType<PlayerShadowMimicEntity>> PLAYER_SHADOW_MIMIC = register(
      "player_shadow_mimic", Builder.m_20704_(PlayerShadowMimicEntity::new, MobCategory.MISC).m_20699_(0.8F, 2.0F).m_20702_(64).m_20717_(1)
   );

   private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Builder<T> entityTypeBuilder) {
      return ENTITIES.register(name, () -> entityTypeBuilder.m_20712_(ResourceLocation.fromNamespaceAndPath("mimic", name).toString()));
   }
}
