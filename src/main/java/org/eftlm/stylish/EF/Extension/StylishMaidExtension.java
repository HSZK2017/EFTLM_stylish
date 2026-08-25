package org.eftlm.stylish.EF.Extension;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.api.entity.ai.IExtraMaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

import java.util.List;

/**
 * TLM 扩展注册点（@LittleMaidExtension 自动扫描）：
 * 通过 addExtraMaidBrain 为女仆 CORE AI 注册"战斗模式抑制慌乱"行为。
 */
@LittleMaidExtension
public class StylishMaidExtension implements ILittleMaid {
    @Override
    public void addExtraMaidBrain(ExtraMaidBrainManager manager) {
        manager.addExtraMaidBrain(new IExtraMaidBrain() {
            @Override
            public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getCoreBehaviors() {
                return List.of(Pair.of(0, new NoPanicInCombat()));
            }
        });
    }
}
