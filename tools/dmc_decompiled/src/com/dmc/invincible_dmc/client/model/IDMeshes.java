package com.dmc.invincible_dmc.client.model;

import com.dmc.invincible_dmc.client.render.EnchantedWeaponOutlineRenderer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.client.model.ClassicMesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;

public class IDMeshes implements PreparableReloadListener {
   public static final MeshAccessor<SkinnedMesh> YAMATO_SPHERE = MeshAccessor.create(
      "invincible_dmc", "particle/yamato_sphere", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new)
   );
   public static final MeshAccessor<ClassicMesh> YAMATO_LAST_SPHERE = new MeshAccessor(
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "particle/yamato_sphere"),
      jsonModelLoader -> jsonModelLoader.loadClassicMesh(ClassicMesh::new),
      false
   );
   public static final MeshAccessor<SkinnedMesh> YAMATO_FLOOR = MeshAccessor.create(
      "invincible_dmc", "particle/yamato_floor", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new)
   );

   @NotNull
   public CompletableFuture<Void> m_5540_(
      PreparationBarrier stage,
      @NotNull ResourceManager resourceManager,
      @NotNull ProfilerFiller preparationsProfiler,
      @NotNull ProfilerFiller reloadProfiler,
      @NotNull Executor backgroundExecutor,
      @NotNull Executor gameExecutor
   ) {
      return CompletableFuture.runAsync(() -> {
         EnchantedWeaponOutlineRenderer.clearCaches();
         Meshes.reload(resourceManager);
      }, gameExecutor).thenCompose(stage::m_6769_);
   }
}
