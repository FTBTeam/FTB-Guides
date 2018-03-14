package com.feed_the_beast.ftbguides.gui.components;

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
	}

	public final List<GuideComponent> components = new ArrayList<>();

	public String toString()
	{
		return components.toString();
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

	public CombinedGuideComponent add(GuideComponent c)
	{
		components.add(c);
		c.parent = this;
		return this;
	}
}