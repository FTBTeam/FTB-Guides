package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ImgGuideComponent extends GuideComponent
{
	private static class ImgWidget extends Widget implements IGuideComponentWidget
	{
		private final ImgGuideComponent component;

		public ImgWidget(Panel parent, ImgGuideComponent c)
		{
			super(parent.gui);
			component = c;
			setWidth(component.width / parent.gui.getScreen().getScaleFactor());
			setHeight(component.height / parent.gui.getScreen().getScaleFactor());

			if (width > parent.width)
			{
				int w = Math.min(parent.width, width);
				double h = height * (w / (double) width);
				setWidth(w);
				setHeight(Math.max((int) h, 0));
			}
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
			String s = component.getProperty("hover", false);

			if (!s.isEmpty())
			{
				list.add(s);
			}
		}

		@Override
		public boolean mousePressed(MouseButton button)
		{
			if (gui.isMouseOver(this))
			{
				String s = component.getProperty("click", true);

				if (!s.isEmpty() && gui.onClickEvent(s))
				{
					GuiHelper.playClickSound();
					return true;
				}
			}

			return false;
		}

		@Override
		public Icon getIcon()
		{
			return component.image;
		}
	}

	public final Icon image;
	public int width, height;

	public ImgGuideComponent(Icon i)
	{
		image = i;
	}

	public String toString()
	{
		return image.toString();
	}

	@Override
	public boolean isEmpty()
	{
		return image.isEmpty();
	}

	@Override
	public void loadProperties(JsonObject json)
	{
		width = json.get("img_width").getAsInt();
		height = json.get("img_height").getAsInt();
	}

	@Override
	public IGuideComponentWidget createWidget(Panel parent)
	{
		return new ImgWidget(parent, this);
	}
}