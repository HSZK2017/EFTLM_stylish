package com.dmc.invincible_dmc.conditions;

import io.netty.util.internal.StringUtil;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.client.gui.datapack.widgets.ResizableEditBox;
import yesman.epicfight.data.conditions.Condition.EntityPatchCondition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PovTargetPovAngle extends EntityPatchCondition {
   protected double min;
   protected double max;

   public PovTargetPovAngle() {
   }

   public PovTargetPovAngle(double min, double max) {
      this.min = min;
      this.max = max;
   }

   public PovTargetPovAngle read(CompoundTag tag) {
      this.min = (Double)this.assertTag("min", "decimal", tag, NumericTag.class, CompoundTag::m_128459_);
      this.max = (Double)this.assertTag("max", "decimal", tag, NumericTag.class, CompoundTag::m_128459_);
      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128347_("min", this.min);
      tag.m_128347_("max", this.max);
      return tag;
   }

   public boolean predicate(LivingEntityPatch<?> entitypatch) {
      if (entitypatch.getTarget() == null) {
         return false;
      } else {
         double degree = Math.toDegrees(MathUtils.getAngleBetween(((LivingEntity)entitypatch.getOriginal()).m_20154_(), entitypatch.getTarget().m_20154_()));
         return this.min < degree && degree < this.max;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      ResizableEditBox minEditBox = new ResizableEditBox(screen.getMinecraft().f_91062_, 0, 0, 0, 0, Component.m_237113_("min"), null, null);
      ResizableEditBox maxEditBox = new ResizableEditBox(screen.getMinecraft().f_91062_, 0, 0, 0, 0, Component.m_237113_("max"), null, null);
      minEditBox.m_94153_(context -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
      maxEditBox.m_94153_(context -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
      Function<Object, Tag> doubleParser = value -> DoubleTag.m_128500_(Double.valueOf(value.toString()));
      Function<Tag, Object> doubleGetter = tag -> ParseUtil.valueOfOmittingType(ParseUtil.nullOrToString(tag, Tag::m_7916_));
      return List.of(ParameterEditor.of(doubleParser, doubleGetter, minEditBox), ParameterEditor.of(doubleParser, doubleGetter, maxEditBox));
   }
}
