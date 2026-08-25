package org.eftlm.stylish.compat.efn;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.eftlm.stylish.EFTLMStylish;

import java.nio.file.Path;

/**
 * 技能目录维护命令（权限等级 2）：
 * <ul>
 *     <li>/efskills dump   —— 把运行时 AnimationManager 注册的全部 EFN / Enhance / WOM
 *         动画键 dump 到 config/eftlm_stylish/efn_anim_dump.json（发现缺失技能的侦察工具）</li>
 *     <li>/efskills reload —— 重载 config/eftlm_stylish/skills.json</li>
 *     <li>/efskills stats  —— 打印当前目录统计（武器 / 技能 / 动画覆盖）</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
public class EfnSkillCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("efskills")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("dump").executes(EfnSkillCommand::dump))
                        .then(Commands.literal("reload").executes(EfnSkillCommand::reload))
                        .then(Commands.literal("stats").executes(EfnSkillCommand::stats)));
    }

    private static int dump(CommandContext<CommandSourceStack> ctx) {
        Path out = FMLPaths.CONFIGDIR.get().resolve("eftlm_stylish").resolve("efn_anim_dump.json");
        EfnSkillCatalog.dump(out);
        ctx.getSource().sendSuccess(() -> Component.literal("[efskills] dumped to " + out), false);
        return 1;
    }

    private static int reload(CommandContext<CommandSourceStack> ctx) {
        EfnSkillCatalog.reload();
        EfnSkillCatalog catalog = EfnSkillCatalog.get();
        ctx.getSource().sendSuccess(() -> Component.literal(
                "[efskills] reloaded: " + catalog.entries().size() + " weapons"), false);
        return 1;
    }

    private static int stats(CommandContext<CommandSourceStack> ctx) {
        EfnSkillCatalog catalog = EfnSkillCatalog.get();
        StringBuilder sb = new StringBuilder("[efskills] weapons: ").append(catalog.entries().size());
        for (EfnSkillCatalog.WeaponEntry entry : catalog.entries().values()) {
            sb.append("\n  ").append(entry.dir()).append(": ").append(entry.skills().size()).append(" skills");
        }
        String msg = sb.toString();
        ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
        return 1;
    }
}
