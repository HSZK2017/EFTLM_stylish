package org.eftlm.stylish;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.eftlm.stylish.EF.Event.Diagnose;
import org.slf4j.Logger;

@Mod(EFTLMStylish.MODID)
public class EFTLMStylish {
    public static final String MODID = "eftlm_stylish";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EFTLMStylish() {
        LOGGER.info("EFTLM Stylish Combat loaded!");
        Diagnose.printEnvironment();
    }
}
