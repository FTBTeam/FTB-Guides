package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.gui.GuiGuide;
import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.MismatchingParentPanelException;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class TableGuideComponent extends CombinedGuideComponent
{
	public enum Align
	{
		NONE(" --- |"),
		LEFT(" :-- |"),
		RIGHT(" --: |"),
		CENTER(" :-: |");

		public final String md;

		Align(String s)
		{
			md = s;
		}
	}

	public static class HeadCellComponent extends CombinedGuideComponent
	{
		public final TableGuideComponent table;
		public Map<String, String> style = null;

		public HeadCellComponent(TableGuideComponent t)
		{
			table = t;
			setProperty("bold", "true");
		}
	}

	public static class CellComponent extends CombinedGuideComponent
	{
		public final HeadCellComponent head;

		public CellComponent(HeadCellComponent h)
		{
			head = h;
		}

		@Override
		public String getProperty(String key, boolean includeParent)
		{
			String p = hasProperties() ? properties.get(key) : null;

			if (p == null)
			{
				p = head.style != null && !head.style.isEmpty() ? head.style.get(key) : null;
			}

			return p == null ? ((parent == null || !includeParent) ? "" : parent.getProperty(key, true)) : p;
		}
	}

	public boolean hasHead = true;
	public final List<HeadCellComponent> rows;

	public TableGuideComponent()
	{
		rows = new ArrayList<>();
	}

	public HeadCellComponent addRow()
	{
		HeadCellComponent component = new HeadCellComponent(this);
		rows.add(component);
		return component;
	}

	@Override
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new PanelTable(parent, this);
	}

	private static class PanelTable extends ComponentPanel
	{
		private TableGuideComponent table;
		public int padding;
		public boolean drawBorders;

		private PanelTable(ComponentPanel parent, TableGuideComponent c)
		{
			super(parent);
			setUnicode(true);
			table = c;
			padding = Integer.parseInt(table.getProperty("padding", true, "2"));
			drawBorders = table.getProperty("borders", true, "true").equals("true");
		}

		@Override
		public List<GuideComponent> getComponents()
		{
			return table.components;
		}

		@Override
		public void refreshWidgets()
		{
			boolean printInfo = FTBLibConfig.debugging.print_more_info && false;

			totalWidth = 0;
			totalHeight = 0;
			widgets.clear();
			pushFontUnicode(true);
			int widths[] = new int[table.rows.size()];
			int heights[] = new int[table.components.size() / widths.length + (table.hasHead ? 1 : 0)];

			if (printInfo)
			{
				FTBGuides.LOGGER.info("Table: Begin [" + (widths.length * heights.length) + " cells, " + widths.length + "x" + heights.length + "]");
			}

			int minWidths[] = new int[widths.length];
			int maxWidths[] = new int[widths.length];
			int xpos[] = new int[widths.length];
			int ypos[] = new int[heights.length];
			int i;

			if (getComponents().size() % widths.length != 0)
			{
				FTBGuides.LOGGER.error("There is something wrong with this table! " + widths.length + "x" + heights.length + " & " + getComponents().size() + " elements: " + getComponents());
			}

			for (i = 0; i < maxWidths.length; i++)
			{
				maxWidths[i] = Integer.parseInt(table.rows.get(i).getProperty("max_width", true, Integer.toString(Integer.MAX_VALUE)));
				minWidths[i] = Integer.parseInt(table.rows.get(i).getProperty("min_width", true, "8"));
			}

			try
			{
				if (table.hasHead)
				{
					for (i = 0; i < widths.length; i++)
					{
						ComponentPanel widget = (ComponentPanel) table.rows.get(i).createWidget(this);

						widget.maxWidth = maxWidths[i];
						widget.refreshWidgets();

						widths[i] = Math.max(widths[i], widget.width);
						heights[0] = Math.max(heights[0], widget.height);

						if (printInfo)
						{
							FTBGuides.LOGGER.info("Table Head " + i + ": " + widget.width + ":" + widget.height);
						}

						add(widget);
					}
				}

				i = table.hasHead ? widths.length : 0;

				for (GuideComponent component : getComponents())
				{
					Widget widget = (Widget) component.createWidget(this);

					int hi = i % widths.length;
					int vi = i / widths.length;

					if (widget instanceof ComponentPanel)
					{
						((ComponentPanel) widget).maxWidth = maxWidths[hi];
					}

					if (widget instanceof Panel)
					{
						((Panel) widget).refreshWidgets();
					}

					widths[hi] = Math.max(widths[hi], widget.width);
					heights[vi] = Math.max(heights[vi], widget.height);

					if (printInfo)
					{
						FTBGuides.LOGGER.info("Table Cell " + hi + ":" + vi + ": " + widget.width + ":" + widget.height);
					}

					add(widget);
					i++;
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

			for (i = 0; i < widths.length; i++)
			{
				widths[i] = Math.max(minWidths[i], widths[i]) + 4;
				widths[i] += padding * 2;
				totalWidth += widths[i];

				for (int j = 0; j < i; j++)
				{
					xpos[i] += widths[j];
				}
			}

			for (i = 0; i < heights.length; i++)
			{
				heights[i] += padding * 2;
				totalHeight += heights[i];

				for (int j = 0; j < i; j++)
				{
					ypos[i] += heights[j];
				}
			}

			for (i = 0; i < widgets.size(); i++)
			{
				Widget widget = widgets.get(i);
				int hi = i % widths.length;
				int vi = i / widths.length;

				if (widget instanceof Panel)
				{
					for (Widget w : ((Panel) widget).widgets)
					{
						w.posX += padding;
						w.posY += padding + (heights[vi] - widget.height) / 2;
					}
				}

				widget.posX = xpos[hi];
				widget.posY = ypos[vi];
				widget.width = widths[hi];
				widget.height = heights[vi];
			}

			setSize(totalWidth, totalHeight);

			if (printInfo)
			{
				FTBGuides.LOGGER.info("Table: End " + width + ":" + height);
			}

			popFontUnicode();
		}

		@Override
		public void addWidgets()
		{
		}

		@Override
		public void alignWidgets()
		{
		}

		@Override
		protected void drawOffsetPanelBackground(int ax, int ay)
		{
			super.drawOffsetPanelBackground(ax, ay);

			if (!drawBorders)
			{
				return;
			}

			GlStateManager.disableTexture2D();
			GlStateManager.color(1F, 1F, 1F, 1F);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			Color4I color = ((GuiGuide) getGui()).page.lineColor;

			for (Widget widget : widgets)
			{
				int x = widget.getAX();
				int y = widget.getAY();
				GuiHelper.addRectToBuffer(buffer, x, y, widget.width, 1, color);
				GuiHelper.addRectToBuffer(buffer, x, y, 1, widget.height, color);
			}

			GuiHelper.addRectToBuffer(buffer, ax, ay + height - 1, width, 1, color);
			GuiHelper.addRectToBuffer(buffer, ax + width - 1, ay, 1, height, color);

			tessellator.draw();
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.enableTexture2D();
		}
	}
}