package org.eftlm.stylish.strategy;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.item.WeaponItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 武器库：扫描女仆背包中的可用武器（参考 tlm_smart_combat 的 EquipOptimizer 逻辑），
 * 提供近战武器轮换、远程武器切换与"枪神收尾"射击。
 */
public final class WeaponArsenal {

    public enum Kind {
        NONE, MELEE, BOW, CROSSBOW, GUN
    }

    /** 连段小循环结束后轮换近战武器的最小间隔（tick），避免频繁换装触发 AI 重建 */
    public static final int SWAP_COOLDOWN = 30;

    private WeaponArsenal() {
    }

    public static Kind classify(ItemStack stack) {
        if (stack.isEmpty()) {
            return Kind.NONE;
        }
        if (stack.getItem() instanceof CrossbowItem) {
            return Kind.CROSSBOW;
        }
        if (stack.getItem() instanceof BowItem) {
            return Kind.BOW;
        }
        if (isGun(stack)) {
            return Kind.GUN;
        }
        if (isMeleeWeapon(stack)) {
            return Kind.MELEE;
        }
        return Kind.NONE;
    }

    /**
     * 是否枪械类武器（如 EFTLM_WOM 适配的奇迹武器 Ender_Blaster / Nova）。
     * <p>
     * 识别方式：
     * <ul>
     *     <li>带 Epic Fight 能力、且武器类别不是 EpicFight 内置近战枚举
     *         （EnderBlaster 注册在自定义扩展类别 WOMWeaponCategories 下）</li>
     *     <li>内置类别但确为枪械的 WOM 武器：Nova 被注册为内置 TACHI 类别，
     *         需按物品特判（否则会被当作近战，收尾时误播弓箭动作）</li>
     * </ul>
     * 编译期不依赖 WOM，未安装 WOM 时自动不匹配。
     */
    public static boolean isGun(ItemStack stack) {
        if (stack.isEmpty() || !isEfWeapon(stack)) {
            return false;
        }
        if (stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem) {
            return false;
        }
        // 内置类别但确为枪械的 WOM 武器（Nova → TACHI）
        if (org.eftlm.stylish.compat.wom.WomSkillChecks.LoadedWOM()) {
            if (stack.getItem() == reascer.wom.world.item.WOMItems.NOVA.get()) {
                return true;
            }
        }
        if (!(stack.getItem() instanceof WeaponItem)) {
            return false;
        }
        CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(stack);
        WeaponCategory category = cap.getWeaponCategory();
        // 非内置类别 = 第三方注册的特殊武器（WOM 枪械等）
        return !(category instanceof CapabilityItem.WeaponCategories);
    }

    /**
     * 近战判定：主手位带攻击伤害属性修饰符（剑 / 斧 / 锄 / 重锤以及模组武器等）。
     */
    public static boolean isMeleeWeapon(ItemStack stack) {
        return stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }

    /**
     * 背包（含双手）中是否存在该弹射武器可用的弹药。
     */
    public static boolean hasAmmo(EntityMaid maid, ItemStack weaponStack) {
        if (!(weaponStack.getItem() instanceof ProjectileWeaponItem projectile)) {
            return false;
        }
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        return ItemsUtil.findStackSlot(inv, projectile.getAllSupportedProjectiles()) >= 0;
    }

    /**
     * 是否带 Epic Fight 武器能力。
     * <p>
     * FightModeTask 要求主手持 EF 能力武器，否则会清除攻击目标；
     * 因此武器轮换候选必须过滤为 EF 能力武器，避免切出战斗状态。
     */
    public static boolean isEfWeapon(ItemStack stack) {
        return EpicFightCapabilities.getItemCapability(stack).isPresent();
    }

    /**
     * 扫描全部近战武器（主手 + 背包），按估算面板伤害降序。
     * 用于"每完成一个连段小循环切换一次近战武器"的轮换候选。
     * 仅保留 Epic Fight 能力武器。
     */
    public static List<ItemStack> scanMelee(EntityMaid maid) {
        List<ItemStack> weapons = new ArrayList<>();
        ItemStack mainHand = maid.getMainHandItem();
        if (classify(mainHand) == Kind.MELEE && isEfWeapon(mainHand)) {
            weapons.add(mainHand);
        }
        CombinedInvWrapper backpack = maid.getAvailableBackpackInv();
        for (int i = 0; i < backpack.getSlots(); i++) {
            ItemStack stack = backpack.getStackInSlot(i);
            if (classify(stack) == Kind.MELEE && isEfWeapon(stack)) {
                weapons.add(stack);
            }
        }
        weapons.sort(Comparator.comparingDouble(WeaponArsenal::powerOf).reversed());
        return weapons;
    }

    /**
     * 近战武器面板力量估算：攻击伤害 + 攻速折算。
     */
    public static double powerOf(ItemStack stack) {
        double damage = 1.0;
        double speed = 1.6;
        for (var entry : stack.getAttributeModifiers(EquipmentSlot.MAINHAND).entries()) {
            if (entry.getKey() == Attributes.ATTACK_DAMAGE) {
                damage += entry.getValue().getAmount();
            }
            if (entry.getKey() == Attributes.ATTACK_SPEED) {
                speed += entry.getValue().getAmount();
            }
        }
        return damage * Mth.clamp(speed, 0.5, 4.0);
    }

