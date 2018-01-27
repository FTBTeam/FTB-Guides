package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * @author LatvianModder
 */
public class SpecialGuideButton
{
	public final ITextComponent title;
	public final Icon icon;
	public final String click;

	public SpecialGuideButton(ITextComponent t, Icon icn, String c)
	{
		title = t;
		icon = icn;
		click = c;
	}

	public SpecialGuideButton(JsonObject o)
	{
		title = o.has("title") ? JsonUtils.deserializeTextComponent(o.get("title")) : new TextComponentString("");
		icon = o.has("icon") ? Icon.getIcon(o.get("icon")) : Icon.EMPTY;
		click = o.has("click") ? o.get("click").getAsString() : "";
	}
}