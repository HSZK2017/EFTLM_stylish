package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.client.model.ModelSnakeBlade;
import com.pla.annoyingvillagers.client.model.ModelSnakeBladeFragment;
import com.pla.annoyingvillagers.entity.PortalEntity;
import com.pla.annoyingvillagers.entity.SnakeBladeEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.item.DemoniacVoltageReaverItem;
import java.util.function.DoubleFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SnakeBladeRenderer extends EntityRenderer<SnakeBladeEntity> {
   private static final ResourceLocation SNAKE_BLADE_TEXTURE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/snake_blade.png");
   private static final ResourceLocation FRAGMENT_CHAIN_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "annoyingvillagers", "textures/entities/fragment_chain.png"
   );
   public static final int MAX_NECK_SEGMENTS = 128;
   private static final float FRAGMENT_LENGTH = 0.45F;
   private static final float HEAD_CLEAR = 0.35F;
   private final ModelSnakeBlade<SnakeBladeEntity> snakeBladeModel;
   private final ModelSnakeBladeFragment<SnakeBladeEntity> fragmentModel;

   public SnakeBladeRenderer(Context context) {
      super(context);
      ModelPart fragmentRoot = context.m_174023_(ModelSnakeBladeFragment.LAYER_LOCATION);
      this.fragmentModel = new ModelSnakeBladeFragment<>(fragmentRoot);
      ModelPart bladeRoot = context.m_174023_(ModelSnakeBlade.LAYER_LOCATION);
      this.snakeBladeModel = new ModelSnakeBlade<>(bladeRoot);
   }

   private static float tipClear(SnakeBladeEntity snakeBladeEntity) {
      return !snakeBladeEntity.hasBlade() && !snakeBladeEntity.isRetracting() ? 0.0F : 0.35F;
   }

   private static VertexConsumer getEntityConsumer(MultiBufferSource buffer, ResourceLocation texture, boolean enchanted) {
      RenderType renderType = RenderType.m_110458_(texture);
      return enchanted ? ItemRenderer.m_115211_(buffer, renderType, true, true) : buffer.m_6299_(renderType);
   }

   public boolean shouldRender(SnakeBladeEntity snakeBladeEntity, @NotNull Frustum frustum, double x, double y, double z) {
      Entity fromEntity = snakeBladeEntity.getRenderFromEntity();
      return fromEntity != null && frustum.m_113029_(snakeBladeEntity.m_20191_().m_82367_(fromEntity.m_20191_()))
         ? true
         : super.m_5523_(snakeBladeEntity, frustum, x, y, z);
   }

   public void render(
      @NotNull SnakeBladeEntity snakeBladeEntity,
      float entityYaw,
      float partialTicks,
      @NotNull PoseStack poseStack,
      @NotNull MultiBufferSource buffer,
      int packedLight
   ) {
      super.m_7392_(snakeBladeEntity, entityYaw, partialTicks, poseStack, buffer, packedLight);
      poseStack.m_85836_();

      try {
         Entity fromEntity = snakeBladeEntity.getRenderFromEntity();
         if (fromEntity != null) {
            double x = Mth.m_14139_((double)partialTicks, snakeBladeEntity.f_19854_, snakeBladeEntity.m_20185_());
            double y = Mth.m_14139_((double)partialTicks, snakeBladeEntity.f_19855_, snakeBladeEntity.m_20186_());
            double z = Mth.m_14139_((double)partialTicks, snakeBladeEntity.f_19856_, snakeBladeEntity.m_20189_());
            float progress = (snakeBladeEntity.prevProgress + (snakeBladeEntity.getProgress() - snakeBladeEntity.prevProgress) * partialTicks) / 5.0F;
            float tipOffset = snakeBladeEntity.isGuard() ? 1.8F : 2.2F;
            Vec3 swordPos = DemoniacVoltageReaverItem.getToolTipPos(fromEntity, partialTicks, tipOffset);
            Vec3 distVec = swordPos != null
               ? swordPos.m_82492_(x, y + 1.2F, z)
               : this.getPositionOfPriorMob(snakeBladeEntity, fromEntity, partialTicks).m_82492_(x, y, z);
            Vec3 to = distVec.m_82490_((double)(1.0F - progress));
            VertexConsumer fragmentConsumer = getEntityConsumer(buffer, FRAGMENT_CHAIN_TEXTURE, snakeBladeEntity.isEnchanted());
            int segmentCount = 0;
            Vec3 currentNeckButt = distVec;
            if (snakeBladeEntity.isGuard()) {
               double distanceLeft = distVec.m_82554_(to);

               for (double buildUpTo = Math.max(0.0, distanceLeft - (double)tipClear(snakeBladeEntity));
                  segmentCount < 128 && buildUpTo > 0.001;
                  segmentCount++
               ) {
                  double step = Math.min(buildUpTo, 0.45F);
                  Vec3 dir = to.m_82546_(currentNeckButt);
                  Vec3 next = dir.m_82541_().m_82490_(step).m_82549_(currentNeckButt);
                  int neckLight = this.getLightColor(snakeBladeEntity, next.m_82520_(x, y, z));
                  this.renderNeckCube(currentNeckButt, next, poseStack, fragmentConsumer, neckLight, 0.0F);
                  currentNeckButt = next;
                  buildUpTo -= step;
               }
            } else {
               double distanceLeft = distVec.m_82554_(to);
               double buildUpTo = Math.max(0.0, distanceLeft - (double)tipClear(snakeBladeEntity));
               if (!(distanceLeft > 1.0E-4)) {
                  for (double time = (double)snakeBladeEntity.f_19797_ + (double)partialTicks; segmentCount < 128 && buildUpTo > 0.001; segmentCount++) {
                     double step = Math.min(buildUpTo, 0.45F);
                     Vec3 dir = to.m_82546_(currentNeckButt);
                     Vec3 next = dir.m_82541_().m_82490_(step).m_82549_(currentNeckButt);
                     int neckLight = this.getLightColor(snakeBladeEntity, next.m_82520_(x, y, z));
                     float yawShake = (float)(3.0 * Math.sin(16.0 * time + 0.7 * (double)segmentCount));
                     this.renderNeckCube(currentNeckButt, next, poseStack, fragmentConsumer, neckLight, yawShake);
                     currentNeckButt = next;
                     buildUpTo -= step;
                  }
               } else {
                  Vec3 fromW = new Vec3(x, y, z).m_82549_(distVec);
                  Vec3 toW = new Vec3(x, y, z).m_82549_(to);
                  Vec3 fwd = toW.m_82546_(fromW).m_82541_();
                  Vec3 right = new Vec3(fwd.f_82481_, 0.0, -fwd.f_82479_);
                  if (right.m_82556_() < 1.0E-6) {
                     right = new Vec3(1.0, 0.0, 0.0);
                  }

                  right = right.m_82541_();
                  double ampSide = Mth.m_14008_(distanceLeft * 0.18, 0.25, 2.0);
                  double ampUp = Mth.m_14008_(distanceLeft * 0.1, 0.0, 1.0);
                  long seed = (long)snakeBladeEntity.m_19879_() << 32
                     ^ (long)snakeBladeEntity.getFromEntityID() << 16
                     ^ (long)snakeBladeEntity.getToEntityID()
                     ^ -7046029254386353131L;
                  RandomSource rand = RandomSource.m_216335_(seed);
                  double sideSign = rand.m_188499_() ? 1.0 : -1.0;
                  double time = (double)snakeBladeEntity.f_19797_ + (double)partialTicks;
                  double phase1 = rand.m_188500_() * Math.PI * 2.0;
                  double phase2 = rand.m_188500_() * Math.PI * 2.0;
                  double phaseYaw = rand.m_188500_() * Math.PI * 2.0;
                  double jitterSideBase = 0.05;
                  double jitterUpBase = 0.03;
                  Vec3 finalRight = right;
                  DoubleFunction<Vec3> wavePoint = sx -> {
                     double u = sx / distanceLeft;
                     double sin = Math.sin(Math.PI * u);
                     Vec3 base = fromW.m_82549_(fwd.m_82490_(sx)).m_82549_(finalRight.m_82490_(ampSide * sideSign * sin)).m_82520_(0.0, ampUp * sin, 0.0);
                     double w1 = 20.0;
                     double w2 = 17.0;
                     double headBias = Math.pow(sin, 0.8);
                     double jitterSide = jitterSideBase * (0.6 + 0.8 * headBias) * Math.sin(w1 * time + 28.0 * u + phase1);
                     double jitterUp = jitterUpBase * (0.5 + 0.7 * headBias) * Math.sin(w2 * time + 19.0 * u + phase2);
                     return base.m_82549_(finalRight.m_82490_(jitterSide)).m_82520_(0.0, jitterUp, 0.0);
                  };
                  double s = 0.0;

                  Vec3 prevW;
                  for (prevW = wavePoint.apply(s); segmentCount < 128 && buildUpTo > 0.001; segmentCount++) {
                     double step = Math.min(buildUpTo, 0.45F);
                     s += step;
                     Vec3 nextW = wavePoint.apply(Math.min(s, distanceLeft - 0.35F));
                     Vec3 prevLocal = prevW.m_82492_(x, y, z);
                     Vec3 nextLocal = nextW.m_82492_(x, y, z);
                     int neckLight = this.getLightColor(snakeBladeEntity, nextW);
                     float yawShake = (float)(4.0 * Math.sin(18.0 * time + 0.9 * (double)segmentCount + phaseYaw));
                     this.renderNeckCube(prevLocal, nextLocal, poseStack, fragmentConsumer, neckLight, yawShake);
                     prevW = nextW;
                     buildUpTo -= step;
                  }

                  currentNeckButt = prevW.m_82492_(x, y, z);
               }
            }

            VertexConsumer bladeConsumer = getEntityConsumer(buffer, SNAKE_BLADE_TEXTURE, snakeBladeEntity.isEnchanted());
            if (snakeBladeEntity.hasBlade() || snakeBladeEntity.isRetracting()) {
               poseStack.m_85836_();
               poseStack.m_85837_(to.f_82479_, to.f_82480_, to.f_82481_);
               Vec3 headDir = to.m_82546_(currentNeckButt);
               float rotY = (float)(Mth.m_14136_(headDir.f_82479_, headDir.f_82481_) * 180.0 / Math.PI);
               float rotX = (float)(-Mth.m_14136_(headDir.f_82480_, headDir.m_165924_()) * 180.0 / Math.PI);
               poseStack.m_252781_(Axis.f_252436_.m_252977_(rotY));
               poseStack.m_252781_(Axis.f_252529_.m_252977_(rotX));
               double time = (double)snakeBladeEntity.f_19797_ + (double)partialTicks;
               float headYawWobble = (float)(1.6 * Math.sin(22.0 * time + 0.5));
               float headPitchWobble = (float)Math.sin(27.0 * time + 1.2);
               poseStack.m_252781_(Axis.f_252436_.m_252977_(headYawWobble));
               poseStack.m_252781_(Axis.f_252529_.m_252977_(headPitchWobble));
               int headLight = this.getLightColor(snakeBladeEntity, to.m_82520_(x, y, z));
               this.snakeBladeModel.m_7695_(poseStack, bladeConsumer, headLight, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
               poseStack.m_85849_();
            }

            return;
         }
      } finally {
         poseStack.m_85849_();
      }
   }

   private void renderNeckCube(Vec3 from, Vec3 to, PoseStack poseStack, VertexConsumer buffer, int packedLightIn, float additionalYaw) {
      Vec3 dir = to.m_82546_(from);
      float yaw = (float)(Mth.m_14136_(dir.f_82479_, dir.f_82481_) * (180.0 / Math.PI));
      float pitch = (float)(-Mth.m_14136_(dir.f_82480_, dir.m_165924_()) * (180.0 / Math.PI));
      poseStack.m_85836_();
      poseStack.m_85837_(from.f_82479_, from.f_82480_, from.f_82481_);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yaw + additionalYaw));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(pitch));
      this.fragmentModel.m_7695_(poseStack, buffer, packedLightIn, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.m_85849_();
   }

   private Vec3 getPositionOfPriorMob(SnakeBladeEntity snakeBladeEntity, Entity fromEntity, float partialTicks) {
      if (fromEntity instanceof PortalEntity portalEntity) {
         return portalEntity.getSnakeBladeAnchor();
      } else {
         double x = Mth.m_14139_((double)partialTicks, fromEntity.f_19854_, fromEntity.m_20185_());
         double y = Mth.m_14139_((double)partialTicks, fromEntity.f_19855_, fromEntity.m_20186_());
         double z = Mth.m_14139_((double)partialTicks, fromEntity.f_19856_, fromEntity.m_20189_());
         float yOffset = 0.0F;
         if (fromEntity instanceof Player player && snakeBladeEntity.isCreator(fromEntity)) {
            float swing = player.m_21324_(partialTicks);
            float swingSin = Mth.m_14031_(Mth.m_14116_(swing) * (float) Math.PI);
            float bodyYaw = Mth.m_14179_(partialTicks, player.f_20884_, player.f_20883_) * (float) (Math.PI / 180.0);
            int armSign = player.m_5737_() == HumanoidArm.RIGHT ? 1 : -1;
            ItemStack mainHand = player.m_21205_();
            if (!mainHand.m_150930_((Item)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER.get())) {
               armSign = -armSign;
            }

            double sin = (double)Mth.m_14031_(bodyYaw);
            double cos = (double)Mth.m_14089_(bodyYaw);
            double armOffset = (double)armSign * 0.35;
            if (this.f_114476_.f_114360_.m_92176_().m_90612_() && player == Minecraft.m_91087_().f_91074_) {
               double fovScale = 960.0 / (double)((Integer)this.f_114476_.f_114360_.m_231837_().m_231551_()).intValue();
               Vec3 nearPlane = this.f_114476_.f_114358_.m_167684_().m_167695_((float)armSign * 0.6F, -1.0F);
               nearPlane = nearPlane.m_82490_(fovScale);
               nearPlane = nearPlane.m_82524_(swingSin * 0.25F);
               nearPlane = nearPlane.m_82496_(-swingSin * 0.35F);
               x = Mth.m_14139_((double)partialTicks, player.f_19854_, player.m_20185_()) + nearPlane.f_82479_;
               y = Mth.m_14139_((double)partialTicks, player.f_19855_, player.m_20186_()) + nearPlane.f_82480_;
               z = Mth.m_14139_((double)partialTicks, player.f_19856_, player.m_20189_()) + nearPlane.f_82481_;
               yOffset = player.m_20192_() * 0.5F;
            } else {
               x = Mth.m_14139_((double)partialTicks, player.f_19854_, player.m_20185_()) - cos * armOffset - sin * 0.2;
               y = player.f_19855_ + (double)player.m_20192_() + (player.m_20186_() - player.f_19855_) * (double)partialTicks - 1.0;
               z = Mth.m_14139_((double)partialTicks, player.f_19856_, player.m_20189_()) - sin * armOffset + cos * 0.2;
               yOffset = (player.m_6047_() ? -0.1875F : 0.0F) - player.m_20192_() * 0.4F;
            }
         }

         return new Vec3(x, y + (double)yOffset, z);
      }
   }

   private int getLightColor(Entity entity, Vec3 pos) {
      Vec3i blockPosInt = new Vec3i(Mth.m_14107_(pos.f_82479_), Mth.m_14107_(pos.f_82480_), Mth.m_14107_(pos.f_82481_));
      BlockPos blockPos = new BlockPos(blockPosInt);
      if (!entity.m_9236_().m_46805_(blockPos)) {
         return 0;
      } else {
         int packedBelow = LevelRenderer.m_109541_(entity.m_9236_(), blockPos);
         int packedAbove = LevelRenderer.m_109541_(entity.m_9236_(), blockPos.m_7494_());
         int block = Math.max(packedBelow & 0xFF, packedAbove & 0xFF);
         int sky = Math.max(packedBelow >> 16 & 0xFF, packedAbove >> 16 & 0xFF);
         return block | sky << 16;
      }
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull SnakeBladeEntity snakeBladeEntity) {
      return SNAKE_BLADE_TEXTURE;
   }
}