    /**
     * 背包中是否存在可用的远程武器（弓 / 弩有弹药，或枪械），且带 EF 能力。
     */
    public static boolean hasUsableRanged(EntityMaid maid) {
        CombinedInvWrapper backpack = maid.getAvailableBackpackInv();
        for (int i = 0; i < backpack.getSlots(); i++) {
            ItemStack stack = backpack.getStackInSlot(i);
            Kind kind = classify(stack);
            if (kind == Kind.GUN) {
                return true;
            }
            if ((kind == Kind.BOW || kind == Kind.CROSSBOW) && isEfWeapon(stack) && hasAmmo(maid, stack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从背包中取出一件可用远程武器（弓 / 弩优先，其次枪械）；没有则返回空栈。
     */
    public static ItemStack findRanged(EntityMaid maid) {
        ItemStack gun = ItemStack.EMPTY;
        CombinedInvWrapper backpack = maid.getAvailableBackpackInv();
        for (int i = 0; i < backpack.getSlots(); i++) {
            ItemStack stack = backpack.getStackInSlot(i);
            Kind kind = classify(stack);
            if (kind == Kind.GUN && gun.isEmpty()) {
                gun = stack;
            }
            if ((kind == Kind.BOW || kind == Kind.CROSSBOW) && isEfWeapon(stack) && hasAmmo(maid, stack)) {
                return stack;
            }
        }
        return gun;
    }

    /**
     * 从背包取出指定槽位武器到手上，旧武器放回背包同一槽位。
     * 换装前终止正在使用的旧物品，避免拔刀剑连击 / 蓄力弓状态残留。
     */
    public static boolean swapBackpackToHand(EntityMaid maid, int slot, InteractionHand hand) {
        CombinedInvWrapper backpack = maid.getAvailableBackpackInv();
        if (slot < 0 || slot >= backpack.getSlots()) {
            return false;
        }
        ItemStack inSlot = backpack.getStackInSlot(slot);
        if (inSlot.isEmpty()) {
            return false;
        }
        ItemStack extracted = backpack.extractItem(slot, inSlot.getCount(), false);
        ItemStack current = maid.getItemInHand(hand);
        if (!current.isEmpty()) {
            // 换下：保存武器充能快照（NBT），下次拿出时恢复继续充能
            saveChargeSnapshot(maid, current);
            backpack.setStackInSlot(slot, current);
        }
        maid.stopUsingItem();
        maid.setItemInHand(hand, extracted);
        // 换上：恢复该武器的充能快照（仅补缺失键，不覆盖现有数据）
        restoreChargeSnapshot(maid, extracted);
        return true;
    }

    /**
     * 把主手武器切到指定槽位（不交换：主手放回背包找空位，候选武器上手）。
     * 用于"枪神收尾"：主手换远程，收尾后换回近战。
     */
    public static boolean forceHand(EntityMaid maid, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        CombinedInvWrapper backpack = maid.getAvailableBackpackInv();
        int sourceSlot = -1;
        for (int i = 0; i < backpack.getSlots(); i++) {
            // 值匹配而非引用比较：TLM 背包 NBT 存储, getStackInSlot 可能返回新实例,
            // `==` 恒 false 会走 else 分支直接上手 → 背包残留一份 → 武器复制
            if (ItemStack.isSameItemSameTags(backpack.getStackInSlot(i), stack)) {
                sourceSlot = i;
                break;
            }
        }
        ItemStack current = maid.getMainHandItem();
        if (!current.isEmpty()) {
            saveChargeSnapshot(maid, current);
            if (!putBack(maid, current)) {
                return false;
            }
        }
        if (sourceSlot >= 0) {
            ItemStack extracted = backpack.extractItem(sourceSlot, stack.getCount(), false);
            maid.setItemInHand(InteractionHand.MAIN_HAND, extracted);
            restoreChargeSnapshot(maid, extracted);
        } else {
            maid.setItemInHand(InteractionHand.MAIN_HAND, stack);
            restoreChargeSnapshot(maid, stack);
        }
        return true;
    }

    // ==================================================================
    // 武器充能快照（NBT）：切换武器时保留充能，下次拿出继续充能
    // ==================================================================

    private static final String CHARGE_SNAP_TAG = "eftlm_stylish:charge_snap";

    /**
     * 快照黑名单（原版敏感键）：这些键按 ItemStack 实例变化（耐久 / 附魔 / 展示等），
     * 按物品注册名做快照键时会把 A 实例的耐久/附魔注入同名 B 实例（跨实例污染）。
     * 快照只保留充能类自定义键（如 Charged/ChargedProjectiles 及模组自定义键）。
     */
    private static final java.util.Set<String> SNAP_DENY_KEYS = java.util.Set.of(
            "Damage", "Unbreakable", "Enchantments", "StoredEnchantments", "RepairCost",
            "display", "HideFlags", "AttributeModifiers", "CustomPotionColor", "CustomPotionEffects",
            "Potion", "BlockEntityTag", "BlockStateTag", "EntityTag", "Items", "CustomModelData", "Trim");

    /**
     * 换下武器时：把物品 NBT 完整快照存入女仆 persistentData（按物品注册名）。
     */
    private static void saveChargeSnapshot(EntityMaid maid, ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return;
        }
        CompoundTag snapshot = stack.getTag().copy();
        for (String deny : SNAP_DENY_KEYS) {
            snapshot.remove(deny);
        }
        if (snapshot.isEmpty()) {
            return; // 无充能类数据，不写空快照
        }
        CompoundTag data = maid.getPersistentData();
        CompoundTag snapshots = data.getCompound(CHARGE_SNAP_TAG);
        snapshots.put(getItemKey(stack), snapshot);
        data.put(CHARGE_SNAP_TAG, snapshots);
    }

    /**
     * 换上武器时：把该武器之前的充能快照合并回物品 NBT。
     * 快照已排除原版敏感键（耐久/附魔等），仅补"当前缺失"的充能类键，
     * 不覆盖耐久 / 附魔等现有数据。
     */
    private static void restoreChargeSnapshot(EntityMaid maid, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        CompoundTag data = maid.getPersistentData();
        CompoundTag snapshots = data.getCompound(CHARGE_SNAP_TAG);
        String key = getItemKey(stack);
        if (!snapshots.contains(key)) {
            return;
        }
        CompoundTag snapshot = snapshots.getCompound(key);
        CompoundTag tag = stack.getOrCreateTag();
        boolean changed = false;
        for (String k : snapshot.getAllKeys()) {
            if (!tag.contains(k)) {
                tag.put(k, snapshot.get(k).copy());
                changed = true;
            }
        }
        // 快照已消费，移除（下次换下会重新保存最新状态）
        snapshots.remove(key);
        data.put(CHARGE_SNAP_TAG, snapshots);
    }

    private static String getItemKey(ItemStack stack) {
        return net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
    }

    private static boolean putBack(EntityMaid maid, ItemStack stack) {
        CombinedInvWrapper backpack = maid.getAvailableBackpackInv();
        for (int i = 0; i < backpack.getSlots(); i++) {
            if (backpack.getStackInSlot(i).isEmpty()) {
                backpack.setStackInSlot(i, stack);
                return true;
            }
        }
        return false;
    }

    /**
     * 枪神收尾：用弓射击目标。
     * <p>
     * 复刻 TLM TaskBowAttack 的箭矢生成逻辑（女仆在 FightModeTask 下没有
     * IRangedAttackTask，因此不能走 performRangedAttack）。
     */
    public static void shootBow(EntityMaid maid, @Nullable LivingEntity target) {
        if (target == null) {
            return;
        }
        ItemStack mainHand = maid.getMainHandItem();
        if (!(mainHand.getItem() instanceof BowItem bowItem)) {
            return;
        }
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        int slot = ItemsUtil.findStackSlot(inv, bowItem.getAllSupportedProjectiles());
        if (slot < 0) {
            return;
        }
        ItemStack arrowStack = inv.getStackInSlot(slot);
        AbstractArrow arrow = ProjectileUtil.getMobArrow(maid, arrowStack, 1.0F);
        arrow = bowItem.customArrow(arrow);
        if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.INFINITY_ARROWS, mainHand) <= 0) {
            arrowStack.shrink(1);
            inv.setStackInSlot(slot, arrowStack);
            arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        }
        // 箭伤害与女仆基础攻击挂钩（同 TLM）
        var attackDamage = maid.getAttribute(Attributes.ATTACK_DAMAGE);
        double attackValue = attackDamage == null ? 2.0 : attackDamage.getBaseValue();
        arrow.setBaseDamage(arrow.getBaseDamage() * (float) (attackValue / 2.0F));

        double x = target.getX() - maid.getX();
        double y = target.getEyeY() - maid.getEyeY();
        double z = target.getZ() - maid.getZ();
        float distance = maid.distanceTo(target);
        float velocity = Mth.clamp(distance / 10.0F, 1.6F, 3.2F);
        float inaccuracy = 1.0F - Mth.clamp(distance / 100.0F, 0.0F, 0.9F);
        arrow.setNoGravity(true);
        arrow.shoot(x, y, z, velocity, inaccuracy);
        mainHand.hurtAndBreak(1, maid, (m) -> m.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        maid.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (maid.getRandom().nextFloat() * 0.4F + 0.8F));
        maid.level().addFreshEntity(arrow);
    }

    /**
     * 枪神收尾：弩射击（走原版 performCrossbowAttack，自动装填并发射）。
     */
    public static void shootCrossbow(EntityMaid maid) {
        if (maid.getMainHandItem().getItem() instanceof CrossbowItem) {
            maid.performCrossbowAttack(maid, 1.6F);
        }
    }
}
