package com.dmc.invincible_dmc.client.renderer;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.render.EnchantedWeaponOutlineRenderer;
import com.dmc.invincible_dmc.client.render.NormalMappedRenderTypes;
import com.dmc.invincible_dmc.client.render.PbrMaterialTextures;
import com.dmc.invincible_dmc.client.render.afterimage.AfterimageSnapshot;
import com.dmc.invincible_dmc.client.render.cinematic.CinematicYamatoBreakoutRenderer;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.item.DMCItems;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderYamato extends RenderItemBase {
   private static final String DS_MODID = "invincible_dmc";
   private static final ResourceLocation DS_TEX = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/item/weapon/devil_sword_vergil.png");
   private static final ResourceLocation DS_TEX_L = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/item/weapon/devil_sword_vergil_l.png");
   private static final MeshAccessor<SkinnedMesh> DS_MESH1 = MeshAccessor.create(
      "invincible_dmc", "weapon/devil_sword/devil_sword_vergil_in_sheath", loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
   );
   private static final MeshAccessor<SkinnedMesh> DS_MESH2 = MeshAccessor.create(
      "invincible_dmc", "weapon/devil_sword/devil_sword_vergil_all", loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
   );
   private static final MeshAccessor<SkinnedMesh> DS_MESH3 = MeshAccessor.create(
      "invincible_dmc", "weapon/devil_sword/devil_sword_vergil_jc", loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
   );
   private final ResourceLocation mesh1Texture;
   private final ResourceLocation mesh1TextureL;
   private final PbrMaterialTextures mesh1Material;
   private final PbrMaterialTextures mesh1LightMaterial;
   private final AssetAccessor<? extends SkinnedMesh> mesh1_main;
   private final AssetAccessor<? extends SkinnedMesh> mesh1_off;
   private final ResourceLocation mesh2Texture;
   private final ResourceLocation mesh2TextureL;
   private final PbrMaterialTextures mesh2Material;
   private final PbrMaterialTextures mesh2LightMaterial;
   private final AssetAccessor<? extends SkinnedMesh> mesh2_main;
   private final AssetAccessor<? extends SkinnedMesh> mesh2_off;
   private final ResourceLocation mesh3Texture;
   private final ResourceLocation mesh3TextureL;
   private final PbrMaterialTextures mesh3Material;
   private final PbrMaterialTextures mesh3LightMaterial;
   private final AssetAccessor<? extends SkinnedMesh> mesh3_main;
   private final AssetAccessor<? extends SkinnedMesh> mesh3_off;

   public RenderYamato(JsonElement jsonElement) {
      super(jsonElement);
      JsonObject jsonObj = jsonElement.getAsJsonObject();
      if (jsonObj.has("mesh1_texture")) {
         this.mesh1Texture = ResourceLocation.parse(jsonObj.get("mesh1_texture").getAsString());
      } else {
         this.mesh1Texture = null;
      }

      if (jsonObj.has("mesh1_texture_l")) {
         this.mesh1TextureL = ResourceLocation.parse(jsonObj.get("mesh1_texture_l").getAsString());
      } else {
         this.mesh1TextureL = null;
      }

      this.mesh1Material = PbrMaterialTextures.fromJson(jsonObj, "mesh1");
      this.mesh1LightMaterial = PbrMaterialTextures.fromJson(jsonObj, "mesh1_l");
      if (jsonObj.has("mesh2_texture")) {
         this.mesh2Texture = ResourceLocation.parse(jsonObj.get("mesh2_texture").getAsString());
      } else {
         this.mesh2Texture = null;
      }

      if (jsonObj.has("mesh2_texture_l")) {
         this.mesh2TextureL = ResourceLocation.parse(jsonObj.get("mesh2_texture_l").getAsString());
      } else {
         this.mesh2TextureL = null;
      }

      this.mesh2Material = PbrMaterialTextures.fromJson(jsonObj, "mesh2");
      this.mesh2LightMaterial = PbrMaterialTextures.fromJson(jsonObj, "mesh2_l");
      if (jsonObj.has("mesh3_texture")) {
         this.mesh3Texture = ResourceLocation.parse(jsonObj.get("mesh3_texture").getAsString());
      } else {
         this.mesh3Texture = null;
      }

      if (jsonObj.has("mesh3_texture_l")) {
         this.mesh3TextureL = ResourceLocation.parse(jsonObj.get("mesh3_texture_l").getAsString());
      } else {
         this.mesh3TextureL = null;
      }

      this.mesh3Material = PbrMaterialTextures.fromJson(jsonObj, "mesh3");
      this.mesh3LightMaterial = PbrMaterialTextures.fromJson(jsonObj, "mesh3_l");
      if (jsonObj.has("mesh1_main")) {
         String meshLoc = jsonObj.get("mesh1_main").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh1_main = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh1_main = null;
      }

      if (jsonObj.has("mesh1_off")) {
         String meshLoc = jsonObj.get("mesh1_off").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh1_off = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh1_off = null;
      }

      if (jsonObj.has("mesh2_main")) {
         String meshLoc = jsonObj.get("mesh2_main").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh2_main = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh2_main = null;
      }

      if (jsonObj.has("mesh2_off")) {
         String meshLoc = jsonObj.get("mesh2_off").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh2_off = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh2_off = null;
      }

      if (jsonObj.has("mesh3_main")) {
         String meshLoc = jsonObj.get("mesh3_main").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh3_main = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh3_main = null;
      }

      if (jsonObj.has("mesh3_off")) {
         String meshLoc = jsonObj.get("mesh3_off").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh3_off = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh3_off = null;
      }
   }

   private static boolean isUseMesh2(DynamicAnimation dynamicAnimation, AnimationPlayer animPlayer) {
      DynamicAnimation realAnim = DMCAnimationUtils.getRealAnimation(dynamicAnimation);
      Optional<TimePairList> unsheathTime = realAnim.getProperty(YamatoAttackAnimation.UNSHEATH_TIME);
      if (unsheathTime.isEmpty()) {
         return false;
      } else {
         TimePairList tp = unsheathTime.get();
         if (dynamicAnimation.isLinkAnimation()) {
            boolean useInLink = realAnim.getProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK).orElse(false);
            if (!useInLink) {
               return false;
            }
         }

         return tp.isTimeInPairs(animPlayer.getElapsedTime());
      }
   }

   private static boolean isUseMesh3(DynamicAnimation dynamicAnimation, AnimationPlayer animPlayer) {
      if (dynamicAnimation.isLinkAnimation()) {
         return false;
      } else {
         DynamicAnimation realAnim = DMCAnimationUtils.getRealAnimation(dynamicAnimation);
         Optional<TimePairList> spModelTime = realAnim.getProperty(YamatoAttackAnimation.SP_MODEL_TIME);
         if (spModelTime.isPresent()) {
            return spModelTime.get().isTimeInPairs(animPlayer.getElapsedTime());
         } else {
            ResourceLocation realName = DMCAnimationUtils.getRealAnimationAccessor(dynamicAnimation).registryName();
            if (!realName.equals(YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND.registryName())
               && !realName.equals(YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND_FS.registryName())
               && !realName.equals(YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR.registryName())
               && !realName.equals(YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR_FS.registryName())) {
               return false;
            } else {
               Optional<TimePairList> unsheathTime = realAnim.getProperty(YamatoAttackAnimation.UNSHEATH_TIME);
               return unsheathTime.<Boolean>map(t -> t.isTimeInPairs(animPlayer.getElapsedTime())).orElse(true);
            }
         }
      }
   }

   public void renderItemInHand(
      ItemStack stack,
      LivingEntityPatch<?> livingEntityPatch,
      InteractionHand hand,
      OpenMatrix4f[] poses,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      if (livingEntityPatch != null) {
         if (livingEntityPatch.currentLivingMotion != LivingMotions.CLIMB && livingEntityPatch.currentLivingMotion != LivingMotions.SIT) {
            CinematicYamatoBreakoutRenderer.capture(this, stack, livingEntityPatch, hand, poses, poseStack, packedLight, partialTicks);
            if ((Boolean)DMConfig.SDT_WEAPON_RENDERER.get()
               && livingEntityPatch.getOriginal() instanceof Player player
               && SinDevilTriggerManager.isPlayerInSDT(player)) {
               Armature armature = livingEntityPatch.getArmature();
               poseStack.m_85836_();
               float weaponScale = SdtWeaponRenderer.getTargetScale(livingEntityPatch);
               poseStack.m_85841_(weaponScale, weaponScale, weaponScale);
               armature.setPose(livingEntityPatch.getAnimator().getPose(partialTicks));
               SdtWeaponRenderer.render(livingEntityPatch, hand, armature, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
               poseStack.m_85849_();
            } else {
               AnimationPlayer animPlayer = Objects.requireNonNull(livingEntityPatch.getClientAnimator().baseLayer.animationPlayer);
               DynamicAnimation animation = DMCAnimationUtils.getCurrentAnimation(animPlayer);
               boolean useMesh3 = isUseMesh3(animation, animPlayer);
               boolean useMesh2 = !useMesh3
                  && (
                     isUseMesh2(animation, animPlayer)
                        || DMCAnimationUtils.sameAccessor(DMCAnimationUtils.getCurrentAnimationAccessor(animPlayer), YamatoAnimations.TEST)
                  );
               boolean useDevilSword = false;
               float afterimageAlpha = 0.0F;
               AbstractClientPlayer dsPlayer = null;
               if ((Boolean)DMConfig.SDT_CHARGE_WEAPON_SWAP.get() && livingEntityPatch.getOriginal() instanceof AbstractClientPlayer clientPlayer) {
                  dsPlayer = clientPlayer;
                  afterimageAlpha = SdtWeaponAfterimageManager.getAfterimageAlpha(clientPlayer);
                  useDevilSword = SdtWeaponAfterimageManager.shouldUseDevilSword(clientPlayer);
               }

               Armature armature = livingEntityPatch.getArmature();
               poseStack.m_85836_();
               poseStack.m_85841_(1.0F, 1.0F, 1.0F);
               armature.setPose(livingEntityPatch.getAnimator().getPose(partialTicks));
               ResourceLocation texture;
               ResourceLocation textureL;
               PbrMaterialTextures material;
               PbrMaterialTextures lightMaterial;
               SkinnedMesh renderMesh;
               if (useDevilSword) {
                  if (useMesh3) {
                     renderMesh = (SkinnedMesh)DS_MESH3.get();
                  } else if (useMesh2) {
                     renderMesh = (SkinnedMesh)DS_MESH2.get();
                  } else {
                     renderMesh = (SkinnedMesh)DS_MESH1.get();
                  }

                  texture = DS_TEX;
                  textureL = DS_TEX_L;
                  material = null;
                  lightMaterial = null;
               } else if (useMesh3) {
                  renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh3_main.get() : (SkinnedMesh)this.mesh3_off.get();
                  texture = this.mesh3Texture;
                  textureL = this.mesh3TextureL;
                  material = this.mesh3Material;
                  lightMaterial = this.mesh3LightMaterial;
               } else if (useMesh2) {
                  renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh2_main.get() : (SkinnedMesh)this.mesh2_off.get();
                  texture = this.mesh2Texture;
                  textureL = this.mesh2TextureL;
                  material = this.mesh2Material;
                  lightMaterial = this.mesh2LightMaterial;
               } else {
                  renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh1_main.get() : (SkinnedMesh)this.mesh1_off.get();
                  texture = this.mesh1Texture;
                  textureL = this.mesh1TextureL;
                  material = this.mesh1Material;
                  lightMaterial = this.mesh1LightMaterial;
               }

               if (renderMesh != null) {
                  boolean cinematicReplay = CinematicYamatoBreakoutRenderer.isReplaying();
                  boolean materialEnabled = !stack.m_150930_((Item)DMCItems.YAMATO_DMC5_BD.get()) || (Boolean)DMConfig.YAMATO_DMC5_BD_PBR.get();
                  boolean pbrEnabled = !cinematicReplay && materialEnabled && NormalMappedRenderTypes.isPbrEnabled(material);
                  ResourceLocation baseTexture = !pbrEnabled && material != null && material.legacyTexture() != null ? material.legacyTexture() : texture;
                  if (texture != null) {
                     RenderType baseRenderType = cinematicReplay
                        ? RenderType.m_110458_(baseTexture)
                        : (!pbrEnabled ? RenderType.m_110473_(baseTexture) : NormalMappedRenderTypes.pbrEntity(texture, material));
                     if (!cinematicReplay && (Boolean)DMConfig.YAMATO_MODEL_OUTLINE.get()) {
                        EnchantedWeaponOutlineRenderer.renderBeforeBase(
                           stack, renderMesh, baseTexture, poseStack, buffer, packedLight, armature, armature.getPoseMatrices()
                        );
                     }

                     renderMesh.draw(
                        poseStack, buffer, baseRenderType, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.f_118083_, armature, armature.getPoseMatrices()
                     );
                  }

                  if (!cinematicReplay && textureL != null) {
                     if (pbrEnabled && NormalMappedRenderTypes.isPbrEnabled(lightMaterial)) {
                        renderMesh.draw(
                           poseStack,
                           buffer,
                           NormalMappedRenderTypes.pbrEntity(textureL, lightMaterial),
                           packedLight,
                           1.0F,
                           1.0F,
                           1.0F,
                           1.0F,
                           OverlayTexture.f_118083_,
                           armature,
                           armature.getPoseMatrices()
                        );
                     } else {
                        renderMesh.draw(
                           poseStack,
                           buffer,
                           RenderType.m_234338_(textureL),
                           packedLight,
                           1.0F,
                           1.0F,
                           1.0F,
                           0.9F,
                           OverlayTexture.f_118083_,
                           armature,
                           armature.getPoseMatrices()
                        );
                     }
                  }

                  if (!cinematicReplay && texture != null && (Boolean)DMConfig.YAMATO_MODEL_OUTLINE.get()) {
                     EnchantedWeaponOutlineRenderer.render(stack, renderMesh, baseTexture, poseStack, buffer, packedLight, armature, armature.getPoseMatrices());
                  }
               }

               if (afterimageAlpha > 0.0F && !useDevilSword && !CinematicYamatoBreakoutRenderer.isReplaying()) {
                  SkinnedMesh aiMesh;
                  if (useMesh3) {
                     aiMesh = (SkinnedMesh)DS_MESH3.get();
                  } else if (useMesh2) {
                     aiMesh = (SkinnedMesh)DS_MESH2.get();
                  } else {
                     aiMesh = (SkinnedMesh)DS_MESH1.get();
                  }

                  float aiScale = SdtWeaponAfterimageManager.getAfterimageScale(dsPlayer);
                  float aiOffsetX = SdtWeaponAfterimageManager.getAfterimageOffsetX(dsPlayer);
                  poseStack.m_85836_();
                  poseStack.m_252880_(aiOffsetX, 0.0F, 0.0F);
                  poseStack.m_85841_(aiScale, aiScale, aiScale);
                  aiMesh.draw(
                     poseStack,
                     buffer,
                     RenderType.m_110473_(DS_TEX),
                     packedLight,
                     0.3F,
                     0.5F,
                     1.0F,
                     afterimageAlpha,
                     OverlayTexture.f_118083_,
                     armature,
                     armature.getPoseMatrices()
                  );
                  aiMesh.draw(
                     poseStack,
                     buffer,
                     RenderType.m_234338_(DS_TEX_L),
                     packedLight,
                     0.3F,
                     0.5F,
                     1.0F,
                     afterimageAlpha * 0.8F,
                     OverlayTexture.f_118083_,
                     armature,
                     armature.getPoseMatrices()
                  );
                  poseStack.m_85849_();
               }

               poseStack.m_85849_();
            }
         }
      }
   }

   @Nullable
   public AfterimageSnapshot.AfterimageItemMesh captureAfterimageMesh(LivingEntityPatch<?> patch, InteractionHand hand) {
      AnimationPlayer animPlayer = patch.getClientAnimator().baseLayer.animationPlayer;
      if (animPlayer == null) {
         return null;
      } else {
         DynamicAnimation animation = DMCAnimationUtils.getCurrentAnimation(animPlayer);
         boolean useMesh3 = isUseMesh3(animation, animPlayer);
         boolean useMesh2 = !useMesh3
            && (
               isUseMesh2(animation, animPlayer)
                  || DMCAnimationUtils.sameAccessor(DMCAnimationUtils.getCurrentAnimationAccessor(animPlayer), YamatoAnimations.TEST)
            );
         SkinnedMesh mesh;
         ResourceLocation tex;
         ResourceLocation texL;
         if (useMesh3) {
            mesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh3_main.get() : (SkinnedMesh)this.mesh3_off.get();
            tex = this.mesh3Texture;
            texL = this.mesh3TextureL;
         } else if (useMesh2) {
            mesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh2_main.get() : (SkinnedMesh)this.mesh2_off.get();
            tex = this.mesh2Texture;
            texL = this.mesh2TextureL;
         } else {
            mesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh1_main.get() : (SkinnedMesh)this.mesh1_off.get();
            tex = this.mesh1Texture;
            texL = this.mesh1TextureL;
         }

         return mesh == null ? null : new AfterimageSnapshot.AfterimageItemMesh(mesh, tex, texL);
      }
   }
}
