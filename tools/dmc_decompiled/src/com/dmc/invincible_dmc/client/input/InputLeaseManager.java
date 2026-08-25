package com.dmc.invincible_dmc.client.input;

import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class InputLeaseManager {
   private static final Map<Integer, InputLeaseManager.InputLease> LEASES = new HashMap<>();
   private static final Map<Integer, Boolean> WAITING_FOR_RELEASE = new HashMap<>();

   private InputLeaseManager() {
   }

   public static boolean acquire(int keyIndex, InputLeaseManager.Owner owner, long token) {
      if (keyIndex >= 0 && token > 0L && !WAITING_FOR_RELEASE.getOrDefault(keyIndex, false)) {
         InputLeaseManager.InputLease existing = LEASES.get(keyIndex);
         if (existing != null && !existing.matches(owner, token)) {
            return false;
         } else {
            LEASES.put(keyIndex, new InputLeaseManager.InputLease(owner, token));
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean isOwnedBy(int keyIndex, InputLeaseManager.Owner owner, long token) {
      InputLeaseManager.InputLease lease = LEASES.get(keyIndex);
      return lease != null && lease.matches(owner, token);
   }

   public static boolean rebind(int keyIndex, InputLeaseManager.Owner owner, long previousToken, long nextToken) {
      if (keyIndex >= 0 && nextToken > 0L) {
         InputLeaseManager.InputLease lease = LEASES.get(keyIndex);
         if (lease != null && lease.matches(owner, previousToken)) {
            LEASES.put(keyIndex, new InputLeaseManager.InputLease(owner, nextToken));
            return true;
         } else {
            return lease != null && lease.matches(owner, nextToken);
         }
      } else {
         return false;
      }
   }

   public static boolean acquireOrReplaceOwnerToken(int keyIndex, InputLeaseManager.Owner owner, long token) {
      if (keyIndex >= 0 && token > 0L) {
         InputLeaseManager.InputLease existing = LEASES.get(keyIndex);
         if (existing == null) {
            if (WAITING_FOR_RELEASE.getOrDefault(keyIndex, false)) {
               return false;
            }
         } else if (existing.owner() != owner) {
            return false;
         }

         LEASES.put(keyIndex, new InputLeaseManager.InputLease(owner, token));
         return true;
      } else {
         return false;
      }
   }

   public static void release(int keyIndex, InputLeaseManager.Owner owner, long token, boolean requireFreshRelease, boolean physicallyHeld) {
      InputLeaseManager.InputLease lease = LEASES.get(keyIndex);
      if (lease != null && lease.matches(owner, token)) {
         LEASES.remove(keyIndex);
         if (requireFreshRelease && physicallyHeld) {
            WAITING_FOR_RELEASE.put(keyIndex, true);
         }
      }
   }

   public static void releaseOwner(int keyIndex, InputLeaseManager.Owner owner, boolean requireFreshRelease, boolean physicallyHeld) {
      InputLeaseManager.InputLease lease = LEASES.get(keyIndex);
      if (lease != null && lease.owner() == owner) {
         LEASES.remove(keyIndex);
         if (requireFreshRelease && physicallyHeld) {
            WAITING_FOR_RELEASE.put(keyIndex, true);
         }
      }
   }

   public static void observePhysicalState(int keyIndex, boolean physicallyHeld) {
      if (!physicallyHeld) {
         WAITING_FOR_RELEASE.remove(keyIndex);
      }
   }

   public static void clear() {
      LEASES.clear();
      WAITING_FOR_RELEASE.clear();
   }

   private static record InputLease(InputLeaseManager.Owner owner, long token) {
      private boolean matches(InputLeaseManager.Owner expectedOwner, long expectedToken) {
         return this.owner == expectedOwner && this.token == expectedToken;
      }
   }

   public static enum Owner {
      YAMATO_JUDGEMENT_CUT,
      CRAZY_COMBO;
   }
}
