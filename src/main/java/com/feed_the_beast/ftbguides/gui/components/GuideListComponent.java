package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.ftblib.lib.icon.BulletIcon;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.misc.NameMap;
import com.google.gson.JsonObject;
import net.minecraft.util.IStringSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuideListComponent extends GuideComponent
{
	private static final Icon CODE_BACKGROUND = Color4I.rgba(0x33AAAAAA);
	private static final Icon SCROLL_BAR_BACKGROUND = Color4I.rgba(0x33333333);

	public enum Ordering implements IStringSerializable
	{
		NONE("none", 0),
		BULLET("bullet", 8),
		NUMBER("number", 12),
		LETTER("letter", 10);

		public static final NameMap<Ordering> NAME_MAP = NameMap.create(BULLET, values());

		private final String name;
		public final int size;

		Ordering(String n, int s)
		{
			name = n;
			size = s;
		}

		@Override
		public String getName()
		{
			return name;
		}
	}

	private final List<GuideComponent> components;
	public Ordering ordering = Ordering.BULLET;
	public int spacing = 0;

	public GuideListComponent(List<GuideComponent> c)
	{
		components = c;

		/*
		if (json.isJsonObject())
		{
			JsonObject o = json.getAsJsonObject();

			if (o.has("list"))
			{
				for (JsonElement element : o.get("list").getAsJsonArray())
				{
					components.add(GuideComponent.create(element));
				}
			}

			type = o.has("type") ? Type.NAME_MAP.get(o.get("type").getAsString()) : Type.NONE;
			ordering = (type != Type.HORIZONTAL && o.has("ordering")) ? Ordering.NAME_MAP.get(o.get("ordering").getAsString()) : type.defaultOrdering;
			spacing = o.has("spacing") ? o.get("spacing").getAsInt() : 0;
		}
		else
		{
			for (JsonElement element : json.getAsJsonArray())
			{
				components.add(GuideComponent.create(element));
			}

			ordering = type.defaultOrdering;
			spacing = 0;
		}*/
	}

	@Override
	public IGuideComponentWidget createWidget(Panel parent)
	{
		return new PanelList(parent.gui, parent.hasFlag(Widget.UNICODE));
	}

	@Override
	public GuideListComponent copy()
	{
		GuideListComponent component = new GuideListComponent(new ArrayList<>(components.size()));
		component.ordering = ordering;
		component.spacing = spacing;

		for (GuideComponent component1 : components)
		{
			component.components.add(component1.copy());
		}

		return component;
	}

	@Override
	public boolean isEmpty()
	{
		for (GuideComponent component : components)
		{
			if (!component.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public void loadProperties(JsonObject json)
	{
		if (json.has("ordering"))
		{
			setProperty("ordering", json.get("ordering").getAsString());
		}

		ordering = Ordering.NAME_MAP.get(getProperty("ordering"));
	}

	private class PanelList extends Panel implements IGuideComponentWidget
	{
		private final PanelScrollBar scrollBar;
		private final WidgetLayout layout;
		private BulletIcon bullet;

		private PanelList(GuiBase gui, boolean unicodeFont)
		{
			super(gui, ordering.size, 0, 0, 0);
			//addFlags(DEFAULTS);

			scrollBar = new PanelScrollBar(gui, 0, 0, 1, 4, 0, this)
			{
				@Override
				public Plane getPlane()
				{
					return Plane.HORIZONTAL;
				}

				@Override
				public Icon getBackground()
				{
					return SCROLL_BAR_BACKGROUND;
				}

				@Override
				public boolean canMouseScroll()
				{
					return gui.isMouseOver(parent);
				}
			};

			if (unicodeFont)
			{
				addFlags(UNICODE);
			}

			layout = new WidgetLayout.Vertical(0, spacing, 0);
			bullet = new BulletIcon().setColor(gui.getTheme().getContentColor(false));
		}

		@Override
		public void addWidgets()
		{
			if (isEmpty())
			{
				setWidth(0);
				setHeight(0);
				return;
			}

			setWidth(parent.width - ordering.size);

			for (GuideComponent component : components)
			{
				add((Widget) component.createWidget(this));
			}

			parent.add(scrollBar);

			if (widgets.isEmpty())
			{
				setWidth(0);
				setHeight(0);
				return;
			}

			align(layout);
			Widget last = widgets.get(widgets.size() - 1);
			int s;

			setHeight(last.posY + last.height);
			s = 0;

			for (Widget widget : widgets)
			{
				s = Math.max(s, widget.width);
			}

			s += ordering.size;

			setWidth(Math.min(s, parent.width) - ordering.size);
			scrollBar.setWidth(width + ordering.size);
			scrollBar.setElementSize(s - ordering.size);
			scrollBar.setSrollStepFromOneElementSize(10);
			scrollBar.sliderSize = scrollBar.width / 10;
		}

		@Override
		protected void renderWidget(Widget widget, int index, int ax, int ay, int w, int h)
		{
			widget.draw();

			if (ordering.size > 0 && widget.getClass() != Widget.class && !(widget instanceof PanelList))
			{
				String n;
				switch (ordering)
				{
					case BULLET:
						bullet.draw(ax - 7, widget.getAY() + 3, 4, 4);
						break;
					case NUMBER:
						n = Integer.toString(index + 1);
						gui.drawString(n, ax - 1 - gui.getStringWidth(n), widget.getAY() + 1);
						break;
					case LETTER:
						char c = (char) ('a' + index);
						if (c > 'z')
						{
							c = (char) ('A' + index);
						}
						if (c > 'Z')
						{
							c = '-';
						}

						n = Character.toString(c);
						gui.drawString(n, ax - 1 - gui.getStringWidth(n), widget.getAY() + 1);
						break;
				}
			}
		}
	}
}