package com.dmc.invincible_dmc.api.forgeevent;

import com.dmc.invincible_dmc.mixin.epicfight.AnimationManagerAccessor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;

public class DuplicateAnimationRegistryEvent extends Event implements IModBusEvent {
   private final List<DuplicateAnimationRegistryEvent.DuplicateAnimationBuilder> builders = new ArrayList<>();
   private final Set<String> namespaces = new HashSet<>();

   public static AnimationManagerAccessor getInstance() {
      return (AnimationManagerAccessor)AnimationManager.getInstance();
   }

   public void newBuilder(String namespace, Consumer<DuplicateAnimationRegistryEvent.DuplicateAnimationBuilder> build) {
      if (this.namespaces.contains(namespace)) {
         throw new IllegalArgumentException("Animation builder namespace '" + namespace + "' already exists!");
      } else {
         this.namespaces.add(namespace);
         this.builders.add(new DuplicateAnimationRegistryEvent.DuplicateAnimationBuilder(namespace, build));
      }
   }

   public List<DuplicateAnimationRegistryEvent.DuplicateAnimationBuilder> getBuilders() {
      return this.builders;
   }

   public static record DuplicateAnimationAccessorImpl<A extends StaticAnimation>(
      ResourceLocation originalAnimationName, ResourceLocation registryName, int id, boolean inRegistry, Function<AnimationAccessor<A>, A> onLoad
   ) implements AnimationAccessor<A> {
      private static <A extends StaticAnimation> AnimationAccessor<A> create(
         ResourceLocation originalAnimationName, ResourceLocation registryName, int id, boolean inRegistry, Function<AnimationAccessor<A>, A> onLoad
      ) {
         return new DuplicateAnimationRegistryEvent.DuplicateAnimationAccessorImpl<>(originalAnimationName, registryName, id, inRegistry, onLoad);
      }

      public A get() {
         if (!DuplicateAnimationRegistryEvent.getInstance().getAnimations().containsKey(this)) {
            A anim = this.onLoad.apply(this);
            anim.setResourceLocation(this.originalAnimationName.m_135827_(), this.originalAnimationName.m_135815_());

            try {
               Field filehashField = StaticAnimation.class.getDeclaredField("filehash");
               filehashField.setAccessible(true);
               filehashField.set(anim, StaticAnimation.getFileHash(anim.getLocation()));
            } catch (Exception var3) {
            }

            DuplicateAnimationRegistryEvent.getInstance().getAnimations().put(this, anim);
         }

         return (A)DuplicateAnimationRegistryEvent.getInstance().getAnimations().get(this);
      }

      @Override
      public String toString() {
         return this.registryName.toString();
      }

      @Override
      public int hashCode() {
         return this.registryName.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj instanceof AnimationAccessor<?> armatureAccessor) {
            return this.registryName.equals(armatureAccessor.registryName());
         } else if (obj instanceof ResourceLocation rl) {
            return this.registryName.equals(rl);
         } else {
            return obj instanceof String name ? this.registryName.toString().equals(name) : false;
         }
      }
   }

   public static record DuplicateAnimationBuilder(String namespace, Consumer<DuplicateAnimationRegistryEvent.DuplicateAnimationBuilder> task) {
      public <T extends StaticAnimation> AnimationAccessor<T> nextAccessor(AnimationAccessor<?> original, Function<AnimationAccessor<T>, T> onLoad) {
         ResourceLocation selfName = ResourceLocation.fromNamespaceAndPath(this.namespace, original.registryName().m_135815_());
         AnimationAccessor<T> accessor = DuplicateAnimationRegistryEvent.DuplicateAnimationAccessorImpl.create(
            original.registryName(), selfName, DuplicateAnimationRegistryEvent.getInstance().getAnimations().size() + 1, true, onLoad
         );
         DuplicateAnimationRegistryEvent.getInstance().getAnimationById().put(accessor.id(), accessor);
         DuplicateAnimationRegistryEvent.getInstance().getAnimationByName().put(selfName, accessor);
         DuplicateAnimationRegistryEvent.getInstance().getAnimations().put(accessor, null);
         return accessor;
      }
   }
}
