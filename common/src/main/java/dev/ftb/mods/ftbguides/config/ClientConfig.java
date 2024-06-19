package dev.ftb.mods.ftbguides.config;

import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftblibrary.snbt.config.BooleanValue;
import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import dev.ftb.mods.ftblibrary.snbt.config.StringValue;

import static dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil.LOCAL_DIR;
import static dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil.loadDefaulted;

public interface ClientConfig {
    SNBTConfig CONFIG = SNBTConfig.create(FTBGuides.MOD_ID + "-client");

    SNBTConfig GENERAL = CONFIG.getGroup("general");
    StringValue HOME = GENERAL.getString("home", "ftbguides:index")
            .comment("Default home page (navigate here with the Home button, pressing Alt+Home, or when opening an invalid page with /ftbguides open ...)");
    BooleanValue PINNED = GENERAL.getBoolean("pinned", true)
            .comment("Is the index panel pinned to stay open?");
    BooleanValue SEARCH_THIS_ONLY = GENERAL.getBoolean("search_this_guide_only", true)
            .comment("If true, search result will only include pages in the same guide namespace as the current page. If false, results will include pages from *all* known guide namespaces");

    static void init() {
        loadDefaulted(CONFIG, LOCAL_DIR, FTBGuides.MOD_ID, CONFIG.key + ".snbt");
    }

    private static void saveConfig() {
        CONFIG.save(LOCAL_DIR.resolve(CONFIG.key + ".snbt"));
    }

    static void toggleIndexPinned() {
        PINNED.set(!PINNED.get());
        saveConfig();
    }

    static boolean searchThisGuideOnly() {
        return SEARCH_THIS_ONLY.get();
    }

    static void toggleSearchThisOnly() {
        SEARCH_THIS_ONLY.set(!SEARCH_THIS_ONLY.get());
        saveConfig();
    }
}
