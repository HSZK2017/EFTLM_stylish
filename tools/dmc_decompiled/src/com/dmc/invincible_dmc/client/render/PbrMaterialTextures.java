package com.dmc.invincible_dmc.client.render;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record PbrMaterialTextures(
   @Nullable ResourceLocation normal,
   @Nullable ResourceLocation diffuse,
   @Nullable ResourceLocation ambientOcclusion,
   @Nullable ResourceLocation height,
   @Nullable ResourceLocation metallic,
   @Nullable ResourceLocation roughness,
   @Nullable ResourceLocation packedMer,
   @Nullable ResourceLocation shaderPackTexture,
   @Nullable ResourceLocation legacyTexture
) {
   @Nullable
   public static PbrMaterialTextures fromJson(JsonObject jsonObject, String prefix) {
      PbrMaterialTextures textures = new PbrMaterialTextures(
         readTexture(jsonObject, prefix + "_normal"),
         readTexture(jsonObject, prefix + "_diffuse"),
         readTexture(jsonObject, prefix + "_ao"),
         readTexture(jsonObject, prefix + "_height"),
         readTexture(jsonObject, prefix + "_metallic"),
         readTexture(jsonObject, prefix + "_roughness"),
         readTexture(jsonObject, prefix + "_mer"),
         readTexture(jsonObject, prefix + "_shader_texture"),
         readTexture(jsonObject, prefix + "_legacy_texture")
      );
      return textures.hasAnyTexture() ? textures : null;
   }

   public static PbrMaterialTextures fromTextureBase(String namespace, String textureBasePath) {
      return new PbrMaterialTextures(
         ResourceLocation.fromNamespaceAndPath(namespace, textureBasePath + "_n_v4.png"),
         ResourceLocation.fromNamespaceAndPath(namespace, textureBasePath + "_diffuse_v4.png"),
         ResourceLocation.fromNamespaceAndPath(namespace, textureBasePath + "_ao_v4.png"),
         ResourceLocation.fromNamespaceAndPath(namespace, textureBasePath + "_height_v4.png"),
         ResourceLocation.fromNamespaceAndPath(namespace, textureBasePath + "_metallic_v4.png"),
         ResourceLocation.fromNamespaceAndPath(namespace, textureBasePath + "_roughness_v4.png"),
         null,
         ResourceLocation.fromNamespaceAndPath(namespace, textureBasePath + "_pbr_v4.png"),
         ResourceLocation.fromNamespaceAndPath(namespace, textureBasePath + "_legacy.png")
      );
   }

   public static PbrMaterialTextures fromPackedMer(
      @Nullable ResourceLocation normal,
      ResourceLocation diffuse,
      @Nullable ResourceLocation ambientOcclusion,
      @Nullable ResourceLocation height,
      ResourceLocation packedMer,
      @Nullable ResourceLocation shaderPackTexture,
      @Nullable ResourceLocation legacyTexture
   ) {
      return new PbrMaterialTextures(normal, diffuse, ambientOcclusion, height, null, null, packedMer, shaderPackTexture, legacyTexture);
   }

   public boolean isComplete() {
      return this.diffuse != null
         && (this.normal != null || this.height != null)
         && (this.packedMer != null || this.metallic != null && this.roughness != null);
   }

   private boolean hasAnyTexture() {
      return this.normal != null
         || this.diffuse != null
         || this.ambientOcclusion != null
         || this.height != null
         || this.metallic != null
         || this.roughness != null
         || this.packedMer != null;
   }

   @Nullable
   private static ResourceLocation readTexture(JsonObject jsonObject, String key) {
      return jsonObject.has(key) ? ResourceLocation.parse(jsonObject.get(key).getAsString()) : null;
   }
}
