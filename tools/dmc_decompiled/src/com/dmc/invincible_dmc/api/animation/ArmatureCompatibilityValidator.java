package com.dmc.invincible_dmc.api.animation;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.model.armature.types.ToolHolderArmature;

public final class ArmatureCompatibilityValidator {
   private ArmatureCompatibilityValidator() {
   }

   public static void validate(Armature current, Armature target) {
      if (current.getJointNumber() != target.getJointNumber()) {
         throw new IllegalArgumentException("Joint count mismatch: " + current.getJointNumber() + " != " + target.getJointNumber());
      } else if (current instanceof ToolHolderArmature != (target instanceof ToolHolderArmature)) {
         throw new IllegalArgumentException("Tool-holder armature capability mismatch");
      } else {
         validateHierarchy(current.rootJoint, target.rootJoint);

         for (int id = 0; id < current.getJointNumber(); id++) {
            Joint currentJoint = current.searchJointById(id);
            Joint targetJoint = target.searchJointById(id);
            if (currentJoint == null || targetJoint == null) {
               throw new IllegalArgumentException("Missing joint id " + id);
            }

            if (!currentJoint.getName().equals(targetJoint.getName())) {
               throw new IllegalArgumentException("Joint name mismatch at id " + id + ": " + currentJoint.getName() + " != " + targetJoint.getName());
            }
         }
      }
   }

   private static void validateHierarchy(Joint current, Joint target) {
      if (current.getId() != target.getId() || !current.getName().equals(target.getName())) {
         throw new IllegalArgumentException(
            "Joint hierarchy mismatch: " + current.getName() + "[" + current.getId() + "] != " + target.getName() + "[" + target.getId() + "]"
         );
      } else if (current.getSubJoints().size() != target.getSubJoints().size()) {
         throw new IllegalArgumentException("Child count mismatch for joint " + current.getName());
      } else {
         for (int index = 0; index < current.getSubJoints().size(); index++) {
            validateHierarchy((Joint)current.getSubJoints().get(index), (Joint)target.getSubJoints().get(index));
         }
      }
   }
}
