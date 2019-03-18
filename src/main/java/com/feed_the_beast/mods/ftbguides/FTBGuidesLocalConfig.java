package com.feed_the_beast.mods.ftbguides;

import net.minecraftforge.common.config.Config;

/**
 * @author LatvianModder
 */
@Config(modid = FTBGuides.MOD_ID, category = "", name = "../local/client/ftbguides")
@Config.LangKey(FTBGuides.MOD_ID + "_local")
public class FTBGuidesLocalConfig
{
	@Config.LangKey("stat.generalButton")
	public static final General general = new General();

	public static class General
	{
		@Config.Comment("Use unicode font for guides.")
		public boolean use_unicode_font = true;

		@Config.Comment({"Last guide version.", "Do not change, for internal use only."})
		public String last_guide_version = "";

		@Config.Comment("Current theme of guides.")
		public String theme = "dark";

		@Config.Comment("Width of the GUI based on your window width. Default to 50%.")
		@Config.RangeInt(min = 10, max = 99)
		public int width_percent = 50;
	}
}