package com.pla.annoyingvillagers.client.engine;

import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.lowdragmc.photon.client.fx.EntityEffect.AutoRotate;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import com.lowdragmc.photon.client.gameobject.emitter.beam.BeamConfig;
import com.lowdragmc.photon.client.gameobject.emitter.beam.BeamEmitter;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
final class PhotonBeamEffect extends EntityEffect {
   private static final Map<String, PhotonBeamEffect> ACTIVE = new HashMap<>();
   private static final double FOLLOW_SMOOTHING = 0.55;
   private static final double SNAP_DISTANCE_SQR = 64.0;
   private static final float MIN_VISUAL_SCALE = 1.0E-4F;
   private final String key;
   private final Map<BeamEmitter, Vector3f> originalBeamEnds = new IdentityHashMap<>();
   private PhotonClientFxUtil.BeamPositionProvider startProvider;
   private PhotonClientFxUtil.BeamPositionProvider endProvider;
   private BooleanSupplier aliveSupplier;
   private PhotonClientFxUtil.BeamForwardAxis forwardAxis;
   private float visualBaseLength;
   private Vec3 lastGoodStart;
   private Vec3 lastGoodEnd;
   private Vec3 smoothedStart;
   private Vec3 smoothedEnd;
   private long expireTick;

   private PhotonBeamEffect(
      FX fx,
      Level level,
      Entity owner,
      String key,
      PhotonClientFxUtil.BeamPositionProvider startProvider,
      PhotonClientFxUtil.BeamPositionProvider endProvider,
      BooleanSupplier aliveSupplier,
      PhotonClientFxUtil.BeamForwardAxis forwardAxis,
      float visualBaseLength,
      int lifetimeTicks
   ) {
      super(fx, level, owner, AutoRotate.NONE);
      this.key = key;
      this.startProvider = startProvider;
      this.endProvider = endProvider;
      this.aliveSupplier = aliveSupplier;
      this.forwardAxis = forwardAxis;
      this.visualBaseLength = Math.max(visualBaseLength, 0.0F);
      this.expireTick = lifetimeTicks > 0 ? level.m_46467_() + (long)lifetimeTicks : Long.MAX_VALUE;
   }

   static boolean startOrUpdate(
      ResourceLocation fxLocation,
      Level level,
      Entity owner,
      String key,
      PhotonClientFxUtil.BeamPositionProvider startProvider,
      PhotonClientFxUtil.BeamPositionProvider endProvider,
      BooleanSupplier aliveSupplier,
      PhotonClientFxUtil.BeamForwardAxis forwardAxis,
      float visualBaseLength,
      int lifetimeTicks
   ) {
      FX fx = FXHelper.getFX(fxLocation);
      if (fx != null && level != null && owner != null && owner.m_6084_() && startProvider != null && endProvider != null && aliveSupplier != null) {
         cleanupDead();
         PhotonBeamEffect active = ACTIVE.get(key);
         if (active != null
            && active.level == level
            && active.entity == owner
            && active.runtime != null
            && active.runtime.isAlive()
            && Objects.equals(active.fx.getFxLocation(), fx.getFxLocation())) {
            active.startProvider = startProvider;
            active.endProvider = endProvider;
            active.aliveSupplier = aliveSupplier;
            active.forwardAxis = forwardAxis;
            active.visualBaseLength = Math.max(visualBaseLength, 0.0F);
            active.expireTick = lifetimeTicks > 0 ? level.m_46467_() + (long)lifetimeTicks : Long.MAX_VALUE;
            return active.cacheBeamTarget(1.0F) != null;
         } else {
            PhotonBeamEffect effect = new PhotonBeamEffect(
               fx, level, owner, key, startProvider, endProvider, aliveSupplier, forwardAxis, visualBaseLength, lifetimeTicks
            );
            effect.setAllowMulti(true);
            effect.start();
            return effect.runtime != null && effect.runtime.isAlive();
         }
      } else {
         return false;
      }
   }

