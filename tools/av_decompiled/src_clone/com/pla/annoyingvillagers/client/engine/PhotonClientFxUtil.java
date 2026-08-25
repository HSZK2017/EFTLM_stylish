package com.pla.annoyingvillagers.client.engine;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   modid = "annoyingvillagers",
   value = {Dist.CLIENT}
)
public final class PhotonClientFxUtil {
   private static final String PHOTON_MOD_ID = "photon";
   private static final int STALE_TICKS = 5;
   private static final Map<String, PhotonClientFxUtil.ActiveEffect> ACTIVE_EFFECTS = new HashMap<>();
   private static PhotonClientFxUtil.Reflection reflection;
   private static boolean reflectionFailed;
   private static boolean warnedReflectionFailure;

   private PhotonClientFxUtil() {
   }

   public static boolean isLoaded() {
      return ModList.get().isLoaded("photon") && reflection() != null;
   }

   public static boolean hasPhotonMod() {
      return ModList.get().isLoaded("photon");
   }

   public static ResourceLocation photon(String path) {
      return ResourceLocation.fromNamespaceAndPath("photon", path);
   }

   public static boolean spawnAt(Level level, String effectPath, Vec3 pos) {
      return spawnAt(level, effectPath, pos, new Quaternionf(), new Vector3f(1.0F, 1.0F, 1.0F));
   }

   public static boolean spawnDirectional(Level level, String effectPath, Vec3 from, Vec3 to, boolean flip) {
      return from != null && to != null ? spawnAt(level, effectPath, from, rotationFromTo(from, to, flip), new Vector3f(1.0F, 1.0F, 1.0F)) : false;
   }

   public static boolean spawnPortal(Level level, String effectPath, Vec3 pos, Vec3 normal) {
      return spawnAt(level, effectPath, pos, portalRotation(normal), unitScale());
   }

