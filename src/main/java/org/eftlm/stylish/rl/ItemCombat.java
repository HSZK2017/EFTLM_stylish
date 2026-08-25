package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * P2.5 道具战斗（仿 AVNpc 战斗道具，报告 1.5.4）：受击方块格挡采用三级策略——
 * <ol>
 *     <li><b>Herobrine 式放墙</b>（对应 AV {@code placeRandomFrontWall}）：朝攻击方向
 *         放置短墙（黑曜石优先，其次背包任意方块），阻挡攻击者接近与弹道；
 *         冷却 80 tick；</li>
 *     <li><b>Steve 式速搭</b>（对应 AV {@code swapToBlock} 的受击挡伤害语义）：背包方块
 *         速放脚下挡下本次伤害，冷却 60 tick；</li>
 *     <li><b>垫高 pillar</b>：脚下叠 2 格方块并把女仆抬升（teleport 到柱顶），
 *         脱离地面攻击/横扫判定；冷却 120 tick。</li>
 * </ol>
 * P5.5 方块武器扩展（仿 AV "Him克隆" 的黑曜石方块武器，报告 1.5.5）：主手为 BlockItem 时
 * 该方块被"临时注册"为方块武器（{@link BlockWeaponRegistry} 通用模板 + 圆石/深板岩/黑曜石
 * 三个注册项）——三级策略优先使用<b>主手方块</b>且<b>不消耗</b>（方块是武器不是弹药），
 * 并新增主动放置战技 {@link #tryActiveBlockWall}（近身主动放墙，仿 Herobrine 暗影黑曜石）。
 * 其余道具逻辑：
 * <ul>
 *     <li><b>水桶灭火</b>（对应 AV {@code tryPerformAvNpcWaterBucketSelfExtinguish}）：
 *         着火消耗水桶放水灭火，冷却 220~400 tick；</li>
 *     <li><b>末影珍珠反击</b>（对应 AV {@code doSteveStyleEnderPearlCounter}）：
 *         受击消耗珍珠朝攻击者投掷（落点传送自身=绕背换位），冷却 100~300 tick。</li>
 * </ul>
 * 全部为规则层（不进 RL 状态/训练数据），动作记录 {@code item_*} trace 事件；
 * 竞技场安全：所有临时放置的方块/水 3 tick 自动移除（含垫高柱），不污染地形。
 */
public final class ItemCombat {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    /** Steve 速搭冷却（tick，对应 AV placeBlockParryCooldown=60） */
    static final int BLOCK_PARRY_COOLDOWN = 60;
    /** Herobrine 放墙冷却（tick） */
    static final int WALL_COOLDOWN = 80;
    /** 垫高冷却（tick） */
    static final int PILLAR_COOLDOWN = 120;
    /** 临时方块/水/柱子存在时长（tick，到点自动移除防地形污染） */
    static final int TEMP_BLOCK_TICKS = 3;
    /** 水桶灭火冷却下限/区间（对应 AV 220 + rand(181)） */
    static final int WATER_COOLDOWN_MIN = 220;
    static final int WATER_COOLDOWN_RAND = 181;
    /** 珍珠反击冷却区间（对应 AV 100 + rand(201)） */
    static final int PEARL_COOLDOWN_MIN = 100;
    static final int PEARL_COOLDOWN_RAND = 201;
    /** 珍珠反击触发概率 */
    static final float PEARL_CHANCE = 0.3F;
    /** 水桶检查节流（tick） */
    static final int WATER_CHECK_INTERVAL = 10;
    /** 垫高柱高度（格） */
    static final int PILLAR_HEIGHT = 2;
    /** P5.5 方块武器主动放墙的触发距离（格） */
    static final double ACTIVE_WALL_RANGE = 3.0;

    /** 每女仆道具战斗状态 */
    record ItemState(int blockParryUntil, int wallUntil, int pillarUntil,
                     int waterUntil, int pearlUntil,
                     List<BlockPos> tempBlocks, int tempUntil, int lastWaterCheck) {
        ItemState withTemp(List<BlockPos> blocks, int until) {
            List<BlockPos> merged = new ArrayList<>(this.tempBlocks);
            merged.addAll(blocks);
            return new ItemState(blockParryUntil, wallUntil, pillarUntil, waterUntil, pearlUntil,
                    merged, Math.max(this.tempUntil, until), lastWaterCheck);
        }

        ItemState withBlockParry(int until) {
            return new ItemState(until, wallUntil, pillarUntil, waterUntil, pearlUntil,
                    tempBlocks, tempUntil, lastWaterCheck);
        }

        ItemState withWall(int until) {
            return new ItemState(blockParryUntil, until, pillarUntil, waterUntil, pearlUntil,
                    tempBlocks, tempUntil, lastWaterCheck);
        }

        ItemState withPillar(int until) {
            return new ItemState(blockParryUntil, wallUntil, until, waterUntil, pearlUntil,
                    tempBlocks, tempUntil, lastWaterCheck);
        }

        ItemState withWater(int until, int lastCheck) {
            return new ItemState(blockParryUntil, wallUntil, pillarUntil, until, pearlUntil,
                    tempBlocks, tempUntil, lastCheck);
        }

        ItemState withPearl(int until) {
            return new ItemState(blockParryUntil, wallUntil, pillarUntil, waterUntil, until,
                    tempBlocks, tempUntil, lastWaterCheck);
        }
    }

    private static final Map<UUID, ItemState> STATES = new HashMap<>();

    private ItemCombat() {
    }

    /** 每 tick 调用：临时方块/水清理 + 水桶灭火检查（节流）+ 危险区清理 */
    public static void tick(EntityMaid maid) {
        int tick = maid.tickCount;
        SpatialMap.prune(tick); // P5：危险区过期清理
        UUID id = maid.getUUID();
        ItemState st = STATES.computeIfAbsent(id, k -> empty());
        // 临时方块/水/垫高柱移除
        if (!st.tempBlocks().isEmpty() && tick >= st.tempUntil()) {
            for (BlockPos pos : st.tempBlocks()) {
                if (maid.level().getBlockState(pos).is(Blocks.WATER)
                        || !maid.level().getBlockState(pos).isAir()) {
                    maid.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
            STATES.put(id, new ItemState(st.blockParryUntil(), st.wallUntil(), st.pillarUntil(),
                    st.waterUntil(), st.pearlUntil(), new ArrayList<>(), 0, st.lastWaterCheck()));
        }
        // 水桶灭火（节流）
        if (tick - st.lastWaterCheck() >= WATER_CHECK_INTERVAL) {
            tryWaterExtinguish(maid, tick);
        }
    }

    /**
     * 受击方块格挡（三级策略）：伤害事件中调用，返回 true = 已采取方块应对并取消本次伤害。
     * 优先级：Herobrine 放墙 → Steve 速搭 → 垫高 pillar。
     * P5.5：主手方块武器时优先用主手方块（不消耗），冷却与墙尺寸取方块武器规格。
     */
    public static boolean tryPlaceBlockParry(MaidPatch<?> patch, EntityMaid maid, DamageSource source) {
        if (!RlConfig.itemBlockParry) {
            return false;
        }
        int tick = maid.tickCount;
        UUID id = maid.getUUID();
        ItemState st = STATES.computeIfAbsent(id, k -> empty());
        if (patch.getEntityState().knockDown() || patch.getEntityState().inaction()) {
            return false; // 击倒/动作中不放方块
        }
        if (!(source.getEntity() instanceof LivingEntity attacker) || !attacker.isAlive()) {
            return false; // 只挡实体攻击（环境伤害不触发）
        }
        if (maid.getRandom().nextDouble() > RlConfig.blockParryChance) {
            return false;
        }
        // P5.5：主手方块武器规格（null = 未持方块，走背包消耗逻辑）
        BlockWeaponRegistry.BlockWeaponSpec bw = BlockWeaponRegistry.specOf(maid.getMainHandItem());

        // ---- 一级：Herobrine 式放墙（朝攻击方向，方块武器主手优先/黑曜石优先） ----
        if (tick >= st.wallUntil()) {
            ItemStack block = bw != null ? maid.getMainHandItem() : findBlock(maid, true);
            if (!block.isEmpty()) {
                int rows = bw != null ? bw.wallRows() : 2;
                int height = bw != null ? bw.wallHeight() : 2;
                List<BlockPos> placed = placeWall(maid, attacker, ((BlockItem) block.getItem()).getBlock().defaultBlockState(), rows, height);
                if (!placed.isEmpty()) {
                    if (bw == null) {
                        block.shrink(1); // 背包方块消耗；方块武器不消耗（仿克隆体）
                    }
                    // P5：登记墙为危险区（阻挡弹道/接近的障碍地形）
                    for (BlockPos p : placed) {
                        SpatialMap.registerHazard(p, 1, tick + TEMP_BLOCK_TICKS);
                    }
                    int wallCd = bw != null ? bw.wallCooldown() : WALL_COOLDOWN;
                    STATES.put(id, st.withTemp(placed, tick + TEMP_BLOCK_TICKS)
                            .withWall(tick + wallCd));
                    RlTrace.event(maid, "item_wall_parry",
                            "wall=" + placed.size() + " blocks, dir=" + directionTo(maid, attacker)
                                    + " attacker=" + attacker.getType().getDescriptionId()
                                    + (bw != null ? " block_weapon" : ""));
                    return true;
                }
            }
        }

        // ---- 二级：Steve 式速搭（脚下放方块挡伤害） ----
        if (tick >= st.blockParryUntil()) {
            ItemStack block = bw != null ? maid.getMainHandItem() : findBlock(maid, false);
            if (!block.isEmpty()) {
                BlockPos pos = maid.blockPosition();
                BlockState state = ((BlockItem) block.getItem()).getBlock().defaultBlockState();
                maid.level().setBlockAndUpdate(pos, state);
                if (bw == null) {
                    block.shrink(1);
                }
                // P5：登记危险区（格挡方块视为短暂危险地形，闪避方向规避）
                SpatialMap.registerHazard(pos, 1, tick + TEMP_BLOCK_TICKS);
                List<BlockPos> placed = new ArrayList<>();
                placed.add(pos);
                int parryCd = bw != null ? bw.blockParryCooldown() : BLOCK_PARRY_COOLDOWN;
                STATES.put(id, st.withTemp(placed, tick + TEMP_BLOCK_TICKS)
                        .withBlockParry(tick + parryCd));
                RlTrace.event(maid, "item_block_parry",
                        "block=" + state.getBlock().getDescriptionId() + " attacker="
                                + attacker.getType().getDescriptionId()
                                + (bw != null ? " block_weapon" : ""));
                return true;
            }
        }

        // ---- 三级：垫高 pillar（脚下叠柱抬升，脱离地面攻击判定） ----
        if (tick >= st.pillarUntil()) {
            ItemStack block = bw != null ? maid.getMainHandItem() : findBlock(maid, false);
            if (!block.isEmpty() && (bw != null || block.getCount() >= PILLAR_HEIGHT)) {
                List<BlockPos> placed = placePillar(maid, ((BlockItem) block.getItem()).getBlock().defaultBlockState());
                if (!placed.isEmpty()) {
                    if (bw == null) {
                        block.shrink(PILLAR_HEIGHT);
                    }
                    int pillarCd = bw != null ? bw.pillarCooldown() : PILLAR_COOLDOWN;
                    STATES.put(id, st.withTemp(placed, tick + TEMP_BLOCK_TICKS + 4)
                            .withPillar(tick + pillarCd));
                    RlTrace.event(maid, "item_pillar_parry",
                            "pillar=" + PILLAR_HEIGHT + " blocks, attacker="
                                    + attacker.getType().getDescriptionId()
                                    + (bw != null ? " block_weapon" : ""));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * P5.5 方块武器主动放置战技（仿 Herobrine 暗影黑曜石墙）：主手方块武器且目标近身时
     * 主动朝目标放墙（不消耗主手方块）。由 StrategicLayer 近身规则调用；
     * 与受击墙共用冷却（都是"放墙"动作），临时方块照常 3 tick 自动移除。
     */
    public static boolean tryActiveBlockWall(EntityMaid maid, LivingEntity target) {
        if (!RlConfig.itemBlockWeapon || !RlConfig.itemBlockParry) {
            return false;
        }
        BlockWeaponRegistry.BlockWeaponSpec bw = BlockWeaponRegistry.specOf(maid.getMainHandItem());
        if (bw == null) {
            return false;
        }
        int tick = maid.tickCount;
        UUID id = maid.getUUID();
        ItemState st = STATES.computeIfAbsent(id, k -> empty());
        if (tick < st.wallUntil()) {
            return false;
        }
        if (maid.distanceTo(target) > ACTIVE_WALL_RANGE) {
            return false;
        }
        ItemStack block = maid.getMainHandItem();
        List<BlockPos> placed = placeWall(maid, target,
                ((BlockItem) block.getItem()).getBlock().defaultBlockState(),
                bw.wallRows(), bw.wallHeight());
        if (placed.isEmpty()) {
            return false;
        }
        // 方块武器不消耗；登记危险区 + 临时清理 + 冷却（与受击墙共用 wallUntil）
        for (BlockPos p : placed) {
            SpatialMap.registerHazard(p, 1, tick + TEMP_BLOCK_TICKS);
        }
        STATES.put(id, st.withTemp(placed, tick + TEMP_BLOCK_TICKS)
                .withWall(tick + bw.wallCooldown()));
        RlTrace.event(maid, "item_block_weapon_wall",
                "active wall=" + placed.size() + " blocks toward "
                        + target.getType().getDescriptionId() + " at "
                        + String.format("%.1f", maid.distanceTo(target)) + " blocks");
        return true;
    }

    /** 受击末影珍珠反击：不取消伤害，受击后朝攻击者投掷珍珠（落点传送自身=绕背换位） */
    public static void tryEnderPearlCounter(EntityMaid maid, DamageSource source) {
        if (!RlConfig.itemPearlCounter) {
            return;
        }
        int tick = maid.tickCount;
        UUID id = maid.getUUID();
        ItemState st = STATES.computeIfAbsent(id, k -> empty());
        if (tick < st.pearlUntil()) {
            return;
        }
        if (!(source.getEntity() instanceof LivingEntity attacker) || !attacker.isAlive()) {
            return;
        }
        if (maid.getRandom().nextFloat() > PEARL_CHANCE) {
            return;
        }
        ItemStack pearl = findPearl(maid);
        if (pearl.isEmpty()) {
            return;
        }
        pearl.shrink(1);
        STATES.put(id, st.withPearl(tick + PEARL_COOLDOWN_MIN + maid.getRandom().nextInt(PEARL_COOLDOWN_RAND)));
        throwPearlAt(maid, attacker);
        RlTrace.event(maid, "item_pearl_counter", "target=" + attacker.getType().getDescriptionId());
    }

    /** 着火时消耗水桶放水灭火（对应 AV tryPerformAvNpcWaterBucketSelfExtinguish 的简化版） */
    private static void tryWaterExtinguish(EntityMaid maid, int tick) {
        UUID id = maid.getUUID();
        ItemState st = STATES.get(id);
        if (st == null) {
            return;
        }
        if (tick < st.waterUntil()) {
            return;
        }
        if (!maid.isOnFire() || maid.isInWater() || !maid.onGround()) {
            return;
        }
        ItemStack bucket = findWaterBucket(maid);
        if (bucket.isEmpty()) {
            return;
        }
        bucket.shrink(1);
        BlockPos pos = maid.blockPosition();
        maid.level().setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
        maid.clearFire();
        List<BlockPos> placed = new ArrayList<>();
        placed.add(pos);
        STATES.put(id, st.withWater(tick + WATER_COOLDOWN_MIN + maid.getRandom().nextInt(WATER_COOLDOWN_RAND), tick)
                .withTemp(placed, tick + TEMP_BLOCK_TICKS));
        RlTrace.event(maid, "item_water_extinguish", "self extinguish with water bucket");
    }

    /** 女仆移除时清理道具战斗状态 */
    public static void forget(UUID id) {
        STATES.remove(id);
    }

    // ------------------------------------------------------------------
    // 内部工具
    // ------------------------------------------------------------------

    private static ItemState empty() {
        return new ItemState(0, 0, 0, 0, 0, new ArrayList<>(), 0, -100);
    }

    /** Herobrine 式放墙：朝攻击方向 rows×height 短墙（自身与攻击者之间） */
    private static List<BlockPos> placeWall(EntityMaid maid, LivingEntity attacker, BlockState state, int rows, int height) {
        List<BlockPos> placed = new ArrayList<>();
        if (!maid.onGround()) {
            return placed;
        }
        Direction dir = directionTo(maid, attacker);
        for (int d = 1; d <= rows; d++) {
            BlockPos base = maid.blockPosition().relative(dir, d);
            for (int h = 0; h < height; h++) {
                BlockPos pos = base.above(h);
                if (maid.level().getBlockState(pos).isAir() || maid.level().getBlockState(pos).canBeReplaced()) {
                    maid.level().setBlockAndUpdate(pos, state);
                    placed.add(pos);
                }
            }
        }
        return placed;
    }

    /** 垫高 pillar：脚下叠 PILLAR_HEIGHT 格并把女仆抬升到柱顶（脱离地面攻击判定） */
    private static List<BlockPos> placePillar(EntityMaid maid, BlockState state) {
        List<BlockPos> placed = new ArrayList<>();
        if (!maid.onGround()) {
            return placed;
        }
        BlockPos foot = maid.blockPosition();
        for (int h = 0; h < PILLAR_HEIGHT; h++) {
            BlockPos pos = foot.above(h);
            if (!maid.level().getBlockState(pos).isAir() && !maid.level().getBlockState(pos).canBeReplaced()) {
                return new ArrayList<>(); // 头顶空间不足，放弃垫高
            }
        }
        for (int h = 0; h < PILLAR_HEIGHT; h++) {
            BlockPos pos = foot.above(h);
            maid.level().setBlockAndUpdate(pos, state);
            placed.add(pos);
        }
        // 抬升女仆到柱顶（方块放置后实体被碰撞推挤不可靠，直接传送）
        if (maid.isAlive()) {
            maid.teleportTo(maid.getX(), foot.getY() + PILLAR_HEIGHT, maid.getZ());
        }
        return placed;
    }

    /** 攻击方向（水平） */
    private static Direction directionTo(EntityMaid maid, LivingEntity attacker) {
        return Direction.getNearest(attacker.getX() - maid.getX(), 0, attacker.getZ() - maid.getZ());
    }

    /** 找背包方块；preferObsidian 时优先黑曜石 */
    private static ItemStack findBlock(EntityMaid maid, boolean preferObsidian) {
        ItemStack obsidian = ItemStack.EMPTY;
        var inv = maid.getAvailableInv(true);
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
                continue;
            }
            if (preferObsidian && stack.is(Items.OBSIDIAN)) {
                return stack;
            }
            if (obsidian.isEmpty()) {
                obsidian = stack;
            }
        }
        return obsidian;
    }

    private static ItemStack findWaterBucket(EntityMaid maid) {
        var inv = maid.getAvailableInv(true);
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.is(Items.WATER_BUCKET)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack findPearl(EntityMaid maid) {
        var inv = maid.getAvailableBackpackInv();
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.is(Items.ENDER_PEARL)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /** 延时投掷珍珠（仿 AV CombatBehaviour.throwEnderPearlAt；经服务器执行队列调度） */
    private static void throwPearlAt(EntityMaid maid, LivingEntity target) {
        if (!(maid.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }
        final Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
        serverLevel.getServer().execute(() -> {
            if (!maid.isAlive() || maid.isRemoved()) {
                return;
            }
            Vec3 handPos = maid.position().add(0, 1.2, 0);
            Vec3 delta = targetPos.subtract(handPos);
            double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
            ThrownEnderpearl projectile = new ThrownEnderpearl(
                    net.minecraft.world.entity.EntityType.ENDER_PEARL, maid.level());
            projectile.setOwner(maid);
            projectile.moveTo(handPos);
            projectile.shoot(delta.x, delta.y + horizontal * 0.08, delta.z, 1.8F, 0.0F);
            serverLevel.addFreshEntity(projectile);
            maid.level().playSound(null, maid.getX(), maid.getY(), maid.getZ(),
                    SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F,
                    0.4F / (maid.getRandom().nextFloat() * 0.4F + 0.8F));
        });
    }
}
