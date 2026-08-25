package com.pla.annoyingvillagers.clazz;

public enum SauceType {
   BBQ_SAUCE("entity.annoyingvillagers.bbq_sauce"),
   SWEET_ONION_SAUCE("entity.annoyingvillagers.sweet_onion_sauce"),
   HONEY_MUSTARD_SAUCE("entity.annoyingvillagers.honey_mustard_sauce"),
   SOY_SAUCE("entity.annoyingvillagers.soy_sauce");

   private final String translationKey;

   private SauceType(String translationKey) {
      this.translationKey = translationKey;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public boolean isSupport() {
      return this == SWEET_ONION_SAUCE;
   }

   public boolean isShockSauce() {
      return this == HONEY_MUSTARD_SAUCE || this == SOY_SAUCE;
   }
}
