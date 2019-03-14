package com.feed_the_beast.mods.ftbguides.gui.components;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.MismatchingParentPanelException;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.mods.ftbguides.FTBGuides;
import com.feed_the_beast.mods.ftbguides.FTBGuidesLocalConfig;
import com.feed_the_beast.mods.ftbguides.gui.GuiGuide;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class ComponentPanel extends Panel implements IGuideComponentWidget
{
	public int maxWidth;
	public int totalWidth, totalHeight;

	public ComponentPanel(Panel panel)
	{
		super(panel);
		setUnicode(FTBGuidesLocalConfig.general.use_unicode_font);
	}

	public abstract List<GuideComponent> getComponents();

	@Override
	public void refreshWidgets()
	{
		boolean printInfo = FTBLibConfig.debugging.print_more_info && false;

		int x = 0;
		int y = 1;
		int h = 0;
		int w = 0;
		//int i = 0;
		totalWidth = 0;
		totalHeight = 0;
		widgets.clear();

		Theme theme = getGui().getTheme();
		theme.pushFontUnicode(FTBGuidesLocalConfig.general.use_unicode_font);
		addWidgets();

		if (printInfo)
		{
			FTBGuides.LOGGER.info(getClass().getName() + ": Begin [" + getComponents().size() + " components]");
		}

		try
		{
			int i = 0;

			if (getComponents().size() == 1)
			{
				Widget widget = (Widget) getComponents().get(0).createWidget(this);

				if (widget instanceof Panel)
				{
					((Panel) widget).refreshWidgets();
				}

				totalWidth = widget.width;
				totalHeight = widget.height;
				add(widget);
			}
			else
			{
				int ci = 0;

				for (GuideComponent component : getComponents())
				{
					if (!component.isEmpty())
					{
						Widget widget = (Widget) component.createWidget(this);

						if (widget instanceof Panel)
						{
							((Panel) widget).refreshWidgets();
						}

						if (x + widget.width > maxWidth || !component.isInline())
						{
							if (widget.width > maxWidth)
							{
								totalWidth = Math.max(totalWidth, widget.width);
							}
							else
							{
								totalWidth = Math.max(totalWidth, x);
							}

							x = 0;
							y += h;
							h = 0;
						}

						widget.posX = x;
						widget.posY = y;

						if (printInfo)
						{
							FTBGuides.LOGGER.info(getClass().getName() + ": " + i + " - " + totalWidth + ":" + totalHeight + " ; [" + widget.width + ":" + widget.height + "] " + widget.getClass().getSimpleName());
						}

						x += widget.width;
						w = Math.max(w, x);
						h = Math.max(h, widget.height);

						add(widget);
						i++;
					}

					ci++;
				}

				totalWidth = Math.max(totalWidth, x);
				y += h;
				totalHeight = y;
			}
		}
		catch (MismatchingParentPanelException ex)
		{
			FTBGuides.LOGGER.error(ex.getMessage());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		alignWidgets();

		if (printInfo)
		{
			FTBGuides.LOGGER.info(getClass().getName() + ": End " + width + ":" + height);
		}

		theme.popFontUnicode();
	}

	@Override
	public void addWidgets()
	{
		maxWidth = Integer.MAX_VALUE;
	}

	@Override
	public void alignWidgets()
	{
		setWidth(totalWidth);
		setHeight(totalHeight);
	}

	@Override
	public void drawOffsetBackground(Theme theme, int x, int y, int w, int h)
	{
		if (!Theme.renderDebugBoxes)
		{
			return;
		}

		setOffset(false);
		boolean mouseOver = isMouseOver();
		setOffset(true);
		if (mouseOver)
		{
			GlStateManager.disableTexture2D();
			GlStateManager.color(1F, 1F, 1F, 1F);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			Color4I color = ((GuiGuide) getGui()).page.lineColor.withAlpha(50);

			if (!(parent instanceof GuiBase))
			{
				GuiHelper.addRectToBuffer(buffer, x, y, w, h, color);
			}

			for (Widget widget : widgets)
			{
				if (widget instanceof ComponentPanel || !widget.isMouseOver() || widget.parent instanceof TableGuideComponent.CombinedComponentPanel)
				{
					continue;
				}

				GuiHelper.addRectToBuffer(buffer, widget.getX(), widget.getY(), widget.width, widget.height, color);
			}

			tessellator.draw();
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.enableTexture2D();
		}
	}
}