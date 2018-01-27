package com.feed_the_beast.ftbguides.events;

import com.feed_the_beast.ftbguides.gui.components.EmptyGuideComponent;
import com.feed_the_beast.ftbguides.gui.components.GuideComponent;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * @author LatvianModder
 */
@Cancelable
public class CreateGuideComponentEvent extends FTBGuidesEvent
{
	private final JsonObject json;
	private GuideComponent result = null;

	public CreateGuideComponentEvent(JsonObject o)
	{
		json = o;
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