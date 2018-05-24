package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ImageGuideComponent extends GuideComponent
{
	public static class ImgWidget extends Widget implements IGuideComponentWidget
	{
		private final ImageGuideComponent component;

		public ImgWidget(ComponentPanel parent, ImageGuideComponent c)
		{
			super(parent);
			component = c;
			setWidth((int) (component.width / (double) getScreen().getScaleFactor()));
			setHeight((int) (component.height / (double) getScreen().getScaleFactor()));

			if (width > parent.maxWidth)
			{
				int w = Math.min(parent.maxWidth, width);
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
			if (isMouseOver())
			{
				String s = component.getProperty("click", true);

				if (!s.isEmpty() && handleClick(s))
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
	public final int width, height;

	public ImageGuideComponent(Icon i, int w, int h)
	{
		image = i;
		width = w;
		height = h;
	}

	public ImageGuideComponent(Icon i)
	{
		this(i, 16, 16);
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
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new ImgWidget(parent, this);
	}
}