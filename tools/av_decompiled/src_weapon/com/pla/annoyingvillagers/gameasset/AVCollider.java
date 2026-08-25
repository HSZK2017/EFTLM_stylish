package com.pla.annoyingvillagers.gameasset;

import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;

public class AVCollider {
   public static final Collider SHADOW_OBSIDIAN_PILLAR = new MultiOBBCollider(3, 0.2, 3.0, 0.2, 0.0, 0.0, 0.0);
   public static final Collider SANJI_SPIN = new OBBCollider(2.0, 1.5, 2.0, 0.0, 0.75, 0.0);
}
