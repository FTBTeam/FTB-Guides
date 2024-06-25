package dev.ftb.mods.ftbguides.config;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.snbt.config.BooleanValue;
import dev.ftb.mods.ftblibrary.snbt.config.IntValue;
import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import dev.ftb.mods.ftblibrary.snbt.config.StringValue;

import static dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil.LOCAL_DIR;
import static dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil.loadDefaulted;

public interface ClientConfig {
    SNBTConfig CONFIG = SNBTConfig.create(FTBGuides.MOD_ID + "-client");

    SNBTConfig GENERAL = CONFIG.addGroup("general");
    StringValue HOME = GENERAL.addString("home", "ftbguides:index")
            .comment("Default home page (navigate here with the Home button, pressing Alt+Home, or when opening an invalid page with /ftbguides open ...)");
    BooleanValue PINNED = GENERAL.addBoolean("pinned", true)
            .comment("Is the index panel pinned to stay open?");
    BooleanValue SEARCH_THIS_ONLY = GENERAL.addBoolean("search_this_guide_only", true)
            .comment("If true, search result will only include pages in the same guide namespace as the current page. If false, results will include pages from *all* known guide namespaces");
    IntValue GUI_SCALE = GENERAL.addInt("gui_scale", 0, 0, 8)
            .comment("Custom GUI scaling while the guide book is open. A value of 0 means to use your default Minecraft GUI scaling.");

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

    static void setGuiScale(int newScale) {
        GUI_SCALE.set(newScale);
        saveConfig();
    }

    static ConfigGroup createConfigGroup() {
        ConfigGroup group = new ConfigGroup(FTBGuides.MOD_ID + ".client_settings", accepted -> {
            if (accepted) {
                CONFIG.save(Platform.getGameFolder().resolve("local/" + FTBGuides.MOD_ID + "-client.snbt"));
            }
        });
        CONFIG.createClientConfig(group);

        return group;
    }
}