   public void updateFXObjectTick(IFXObject object) {
      if (this.runtime != null && object == this.runtime.getRoot()) {
         if (!this.entity.m_6084_() || !this.isSourceAlive()) {
            this.stop();
         }
      }
   }

   public void updateFXObjectFrame(IFXObject object, float partialTicks) {
      if (this.runtime != null && object == this.runtime.getRoot()) {
         if (!this.isSourceAlive() || !this.applyBeamTransform(partialTicks)) {
            this.stop();
         }
      }
   }

   public void start() {
      if (this.entity.m_6084_() && this.isSourceAlive() && this.cacheBeamTarget(1.0F) != null) {
         PhotonBeamEffect previous = ACTIVE.remove(this.key);
         if (previous != null) {
            previous.stop();
         }

         super.start();
         if (this.runtime != null) {
            if (!this.applyBeamTransform(this.runtime.getRoot(), 1.0F)) {
               this.stop();
            } else {
               ACTIVE.put(this.key, this);
            }
         }
      }
   }

   private boolean isSourceAlive() {
      return this.level != null && this.level.f_46443_ && this.level.m_46467_() <= this.expireTick && this.aliveSupplier.getAsBoolean();
   }

   private boolean applyBeamTransform(float partialTicks) {
      return this.runtime != null && this.applyBeamTransform(this.runtime.getRoot(), partialTicks);
   }

   private boolean applyBeamTransform(IFXObject root, float partialTicks) {
      PhotonBeamEffect.BeamEndpoints target = this.cacheBeamTarget(partialTicks);
      if (root != null && target != null) {
         this.smoothedStart = smooth(this.smoothedStart, target.start());
         this.smoothedEnd = smooth(this.smoothedEnd, target.end());
         PhotonBeamEffect.BeamTransform transform = beamTransform(this.smoothedStart, this.smoothedEnd, this.forwardAxis);
         root.updatePos(new Vector3f((float)this.smoothedStart.f_82479_, (float)this.smoothedStart.f_82480_, (float)this.smoothedStart.f_82481_));
         root.updateRotation(transform.rotation);
         this.updateBeamLength(root, transform.length);
         return true;
      } else {
         return false;
      }
   }

   private PhotonBeamEffect.BeamEndpoints cacheBeamTarget(float partialTicks) {
      Vec3 from = this.startProvider.get(partialTicks);
      Vec3 to = this.endProvider.get(partialTicks);
      if (from != null && to != null) {
         this.lastGoodStart = from;
         this.lastGoodEnd = to;
      }

      return this.lastGoodStart != null && this.lastGoodEnd != null ? new PhotonBeamEffect.BeamEndpoints(this.lastGoodStart, this.lastGoodEnd) : null;
   }

   private static Vec3 smooth(Vec3 current, Vec3 target) {
      return current != null && !(current.m_82557_(target) > 64.0) ? current.m_165921_(target, 0.55) : target;
   }

   private void updateBeamLength(IFXObject root, float length) {
      if (this.visualBaseLength > 0.0F) {
         float scale = Math.max(length / this.visualBaseLength, 1.0E-4F);
         root.updateScale(scaleVector(this.forwardAxis, scale));
         this.restoreNativeBeamEnds();
      } else {
         boolean usesNativeBeamEmitter = this.updateNativeBeamEnds(length);
         root.updateScale(usesNativeBeamEmitter ? new Vector3f(1.0F, 1.0F, 1.0F) : scaleVector(this.forwardAxis, length));
      }
   }

   private boolean updateNativeBeamEnds(float length) {
      if (this.runtime == null) {
         return false;
      } else {
         boolean updated = false;

         for (Object object : this.runtime.getAllSceneObjects()) {
            if (object instanceof BeamEmitter) {
               BeamEmitter beamEmitter = (BeamEmitter)object;
               BeamConfig config = beamEmitter.getConfig();
               if (config != null) {
                  this.rememberOriginalBeamEnd(beamEmitter, config);
                  setAxisLength(config.getEnd(), this.forwardAxis, length);
                  updated = true;
               }
            }
         }

         return updated;
      }
   }

