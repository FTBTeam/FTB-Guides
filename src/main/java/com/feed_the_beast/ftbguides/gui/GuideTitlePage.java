package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.FTBGuidesFinals;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuideTitlePage extends GuidePage
{
	public static final GuideTitlePage SERVER_INFO = new GuideTitlePage("server_info", GuideType.SERVER_INFO);

	static
	{
		SERVER_INFO.title = new TextComponentTranslation(FTBGuidesFinals.MOD_ID + ".server_info");
		SERVER_INFO.icon = GuiIcons.BOOK_RED;
	}

	public final GuideType type;
	public final List<String> authors;
	public boolean isPresent;

	public GuideTitlePage(String id, GuideType t)
	{
		super(id, GuidePageRoot.INSTANCE);
		type = t;
		authors = new ArrayList<>();
		isPresent = false;
	}
}