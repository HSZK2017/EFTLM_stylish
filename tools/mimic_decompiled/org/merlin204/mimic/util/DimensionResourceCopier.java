package org.merlin204.mimic.util;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import yesman.epicfight.api.exception.AssetLoadingException;

public class DimensionResourceCopier {
   private static final String MOD_ID = "mimic";
   private static final String DIMENSION_PATH = "the_lethean_sea";
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void copyDimensionToSaves(MinecraftServer server) {
      if (server == null) {
         LOGGER.error("Cannot copy dimension data without a server instance");
      } else {
         Path gameDir = FMLPaths.GAMEDIR.get();
         if (gameDir == null) {
            LOGGER.error("Cannot copy dimension data because the game directory is unavailable");
         } else {
            Path savesDir = gameDir.resolve("saves");
            if (server.m_6982_()) {
               copyToSave(gameDir.resolve(server.m_129910_().m_5462_()));
            } else {
               try (Stream<Path> saveFolders = Files.list(savesDir)) {
                  saveFolders.filter(x$0 -> Files.isDirectory(x$0)).forEach(DimensionResourceCopier::copyToSave);
               } catch (IOException var8) {
                  LOGGER.error("御剑凌虚： 遍历存档目录失败: {}", var8.getMessage());
               }
            }
         }
      }
   }

   private static void copyToSave(Path saveFolder) {
      Path targetDir = saveFolder.resolve("dimensions").resolve("mimic").resolve("the_lethean_sea");
      if (!Files.exists(targetDir.resolve("region"))) {
         try {
            Files.createDirectories(targetDir);
            copyUsingModClassLoader(targetDir);
            LOGGER.info("御剑凌虚： {} 复制完成。", targetDir);
         } catch (IOException var3) {
            LOGGER.error("御剑凌虚： 复制到存档失败: {} - {}", saveFolder.getFileName(), var3.getMessage());
         }
      }
   }

   private static void copyUsingModClassLoader(Path targetDir) {
      Class<?> modClass = ModList.get().getModObjectById("mimic").orElseThrow(() -> new AssetLoadingException("找不到模组")).getClass();
      String resourcePath = "/mimic/the_lethean_sea";

      try {
         copyResourceDirectory(modClass, resourcePath, targetDir);
      } catch (URISyntaxException | IOException var4) {
         LOGGER.error("御剑凌虚： 复制资源失败: {}", var4.getMessage());
      }
   }

   private static void copyResourceDirectory(Class<?> modClass, String resourcePath, Path targetDir) throws IOException, URISyntaxException {
      URL resourceUrl = modClass.getResource(resourcePath);
      if (resourceUrl == null) {
         throw new IOException("御剑凌虚： 资源目录不存在: " + resourcePath);
      } else {
         if ("jar".equals(resourceUrl.getProtocol())) {
            copyJarResources(resourceUrl, resourcePath, targetDir);
         } else {
            Path sourceDir = Paths.get(resourceUrl.toURI());
            Files.walk(sourceDir).forEach(source -> copyFile(source, sourceDir, targetDir));
         }
      }
   }

   private static void copyJarResources(URL resourceUrl, String resourcePath, Path targetDir) throws IOException {
      String jarPath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!"));
      jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

      try (FileSystem jarFs = FileSystems.newFileSystem(Path.of(jarPath), (ClassLoader)null)) {
         Path resourceRoot = jarFs.getPath(resourcePath);
         Files.walk(resourceRoot).forEach(source -> copyFile(source, resourceRoot, targetDir));
      }
   }

   private static void copyFile(Path source, Path sourceRoot, Path targetRoot) {
      try {
         Path relative = sourceRoot.relativize(source);
         Path target = targetRoot.resolve(relative.toString());
         if (Files.isDirectory(source)) {
            Files.createDirectories(target);
         } else {
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("御剑凌虚： 从 {} 复制到 {} ", source, target);
         }
      } catch (IOException var5) {
         LOGGER.error("御剑凌虚： 复制文件失败: {} - {}", source, var5.getMessage());
      }
   }
}