   private boolean restoreNativeBeamEnds() {
      if (this.runtime == null) {
         return false;
      } else {
         boolean restored = false;

         for (Object object : this.runtime.getAllSceneObjects()) {
            if (object instanceof BeamEmitter) {
               BeamEmitter beamEmitter = (BeamEmitter)object;
               BeamConfig config = beamEmitter.getConfig();
               if (config != null) {
                  Vector3f originalEnd = this.rememberOriginalBeamEnd(beamEmitter, config);
                  config.getEnd().set(originalEnd);
                  restored = true;
               }
            }
         }

         return restored;
      }
   }

   private Vector3f rememberOriginalBeamEnd(BeamEmitter beamEmitter, BeamConfig config) {
      return this.originalBeamEnds.computeIfAbsent(beamEmitter, ignored -> new Vector3f(config.getEnd()));
   }

   private static void setAxisLength(Vector3f vector, PhotonClientFxUtil.BeamForwardAxis axis, float length) {
      if (axis == PhotonClientFxUtil.BeamForwardAxis.POSITIVE_X) {
         vector.set(length, 0.0F, 0.0F);
      } else {
         vector.set(0.0F, 0.0F, length);
      }
   }

   private static Vector3f scaleVector(PhotonClientFxUtil.BeamForwardAxis axis, float length) {
      return axis == PhotonClientFxUtil.BeamForwardAxis.POSITIVE_X ? new Vector3f(length, 1.0F, 1.0F) : new Vector3f(1.0F, 1.0F, length);
   }

   private void stop() {
      ACTIVE.remove(this.key);
      if (this.runtime != null) {
         this.runtime.destroy(this.forcedDeath);
         this.runtime = null;
      }

      this.removeFromEntityCache();
   }

   private void removeFromEntityCache() {
      List<EntityEffect> effects = (List<EntityEffect>)EntityEffect.CACHE.get(this.entity);
      if (effects != null) {
         effects.remove(this);
         if (effects.isEmpty()) {
            EntityEffect.CACHE.remove(this.entity);
         }
      }
   }

   private static void cleanupDead() {
      Iterator<Entry<String, PhotonBeamEffect>> iterator = ACTIVE.entrySet().iterator();

      while (iterator.hasNext()) {
         PhotonBeamEffect effect = iterator.next().getValue();
         if (effect.runtime == null || !effect.runtime.isAlive() || !effect.isSourceAlive()) {
            if (effect.runtime != null) {
               effect.runtime.destroy(effect.forcedDeath);
               effect.runtime = null;
            }

            effect.removeFromEntityCache();
            iterator.remove();
         }
      }
   }

   private static PhotonBeamEffect.BeamTransform beamTransform(Vec3 from, Vec3 to, PhotonClientFxUtil.BeamForwardAxis forwardAxis) {
      Vec3 delta = to.m_82546_(from);
      double length = Math.max(delta.m_82553_(), 1.0E-4);
      float toX = (float)(delta.f_82479_ / length);
      float toY = (float)(delta.f_82480_ / length);
      float toZ = (float)(delta.f_82481_ / length);
      Quaternionf rotation = forwardAxis == PhotonClientFxUtil.BeamForwardAxis.POSITIVE_X
         ? new Quaternionf().rotationTo(1.0F, 0.0F, 0.0F, toX, toY, toZ)
         : new Quaternionf().rotationTo(0.0F, 0.0F, 1.0F, toX, toY, toZ);
      return new PhotonBeamEffect.BeamTransform(rotation, (float)length);
   }

   private static record BeamEndpoints(Vec3 start, Vec3 end) {
   }

   private static record BeamTransform(Quaternionf rotation, float length) {
   }
}
