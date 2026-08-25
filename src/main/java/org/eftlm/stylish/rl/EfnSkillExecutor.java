package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import org.eftlm.stylish.compat.efn.EfnSkillCatalog;
import org.eftlm.stylish.compat.efn.SkillSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * 武器技能槽位执行器：把 {@link EfnSkillCatalog} 的可用技能（目录驱动，
 * skills.json → 运行时动画校验 → 条件/冷却/资源门控）贡献到 RL 技能池槽位
 * （全局 11..10+N），执行 = 播放目录技能动画（EFN 动画自带伤害/位移事件，播放即结算）。
 */
public final class EfnSkillExecutor implements RlActionExecutor {

    public static final String ID = "efn_skill";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public List<RlActionSlot> available(MaidPatch<?> patch, int tick) {
        List<RlActionSlot> slots = new ArrayList<>();
        if (RlConfig.slotStable) {
            // P2 稳定槽位：当前武器全部技能按 rank+id 占位，可用性走 null 掩码
            // （槽位语义 = 技能身份，与冷却/条件无关——修复槽位漂移）
            for (SkillSpec spec : EfnSkillCatalog.stableSkills(patch)) {
                slots.add(EfnSkillCatalog.isAvailable(patch, spec)
                        ? RlActionSlot.skill(ID, slots.size(), spec) : null);
            }
        } else {
            // 旧动态布局（规则兜底/兼容模式）
            for (SkillSpec spec : EfnSkillCatalog.availableSkills(patch)) {
                slots.add(RlActionSlot.skill(ID, slots.size(), spec));
            }
        }
        return slots;
    }

    @Override
    public boolean canExecute(MaidPatch<?> patch, RlActionSlot slot) {
        // P2：Commitment 门控（技能非紧急动作：空闲/即将结束/受控动画可执行）
        if (!org.eftlm.stylish.rl.CommitmentCatalog.canExecuteNow(patch, false)) {
            return false; // 长动画活性帧中不打断
        }
        SkillSpec spec = slot.skill();
        if (spec == null) {
            return false;
        }
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        // 防御校验：技能必须仍属于当前主手武器（防换装/异步事件场景下跨武器播放不匹配技能）
        return EfnSkillCatalog.skillsOf(patch).contains(spec)
                && !EfnSkillCatalog.isCooling(maid, spec, maid.tickCount);
    }

    @Override
    public RlExecResult execute(MaidPatch<?> patch, RlActionSlot slot) {
        SkillSpec spec = slot.skill();
        if (spec == null) {
            return RlExecResult.REJECTED_INVALID;
        }
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        if (EfnSkillCatalog.release(patch, spec)) {
            EfnSkillCatalog.markUsed(maid, spec, maid.tickCount);
            // 技能执行观测日志（[EFN-SKILL] executed: {技能id}）
            org.apache.logging.log4j.LogManager.getLogger("eftlm_stylish")
                    .info("[EFN-SKILL] executed: {}", spec.id());
            return RlExecResult.EXECUTED;
        }
        return RlExecResult.FAILED;
    }
}
