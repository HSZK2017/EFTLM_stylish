package com.dmc.invincible_dmc.entity.doppelganger;

public sealed interface ScriptInstruction permits ScriptInstruction.PlayNode, ScriptInstruction.LoopStart, ScriptInstruction.LoopEnd {
   public static record LoopEnd() implements ScriptInstruction {
   }

   public static record LoopStart(int count) implements ScriptInstruction {
   }

   public static record PlayNode(long nodeId, int cancelFrame) implements ScriptInstruction {
   }
}