   public static boolean spawnAt(Level level, String effectPath, Vec3 pos, Quaternionf rotation, Vector3f scale) {
      if (canUse(level) && pos != null) {
         try {
            PhotonClientFxUtil.Reflection r = reflection();
            if (r == null) {
               return false;
            } else {
               Object fx = r.getFx.invoke(null, photon(effectPath));
               if (fx == null) {
                  return false;
               } else {
                  BlockPos blockPos = BlockPos.m_274446_(pos);
                  Object effect = r.blockEffectConstructor.newInstance(fx, level, blockPos);
                  r.setOffset.invoke(effect, offsetFromBlockCenter(pos, blockPos));
                  r.setRotation.invoke(effect, rotation);
                  r.setScale.invoke(effect, scale);
                  r.setAllowMulti.invoke(effect, true);
                  r.blockStart.invoke(effect);
                  return true;
               }
            }
         } catch (RuntimeException | ReflectiveOperationException var9) {
            warnReflectionFailure(var9);
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean followPortal(
      String key, Level level, String effectPath, Supplier<Vec3> positionSupplier, Supplier<Vec3> normalSupplier, int lifetimeTicks
   ) {
      if (canUse(level) && positionSupplier != null && normalSupplier != null) {
         try {
            PhotonClientFxUtil.Reflection r = reflection();
            if (r == null) {
               return false;
            } else {
               Vec3 pos = positionSupplier.get();
               if (pos == null) {
                  return false;
               } else {
                  Quaternionf rotation = portalRotation(normalSupplier.get());
                  Vector3f scale = unitScale();
                  long now = level.m_46467_();
                  String normalizedKey = normalizeKey(key, effectPath);
                  PhotonClientFxUtil.ActiveEffect active = ACTIVE_EFFECTS.get(normalizedKey);
                  if (active == null || active.level != level || !active.isAlive(r)) {
                     active = createRuntimeEffect(r, level, effectPath, pos, rotation, scale, now, lifetimeTicks);
                     if (active == null) {
                        return false;
                     }

                     ACTIVE_EFFECTS.put(normalizedKey, active);
                  }

                  active.positionSupplier = positionSupplier;
                  active.rotationSupplier = () -> portalRotation(normalSupplier.get());
                  active.scaleSupplier = PhotonClientFxUtil::unitScale;
                  active.updateTransform(r, pos, rotation, scale, now, lifetimeTicks);
                  return true;
               }
            }
         } catch (RuntimeException | ReflectiveOperationException var14) {
            warnReflectionFailure(var14);
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean followPosition(String key, Level level, String effectPath, Supplier<Vec3> positionSupplier, int lifetimeTicks) {
      if (canUse(level) && positionSupplier != null) {
         try {
            PhotonClientFxUtil.Reflection r = reflection();
            if (r == null) {
               return false;
            } else {
               Vec3 pos = positionSupplier.get();
               if (pos == null) {
                  return false;
               } else {
                  long now = level.m_46467_();
                  String normalizedKey = normalizeKey(key, effectPath);
                  PhotonClientFxUtil.ActiveEffect active = ACTIVE_EFFECTS.get(normalizedKey);
                  if (active == null || active.level != level || !active.isAlive(r)) {
                     active = createRuntimeEffect(r, level, effectPath, pos, new Quaternionf(), unitScale(), now, lifetimeTicks);
                     if (active == null) {
                        return false;
                     }

                     ACTIVE_EFFECTS.put(normalizedKey, active);
                  }

                  active.positionSupplier = positionSupplier;
                  active.rotationSupplier = null;
                  active.scaleSupplier = null;
                  active.updatePosition(r, pos, now, lifetimeTicks);
                  return true;
               }
            }
         } catch (RuntimeException | ReflectiveOperationException var11) {
            warnReflectionFailure(var11);
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean followBeam(
      String key,
      Level level,
      String effectPath,
      Entity owner,
      PhotonClientFxUtil.BeamPositionProvider startProvider,
      PhotonClientFxUtil.BeamPositionProvider endProvider,
      BooleanSupplier aliveSupplier,
      int lifetimeTicks
   ) {
      return followBeam(
         key, level, effectPath, owner, startProvider, endProvider, aliveSupplier, PhotonClientFxUtil.BeamForwardAxis.POSITIVE_Z, 0.0F, lifetimeTicks
      );
   }

   public static boolean followBeam(
      String key,
      Level level,
      String effectPath,
      Entity owner,
      PhotonClientFxUtil.BeamPositionProvider startProvider,
      PhotonClientFxUtil.BeamPositionProvider endProvider,
      BooleanSupplier aliveSupplier,
      PhotonClientFxUtil.BeamForwardAxis forwardAxis,
      float visualBaseLength,
      int lifetimeTicks
   ) {
      if (canUse(level) && owner != null && startProvider != null && endProvider != null && aliveSupplier != null) {
         try {
            return PhotonBeamEffect.startOrUpdate(
               photon(effectPath),
               level,
               owner,
               normalizeKey(key, effectPath),
               startProvider,
               endProvider,
               aliveSupplier,
               forwardAxis == null ? PhotonClientFxUtil.BeamForwardAxis.POSITIVE_Z : forwardAxis,
               visualBaseLength,
               lifetimeTicks
            );
         } catch (RuntimeException | LinkageError var11) {
            warnReflectionFailure(var11);
            return false;
         }
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END && !ACTIVE_EFFECTS.isEmpty()) {
         Level level = Minecraft.m_91087_().f_91073_;
         if (level == null) {
            destroyAll();
         } else {
            PhotonClientFxUtil.Reflection r = reflection();
            if (r == null) {
               destroyAll();
            } else {
               long now = level.m_46467_();
               Iterator<Entry<String, PhotonClientFxUtil.ActiveEffect>> iterator = ACTIVE_EFFECTS.entrySet().iterator();

               while (iterator.hasNext()) {
                  PhotonClientFxUtil.ActiveEffect active = iterator.next().getValue();
                  if (active.level == level && active.positionSupplier != null && !active.tickFollow(r, now)) {
                     active.destroy(r);
                     iterator.remove();
                  } else if (active.level != level || now >= active.expireTick || !active.isAlive(r)) {
                     active.destroy(r);
                     iterator.remove();
                  }
               }
            }
         }
      }
   }

   private static PhotonClientFxUtil.ActiveEffect createRuntimeEffect(
      PhotonClientFxUtil.Reflection r, Level level, String effectPath, Vec3 pos, Quaternionf rotation, Vector3f scale, long now, int lifetimeTicks
   ) throws ReflectiveOperationException {
      Object fx = r.getFx.invoke(null, photon(effectPath));
      if (fx == null) {
         return null;
      } else {
         BlockPos blockPos = BlockPos.m_274446_(pos);
         Object effect = r.blockEffectConstructor.newInstance(fx, level, blockPos);
         r.setOffset.invoke(effect, offsetFromBlockCenter(pos, blockPos));
         r.setRotation.invoke(effect, rotation);
         r.setScale.invoke(effect, scale);
         r.setAllowMulti.invoke(effect, true);
         r.blockStart.invoke(effect);
         Object runtime = r.getRuntime.invoke(effect);
         if (runtime == null) {
            return null;
         } else {
            Object root = r.getRoot.invoke(runtime);
            if (root == null) {
               return null;
            } else {
               PhotonClientFxUtil.ActiveEffect active = new PhotonClientFxUtil.ActiveEffect(level, runtime, root, now);
               active.expireTick = now + (long)Math.max(lifetimeTicks, 5);
               return active;
            }
         }
      }
   }

   private static boolean canUse(Level level) {
      return level != null && level.f_46443_ && ModList.get().isLoaded("photon");
   }

   private static String normalizeKey(String key, String effectPath) {
      return key + ":" + effectPath.toLowerCase(Locale.ROOT);
   }

   private static void destroyAll() {
      PhotonClientFxUtil.Reflection r = reflection();
      if (r != null) {
         ACTIVE_EFFECTS.values().forEach(active -> active.destroy(r));
      }

      ACTIVE_EFFECTS.clear();
   }

   private static Vector3f offsetFromBlockCenter(Vec3 pos, BlockPos blockPos) {
      return new Vector3f(
         (float)(pos.f_82479_ - (double)blockPos.m_123341_() - 0.5),
         (float)(pos.f_82480_ - (double)blockPos.m_123342_() - 0.5),
         (float)(pos.f_82481_ - (double)blockPos.m_123343_() - 0.5)
      );
   }

   private static Quaternionf rotationFromTo(Vec3 from, Vec3 to, boolean flip) {
      Vec3 delta = to.m_82546_(from);
      if (flip) {
         delta = delta.m_82490_(-1.0);
      }

      return rotationToward(delta);
   }

   private static Quaternionf portalRotation(Vec3 normal) {
      return rotationToward(normalizeOrDefault(normal));
   }

   private static Quaternionf rotationToward(Vec3 direction) {
      Vec3 delta = normalizeOrDefault(direction);
      double xz = Math.sqrt(delta.f_82479_ * delta.f_82479_ + delta.f_82481_ * delta.f_82481_);
      float yaw = (float)Math.atan2(delta.f_82479_, delta.f_82481_);
      float pitch = (float)(-Math.atan2(delta.f_82480_, xz));
      return new Quaternionf().rotateXYZ(pitch, yaw, 0.0F);
   }

   private static Vec3 normalizeOrDefault(Vec3 direction) {
      return direction != null && !(direction.m_82556_() < 1.0E-7) ? direction.m_82541_() : new Vec3(0.0, 0.0, 1.0);
   }

   private static Vector3f unitScale() {
      return new Vector3f(1.0F, 1.0F, 1.0F);
   }

   private static PhotonClientFxUtil.Reflection reflection() {
      if (reflection != null) {
         return reflection;
      } else if (!reflectionFailed && ModList.get().isLoaded("photon")) {
         try {
            Class<?> fxClass = Class.forName("com.lowdragmc.photon.client.fx.FX");
            Class<?> fxHelperClass = Class.forName("com.lowdragmc.photon.client.fx.FXHelper");
            Class<?> fxEffectClass = Class.forName("com.lowdragmc.photon.client.fx.FXEffect");
            Class<?> blockEffectClass = Class.forName("com.lowdragmc.photon.client.fx.BlockEffect");
            Class<?> fxRuntimeClass = Class.forName("com.lowdragmc.photon.client.fx.FXRuntime");
            Class<?> fxObjectClass = Class.forName("com.lowdragmc.photon.client.gameobject.IFXObject");
            reflection = new PhotonClientFxUtil.Reflection(
               fxHelperClass.getMethod("getFX", ResourceLocation.class),
               blockEffectClass.getConstructor(fxClass, Level.class, BlockPos.class),
               fxEffectClass.getMethod("setOffset", Vector3f.class),
               fxEffectClass.getMethod("setRotation", Quaternionf.class),
               fxEffectClass.getMethod("setScale", Vector3f.class),
               fxEffectClass.getMethod("setAllowMulti", boolean.class),
               fxEffectClass.getMethod("getRuntime"),
               blockEffectClass.getMethod("start"),
               fxRuntimeClass.getMethod("getRoot"),
               fxRuntimeClass.getMethod("isAlive"),
               fxRuntimeClass.getMethod("destroy", boolean.class),
               fxObjectClass.getMethod("updatePos", Vector3f.class),
               fxObjectClass.getMethod("updateRotation", Quaternionf.class),
               fxObjectClass.getMethod("updateScale", Vector3f.class)
            );
            return reflection;
         } catch (LinkageError | ReflectiveOperationException var6) {
            reflectionFailed = true;
            warnReflectionFailure(var6);
            return null;
         }
      } else {
         return null;
      }
   }

   private static void warnReflectionFailure(Throwable throwable) {
      if (!warnedReflectionFailure) {
         warnedReflectionFailure = true;
         AnnoyingVillagers.LOGGER.warn("Photon is loaded, but Annoying Villagers could not access Photon FX APIs.", throwable);
      }
   }

   private static final class ActiveEffect {
      private final Level level;
      private final Object runtime;
      private final Object root;
      private Supplier<Vec3> positionSupplier;
      private Supplier<Quaternionf> rotationSupplier;
      private Supplier<Vector3f> scaleSupplier;
      private long lastUpdateTick;
      private long expireTick;

      private ActiveEffect(Level level, Object runtime, Object root, long now) {
         this.level = level;
         this.runtime = runtime;
         this.root = root;
         this.lastUpdateTick = now;
         this.expireTick = now + 5L;
      }

      private void updatePosition(PhotonClientFxUtil.Reflection r, Vec3 pos, long now, int lifetimeTicks) throws ReflectiveOperationException {
         this.updateTransform(r, pos, null, PhotonClientFxUtil.unitScale(), now, lifetimeTicks);
      }

      private void updateTransform(PhotonClientFxUtil.Reflection r, Vec3 pos, Quaternionf rotation, Vector3f scale, long now, int lifetimeTicks) throws ReflectiveOperationException {
         r.updatePos.invoke(this.root, new Vector3f((float)pos.f_82479_, (float)pos.f_82480_, (float)pos.f_82481_));
         if (rotation != null) {
            r.updateRotation.invoke(this.root, rotation);
         }

         r.updateScale.invoke(this.root, scale == null ? PhotonClientFxUtil.unitScale() : scale);
         this.lastUpdateTick = now;
         this.expireTick = now + (long)Math.max(lifetimeTicks, 5);
      }

      private boolean tickFollow(PhotonClientFxUtil.Reflection r, long now) {
         try {
            Vec3 pos = this.positionSupplier.get();
            if (pos == null) {
               return false;
            } else {
               Quaternionf rotation = this.rotationSupplier == null ? null : this.rotationSupplier.get();
               Vector3f scale = this.scaleSupplier == null ? PhotonClientFxUtil.unitScale() : this.scaleSupplier.get();
               long previousExpireTick = this.expireTick;
               this.updateTransform(r, pos, rotation, scale, now, 5);
               this.expireTick = previousExpireTick;
               return true;
            }
         } catch (RuntimeException | ReflectiveOperationException var9) {
            return false;
         }
      }

      private boolean isAlive(PhotonClientFxUtil.Reflection r) {
         try {
            return Boolean.TRUE.equals(r.isAlive.invoke(this.runtime));
         } catch (RuntimeException | ReflectiveOperationException var3) {
            return false;
         }
      }

      private void destroy(PhotonClientFxUtil.Reflection r) {
         try {
            r.destroy.invoke(this.runtime, false);
         } catch (RuntimeException | ReflectiveOperationException var3) {
         }
      }
   }

   public static enum BeamForwardAxis {
      POSITIVE_Z,
      POSITIVE_X;
   }

   @FunctionalInterface
   public interface BeamPositionProvider {
      Vec3 get(float var1);
   }

   private static record Reflection(
      Method getFx,
      Constructor<?> blockEffectConstructor,
      Method setOffset,
      Method setRotation,
      Method setScale,
      Method setAllowMulti,
      Method getRuntime,
      Method blockStart,
      Method getRoot,
      Method isAlive,
      Method destroy,
      Method updatePos,
      Method updateRotation,
      Method updateScale
   ) {
   }
}
