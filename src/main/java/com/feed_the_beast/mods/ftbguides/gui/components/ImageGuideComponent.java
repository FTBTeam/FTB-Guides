package com.feed_the_beast.mods.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.icon.ImageIcon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.google.gson.JsonElement;
import javafx.scene.image.Image;

import javax.annotation.Nullable;
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

			if (component.image instanceof ImageIcon)
			{
				setWidth((int) (component.getWidth() / (double) getScreen().getScaleFactor()));
				setHeight((int) (component.getHeight() / (double) getScreen().getScaleFactor()));
			}
			else
			{
				setWidth(component.getWidth());
				setHeight(component.getHeight());
			}

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
			if (!component.hover.isEmpty())
			{
				list.add(component.hover);
			}
		}

		@Override
		public boolean mousePressed(MouseButton button)
		{
			if (isMouseOver())
			{
				if (!component.click.isEmpty() && handleClick(component.click))
				{
					GuiHelper.playClickSound();
					return true;
				}
			}

			return false;
		}

		@Override
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			component.image.draw(x, y, w, h);
		}

		@Nullable
		@Override
		public Object getIngredientUnderMouse()
		{
			return component.image.getIngredient();
		}
	}

	public final ComponentPage page;
	public final Icon image;
	private int width, height;
	public String click = "";
	public String hover = "";

	public ImageGuideComponent(ComponentPage p, Icon i, int w, int h)
	{
		page = p;
		image = i;
		width = w;
		height = h;
	}

	public ImageGuideComponent(ComponentPage page, Icon i)
	{
		this(page, i, -1, -1);
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

	private void loadSizeFromImage()
	{
		if (width == -1 || height == -1)
		{
			JsonElement defJson = page.page.getProperty("default_icon_size");
			int def = defJson.isJsonPrimitive() ? Math.max(1, defJson.getAsInt()) : 16;

			if (image.isLoadedJFXImageInstant())
			{
				Image img = image.loadInstantJFXImage().orElse(null);

				if (img != null)
				{
					if (width == -1)
					{
						width = (int) img.getWidth();
					}

					if (height == -1)
					{
						height = (int) img.getHeight();
					}
				}
			}

			if (width == -1)
			{
				width = def;
			}

			if (height == -1)
			{
				height = def;
			}
		}
	}

	public int getWidth()
	{
		loadSizeFromImage();
		return width;
	}

	public int getHeight()
	{
		loadSizeFromImage();
		return height;
	}

	@Override
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new ImgWidget(parent, this);
	}
}