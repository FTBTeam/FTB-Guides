package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.icon.BulletIcon;
import com.feed_the_beast.ftblib.lib.util.misc.NameMap;
import com.google.gson.JsonObject;
import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public class GuideListComponent extends CombinedGuideComponent
{
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

	public Ordering ordering = Ordering.BULLET;
	public int spacing = 0;

	@Override
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new PanelList(parent, this);
	}

	@Override
	public void loadProperties(JsonObject json)
	{
		setProperty("ordering", json.get("ordering"));
		setProperty("spacing", json.get("spacing"));

		ordering = Ordering.NAME_MAP.get(getProperty("ordering", true));

		String s = getProperty("spacing", true);
		spacing = s.isEmpty() ? 0 : Integer.parseInt(s);
	}

	private static class PanelList extends CombinedComponentPanel
	{
		private BulletIcon bullet;
		private GuideListComponent list;

		private PanelList(ComponentPanel parent, GuideListComponent c)
		{
			super(parent, c);
			list = c;
			bullet = new BulletIcon().setColor(getTheme().getContentColor(WidgetType.NORMAL));
		}

		@Override
		protected void drawWidget(Widget widget, int index, int ax, int ay, int w, int h)
		{
			super.drawWidget(widget, index, ax, ay, w, h);

			if (list.ordering.size > 0 && widget.getClass() != Widget.class && !(widget instanceof PanelList))
			{
				String n;
				switch (list.ordering)
				{
					case BULLET:
						bullet.draw(ax - 7, widget.getAY() + 3, 4, 4);
						break;
					case NUMBER:
						n = Integer.toString(index + 1);
						drawString(n, ax - 1 - getStringWidth(n), widget.getAY() + 1);
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
						drawString(n, ax - 1 - getStringWidth(n), widget.getAY() + 1);
						break;
				}
			}
		}
	}
}