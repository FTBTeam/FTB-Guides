package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftbguides.events.CreateGuideComponentEvent;
import com.feed_the_beast.ftbguides.gui.GuidePage;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.icon.URLImageIcon;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class GuideComponent
{
	public GuideComponent parent = null;
	public Map<String, String> properties = null;

	public boolean isEmpty()
	{
		return false;
	}

	public abstract IGuideComponentWidget createWidget(ComponentPanel parent);

	public boolean hasProperties()
	{
		return properties != null && !properties.isEmpty();
	}

	public String getProperty(String key, boolean includeParent)
	{
		String p = hasProperties() ? properties.get(key) : null;
		return p == null ? ((parent == null || !includeParent) ? "" : parent.getProperty(key, true)) : p;
	}

	public final String getProperty(String key, boolean includeParent, String def)
	{
		String s = getProperty(key, includeParent);
		return s.isEmpty() ? def : s;
	}

	public final GuideComponent setProperty(String key, String value)
	{
		if (!value.isEmpty())
		{
			if (properties == null)
			{
				properties = new HashMap<>();
			}

			properties.put(key, value);
		}
		else if (properties != null)
		{
			properties.remove(key);

			if (properties.isEmpty())
			{
				properties = null;
			}
		}

		return this;
	}

	public final void setProperty(String key, @Nullable JsonElement json)
	{
		setProperty(key, JsonUtils.isNull(json) ? "" : json.getAsString());
	}

	public boolean isInline()
	{
		return true;
	}

	public static String fixHtmlString(String s)
	{
		return s.isEmpty() ? "" : StringUtils.fixTabs(s, 2).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&");
	}

	public String toString()
	{
		return getClass().getSimpleName();
	}

	public static double parseSize(double original, String s)
	{
		if (s.isEmpty())
		{
			return original;
		}
		else if (s.endsWith("%"))
		{
			return original * Double.parseDouble(s.substring(0, s.length() - 1)) / 100D;
		}
		else if (s.endsWith("px"))
		{
			return Double.parseDouble(s.substring(0, s.length() - 2));
		}
		else if (s.endsWith("x"))
		{
			double x = Double.parseDouble(s.substring(0, s.length() - 1));
			return x >= 0 ? original * x : original * (1D / x);
		}

		return Double.parseDouble(s.substring(0, s.length() - 2));
	}

	public static GuideComponent create(GuidePage page, @Nullable JsonElement json)
	{
		if (JsonUtils.isNull(json))
		{
			return EmptyGuideComponent.INSTANCE;
		}

		if (json.isJsonPrimitive())
		{
			String s = json.getAsString();

			if (s.isEmpty())
			{
				return EmptyGuideComponent.INSTANCE;
			}
			else if (s.equals("\n"))
			{
				return LineBreakGuideComponent.INSTANCE;
			}

			return new TextGuideComponent(fixHtmlString(s));
		}
		else if (json.isJsonArray())
		{
			JsonArray array = json.getAsJsonArray();

			if (array.size() == 0)
			{
				return EmptyGuideComponent.INSTANCE;
			}
			else if (array.size() == 1)
			{
				return create(page, array.get(0));
			}

			CombinedGuideComponent component = new CombinedGuideComponent();

			for (JsonElement e : array)
			{
				GuideComponent c1 = create(page, e);

				if (!c1.isEmpty())
				{
					component.add(c1);
				}
			}

			return component.isEmpty() ? EmptyGuideComponent.INSTANCE : component;
		}

		JsonObject object = json.getAsJsonObject();

		if (object.size() == 0)
		{
			return EmptyGuideComponent.INSTANCE;
		}
		if (object.has("br"))
		{
			return LineBreakGuideComponent.INSTANCE;
		}
		else if (object.has("hr"))
		{
			return HRGuideComponent.INSTANCE;
		}

		GuideComponent component = EmptyGuideComponent.INSTANCE;

		if (object.has("h1"))
		{
			component = create(page, object.get("h1")).setProperty("text_scale", "1.5").setProperty("bold", "true");
		}
		else if (object.has("h2"))
		{
			component = create(page, object.get("h2")).setProperty("text_scale", "1.5");
		}
		else if (object.has("h3"))
		{
			component = create(page, object.get("h3")).setProperty("text_scale", "1.25");
		}
		else if (object.has("img"))
		{
			Icon icon = Icon.EMPTY;

			if (object.has("img_url"))
			{
				icon = page.getIcon(object.get("img_url").getAsString());
			}
			else if (object.has("img"))
			{
				icon = page.getIcon(object.get("img").getAsString());
			}

			if (icon instanceof URLImageIcon && (!object.has("img_width") || !object.has("img_height")))
			{
				try
				{
					BufferedImage image = ImageIO.read(new URL(icon.toString()));
					//TODO: Bind this to ResourceLocaiton so the image doesn't have to be downloaded twice
					object.addProperty("img_width", Integer.toString(image.getWidth()));
					object.addProperty("img_height", Integer.toString(image.getHeight()));
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					icon = Icon.EMPTY;
				}
			}

			if (!icon.isEmpty())
			{
				int iw = object.has("img_width") ? object.get("img_width").getAsInt() : 16;
				int ih = object.has("img_height") ? object.get("img_height").getAsInt() : 16;

				int w = (int) parseSize(iw, object.has("width") ? object.get("width").getAsString() : "");
				int h = (int) parseSize(ih, object.has("height") ? object.get("height").getAsString() : "");

				if (w > 0 && h > 0)
				{
					component = new ImageGuideComponent(icon, w, h);
				}
			}
		}
		else if (object.has("codeblock"))
		{
			List<String> list = new ArrayList<>();

			for (JsonElement e : object.get("codeblock").getAsJsonArray())
			{
				if (e.isJsonPrimitive())
				{
					list.add(fixHtmlString(e.getAsString()));
				}
				else
				{
					list.add(TextFormatting.RED + fixHtmlString(e.toString()));
				}
			}

			component = new CodeblockGuideComponent(list);
		}
		else if (object.has("list"))
		{
			TableGuideComponent list = new TableGuideComponent();
			TableGuideComponent.HeadCellComponent left = list.addRow();
			left.setProperty("align", "right");
			TableGuideComponent.HeadCellComponent right = list.addRow();

			list.setProperty("borders", "false");
			list.setProperty("padding", "0");
			list.hasHead = false;

			list.setProperty("ordering", object.get("ordering"));
			ListOrdering ordering = ListOrdering.NAME_MAP.get(list.getProperty("ordering", true));
			JsonArray array = object.get("list").getAsJsonArray();

			for (int i = 0; i < array.size(); i++)
			{
				TableGuideComponent.CellComponent leftc = new TableGuideComponent.CellComponent(left);
				leftc.add(ordering.createComponent(i));
				list.add(leftc);

				TableGuideComponent.CellComponent rightc = new TableGuideComponent.CellComponent(right);
				rightc.add(create(page, array.get(i)));
				list.add(rightc);
			}

			component = list;

			//component = new TextGuideComponent("Lists aren't supported yet!").setProperty("bold", "true");
		}
		else if (object.has("table"))
		{
			TableGuideComponent table = new TableGuideComponent();

			if (object.has("head"))
			{
				table.hasHead = true;

				for (JsonElement element : object.get("head").getAsJsonArray())
				{
					TableGuideComponent.HeadCellComponent head = table.addRow();
					head.add(create(page, element));

					/* FIXME: align, type, style properties
					if (element.isJsonObject())
					{
						JsonObject object1 = element.getAsJsonObject();
					}*/
				}
			}
			else
			{
				table.hasHead = false;
				int r = 0;

				for (JsonElement element : object.get("table").getAsJsonArray())
				{
					r = Math.max(r, element.getAsJsonArray().size());
				}
			}

			for (JsonElement element : object.get("table").getAsJsonArray())
			{
				JsonArray array = element.getAsJsonArray();

				if (array.size() == table.rows.size())
				{
					for (int i = 0; i < array.size(); i++)
					{
						TableGuideComponent.CellComponent component1 = new TableGuideComponent.CellComponent(table.rows.get(i));
						JsonElement element1 = array.get(i);

						if (element1.isJsonArray())
						{
							for (JsonElement element2 : element1.getAsJsonArray())
							{
								if (!component1.components.isEmpty())
								{
									component1.add(LineBreakGuideComponent.INSTANCE);
								}

								component1.add(create(page, element2));
							}
						}
						else
						{
							component1.add(create(page, element1));
						}

						table.add(component1);
					}
				}
			}

			table.setProperty("borders", object.get("borders"));
			table.setProperty("padding", object.get("padding"));
			component = table;
			//component = new TextGuideComponent("Tables aren't supported yet!").setProperty("bold", "true");
		}
		else if (object.has("yt"))
		{
			String id = object.get("yt").getAsString();
			component = new VideoGuideComponent(page.getIcon("https://img.youtube.com/vi/" + id + "/maxresdefault.jpg"), 480, 270);
			object.addProperty("click", "https://youtu.be/" + id);
			object.addProperty("hover", I18n.format("ftbguides.lang.open_in_browser"));
		}
		else if (object.has("text") || object.has("code"))
		{
			component = new TextGuideComponent((object.has("text") ? object.get("text") : object.get("code")).getAsString());
			component.setProperty("bold", object.get("bold"));
			component.setProperty("italic", object.get("italic"));
			component.setProperty("underlined", object.get("underlined"));
			component.setProperty("bold", object.get("bold"));

			if (object.has("text"))
			{
				component.setProperty("code", object.get("code"));
			}
			else
			{
				component.setProperty("code", "true");
			}
		}

		if (component.isEmpty())
		{
			CreateGuideComponentEvent event = new CreateGuideComponentEvent(page, object);
			event.post();
			component = event.getComponent();
		}

		if (component.isEmpty())
		{
			component = new TextGuideComponent(TextFormatting.RED + object.toString());
		}
		else
		{
			component.setProperty("click", object.get("click"));
			component.setProperty("hover", object.get("hover"));
		}

		return component;
	}
}