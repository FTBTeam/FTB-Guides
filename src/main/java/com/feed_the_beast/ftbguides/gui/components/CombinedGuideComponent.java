package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CombinedGuideComponent extends GuideComponent implements Iterable<GuideComponent>
{
	private static class CombinedComponentWidget extends Panel implements IGuideComponentWidget
	{
		public CombinedComponentWidget(GuiBase gui)
		{
			super(gui);
		}

		@Override
		public void addWidgets()
		{
		}
	}

	private final List<GuideComponent> list = new ArrayList<>();

	public String toString()
	{
		return list.toString();
	}

	@Override
	public JsonElement toJson()
	{
		if (list.size() == 1)
		{
			return list.get(0).toJson();
		}

		JsonArray array = new JsonArray();

		for (GuideComponent component : list)
		{
			array.add(component.toJson());
		}

		return array;
	}

	@Override
	public boolean isEmpty()
	{
		for (GuideComponent c : list)
		{
			if (!c.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public GuideComponent copy()
	{
		CombinedGuideComponent c = new CombinedGuideComponent();

		for (GuideComponent c1 : list)
		{
			c.add(c1.copy());
		}

		return c.copyProperties(this);
	}

	@Override
	public IGuideComponentWidget createWidget(Panel parent)
	{
		return new CombinedComponentWidget(parent.gui);
	}

	@Override
	public Iterator<GuideComponent> iterator()
	{
		return list.iterator();
	}

	public void add(GuideComponent c)
	{
		list.add(c);
		c.parent = this;
	}
}