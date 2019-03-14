package com.feed_the_beast.mods.ftbguides.events;

import com.feed_the_beast.mods.ftbguides.gui.GuidePage;
import com.feed_the_beast.mods.ftbguides.gui.components.EmptyGuideComponent;
import com.feed_the_beast.mods.ftbguides.gui.components.GuideComponent;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * @author LatvianModder
 */
@Cancelable
public class CreateGuideComponentEvent extends FTBGuidesEvent
{
	private final GuidePage page;
	private final JsonObject json;
	private GuideComponent result = null;

	public CreateGuideComponentEvent(GuidePage p, JsonObject o)
	{
		page = p;
		json = o;
	}

	public GuidePage getPage()
	{
		return page;
	}

	public JsonObject getJson()
	{
		return json;
	}

	public void setComponent(GuideComponent component)
	{
		result = component;
	}

	public GuideComponent getComponent()
	{
		return result == null || result.isEmpty() ? EmptyGuideComponent.INSTANCE : result;
	}
}