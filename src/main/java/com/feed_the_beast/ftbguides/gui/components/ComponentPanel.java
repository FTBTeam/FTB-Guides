package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Icon;

import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class ComponentPanel extends Panel implements IGuideComponentWidget
{
	public boolean fixedWidth = false, fixedHeight = false;
	public int totalWidth, totalHeight;

	public ComponentPanel(GuiBase gui)
	{
		super(gui);
		addFlags(DEFAULTS | UNICODE);
	}

	public abstract List<GuideComponent> getComponents();

	@Override
	public void addWidgets()
	{
		for (GuideComponent component : getComponents())
		{
			if (!component.isEmpty())
			{
				add((Widget) component.createWidget(this));
			}
		}
	}

	@Override
	public void alignWidgets()
	{
		int x = 0;
		int y = 1;
		int h = 0;
		int w = 0;
		totalWidth = 0;
		totalHeight = 0;

		for (Widget widget : widgets)
		{
			IGuideComponentWidget cwidget = (IGuideComponentWidget) widget;
			if (!cwidget.isInline() || fixedWidth && x + widget.width > width)
			{
				totalWidth = Math.max(totalWidth, x);
				x = 0;
				y += h;
				h = 0;
			}

			widget.posX = x;
			widget.posY = y;

			x += widget.width;
			w = Math.max(w, x);
			h = Math.max(h, widget.height);
		}

		totalHeight = y + h;

		if (!fixedWidth)
		{
			setWidth(totalWidth);
		}

		if (!fixedHeight)
		{
			setHeight(totalHeight);
		}
	}

	@Override
	public Icon getIcon()
	{
		return gui.getTheme().getPanelBackground();
	}
}