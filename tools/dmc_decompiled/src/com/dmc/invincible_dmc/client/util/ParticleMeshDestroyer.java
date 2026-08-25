package com.dmc.invincible_dmc.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;

@OnlyIn(Dist.CLIENT)
public class ParticleMeshDestroyer {
   private static final Map<String, Integer> MESH_USAGE_COUNT = new ConcurrentHashMap<>();

   public static void onParticleCreated(MeshAccessor<SkinnedMesh> accessor) {
      String meshKey = getMeshKey(accessor);
      int newCount = MESH_USAGE_COUNT.merge(meshKey, 1, Integer::sum);
   }

   public static void onParticleDestroyed(MeshAccessor<SkinnedMesh> accessor) {
      String meshKey = getMeshKey(accessor);
      Integer count = MESH_USAGE_COUNT.get(meshKey);
      if (count != null) {
         if (count <= 1) {
            MESH_USAGE_COUNT.remove(meshKey);
            destroyMesh(accessor, meshKey);
         } else {
            MESH_USAGE_COUNT.put(meshKey, count - 1);
         }
      }
   }

   private static void destroyMesh(MeshAccessor<SkinnedMesh> accessor, String meshKey) {
      try {
         Field meshesField = Meshes.class.getDeclaredField("MESHES");
         meshesField.setAccessible(true);
         Map<?, ?> meshesMap = (Map<?, ?>)meshesField.get(null);
         Object targetKey = null;
         SkinnedMesh targetMesh = null;

         for (Entry<?, ?> entry : meshesMap.entrySet()) {
            if (entry.getKey().toString().equals(meshKey)) {
               targetKey = entry.getKey();
               if (entry.getValue() instanceof SkinnedMesh) {
                  targetMesh = (SkinnedMesh)entry.getValue();
               }
               break;
            }
         }

         if (targetMesh != null) {
            releaseSkinnedMeshResources(targetMesh);
            meshesMap.remove(targetKey);
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   private static void releaseSkinnedMeshResources(SkinnedMesh mesh) {
      try {
         try {
            Method destroyMethod = SkinnedMesh.class.getDeclaredMethod("destroy");
            destroyMethod.setAccessible(true);
            destroyMethod.invoke(mesh);
            return;
         } catch (NoSuchMethodException var8) {
            try {
               Field computerShaderField = SkinnedMesh.class.getDeclaredField("computerShaderSetup");
               computerShaderField.setAccessible(true);
               Object computerShaderSetup = computerShaderField.get(mesh);
               if (computerShaderSetup != null) {
                  try {
                     Method destroyBuffers = computerShaderSetup.getClass().getMethod("destroyBuffers");
                     destroyBuffers.invoke(computerShaderSetup);
                  } catch (NoSuchMethodException var6) {
                     try {
                        Method close = computerShaderSetup.getClass().getMethod("close");
                        close.invoke(computerShaderSetup);
                     } catch (NoSuchMethodException var5) {
                     }
                  }

                  computerShaderField.set(mesh, null);
               }
            } catch (NoSuchFieldException var7) {
            }
         }
      } catch (Exception var9) {
      }
   }

   private static String getMeshKey(MeshAccessor<SkinnedMesh> accessor) {
      return accessor.registryName().toString();
   }

   public static void forceCleanupAll() {
      MESH_USAGE_COUNT.clear();

      try {
         Field meshesField = Meshes.class.getDeclaredField("MESHES");
         meshesField.setAccessible(true);
         Map<?, ?> meshesMap = (Map<?, ?>)meshesField.get(null);

         for (Entry<?, ?> entry : meshesMap.entrySet()) {
            String key = entry.getKey().toString();
            if ((key.contains("yamato_sphere") || key.contains("yamato_floor")) && entry.getValue() instanceof SkinnedMesh) {
               releaseSkinnedMeshResources((SkinnedMesh)entry.getValue());
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }
   }
}
