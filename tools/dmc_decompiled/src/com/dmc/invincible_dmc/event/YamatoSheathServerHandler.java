package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.forgeevent.YamatoSheathEvent;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public final class YamatoSheathServerHandler {
   private static final float RESTART_EPSILON = 1.0E-4F;
   private static final Map<UUID, YamatoSheathServerHandler.PlaybackState> PLAYBACK_STATES = new HashMap<>();

   private YamatoSheathServerHandler() {
   }

   public static void handleNotification(ServerPlayer player, ResourceLocation requestedAnimation) {
      AnimationAccessor<? extends StaticAnimation> accessor = AnimationManager.byKey(requestedAnimation);
      YamatoAttackAnimation.SheathConfig config = accessor != null ? YamatoAttackAnimation.getSheathConfig(accessor) : null;
      if (accessor != null && config != null) {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
         if (playerPatch != null) {
            AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(playerPatch);
            YamatoSheathServerHandler.PlaybackState playbackState = observePlayback(player.m_20148_(), animationPlayer);
            if (requestedAnimation.equals(playbackState.animation())) {
               if (playbackState.markProcessed()) {
                  YamatoSheathEvent.Server event = new YamatoSheathEvent.Server(playerPatch, config.time(), config.sound(), accessor);
                  if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Result.DENY) {
                     player.m_9236_()
                        .m_6263_(null, player.m_20185_(), player.m_20186_(), player.m_20189_(), (SoundEvent)config.sound().get(), player.m_5720_(), 1.0F, 1.0F);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.player instanceof ServerPlayer player) {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
         AnimationPlayer animationPlayer = playerPatch != null ? DMCAnimationUtils.getMainPlayer(playerPatch) : null;
         observePlayback(player.m_20148_(), animationPlayer);
      }
   }

   @SubscribeEvent
   public static void onLogout(PlayerLoggedOutEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         PLAYBACK_STATES.remove(player.m_20148_());
      }
   }

   @SubscribeEvent
   public static void onChangedDimension(PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         PLAYBACK_STATES.remove(player.m_20148_());
      }
   }

   @SubscribeEvent
   public static void onDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         PLAYBACK_STATES.remove(player.m_20148_());
      }
   }

   private static YamatoSheathServerHandler.PlaybackState observePlayback(UUID playerId, @Nullable AnimationPlayer animationPlayer) {
      ResourceLocation animation = getCurrentAnimation(animationPlayer);
      float elapsedTime = animationPlayer != null ? animationPlayer.getElapsedTime() : -1.0F;
      YamatoSheathServerHandler.PlaybackState playbackState = PLAYBACK_STATES.computeIfAbsent(
         playerId, ignored -> new YamatoSheathServerHandler.PlaybackState()
      );
      playbackState.observe(animation, elapsedTime);
      return playbackState;
   }

   @Nullable
   private static ResourceLocation getCurrentAnimation(@Nullable AnimationPlayer animationPlayer) {
      if (animationPlayer != null && !animationPlayer.isEmpty()) {
         DynamicAnimation animation = DMCAnimationUtils.getCurrentAnimation(animationPlayer);
         AssetAccessor<? extends StaticAnimation> realAnimation = DMCAnimationUtils.getRealAnimationAccessor(animation);
         if (realAnimation != null && realAnimation.registryName() != null) {
            return realAnimation.registryName();
         } else {
            return animation.getAccessor() != null ? animation.getAccessor().registryName() : null;
         }
      } else {
         return null;
      }
   }

   private static final class PlaybackState {
      @Nullable
      private ResourceLocation animation;
      private float elapsedTime = -1.0F;
      private long generation;
      private long processedGeneration = Long.MIN_VALUE;

      private void observe(@Nullable ResourceLocation currentAnimation, float currentElapsedTime) {
         boolean animationChanged = !Objects.equals(this.animation, currentAnimation);
         boolean animationRestarted = !animationChanged
            && currentAnimation != null
            && this.elapsedTime >= 0.0F
            && currentElapsedTime + 1.0E-4F < this.elapsedTime;
         if (animationChanged || animationRestarted) {
            this.generation++;
         }

         this.animation = currentAnimation;
         this.elapsedTime = currentElapsedTime;
      }

      @Nullable
      private ResourceLocation animation() {
         return this.animation;
      }

      private boolean markProcessed() {
         if (this.processedGeneration == this.generation) {
            return false;
         } else {
            this.processedGeneration = this.generation;
            return true;
         }
      }
   }
}
