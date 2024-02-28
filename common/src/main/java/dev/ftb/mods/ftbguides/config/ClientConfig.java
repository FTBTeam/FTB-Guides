package dev.ftb.mods.ftbguides.config;

import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import dev.ftb.mods.ftblibrary.snbt.config.StringValue;

import static dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil.LOCAL_DIR;
import static dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil.loadDefaulted;

public interface ClientConfig {
    SNBTConfig CONFIG = SNBTConfig.create(FTBGuides.MOD_ID + "-client");

    SNBTConfig GENERAL = CONFIG.getGroup("general");
    StringValue HOME = GENERAL.getString("home", "ftbguides:index")
            .comment("Default home page (navigate here with the Home button, pressing Alt+Home, or when opening an invalid page with /ftbguides open ...)");

    static void init() {
        loadDefaulted(CONFIG, LOCAL_DIR, FTBGuides.MOD_ID, CONFIG.key + ".snbt");
    }
}
