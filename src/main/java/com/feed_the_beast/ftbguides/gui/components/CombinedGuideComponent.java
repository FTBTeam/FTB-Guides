package com.feed_the_beast.ftbguides.gui.components;

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
	public static class CombinedComponentPanel extends ComponentPanel
	{
		public final CombinedGuideComponent component;

		public CombinedComponentPanel(ComponentPanel parent, CombinedGuideComponent c)
		{
			super(parent);
			component = c;
		}

		@Override
		public List<GuideComponent> getComponents()
		{
			return component.components;
		}

		@Override
		public void alignWidgets()
		{
			super.alignWidgets();
			setWidth(totalWidth);
			setHeight(totalHeight);
		}
	}

	public final List<GuideComponent> components = new ArrayList<>();

	public String toString()
	{
		return components.toString();
	}

	@Override
	public JsonElement toJson()
	{
		if (components.size() == 1)
		{
			return components.get(0).toJson();
		}

		JsonArray array = new JsonArray();

		for (GuideComponent component : components)
		{
			array.add(component.toJson());
		}

		return array;
	}

	@Override
	public boolean isEmpty()
	{
		for (GuideComponent c : components)
		{
			if (!c.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new CombinedComponentPanel(parent, this);
	}

	@Override
	public Iterator<GuideComponent> iterator()
	{
		return components.iterator();
	}

	public void add(GuideComponent c)
	{
		components.add(c);
		c.parent = this;
	}
}