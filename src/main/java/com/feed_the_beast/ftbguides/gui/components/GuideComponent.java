package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftbguides.events.CreateGuideComponentEvent;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
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

	public JsonElement toJson()
	{
		return new JsonPrimitive(toString());
	}

	public boolean hasProperties()
	{
		return properties != null && !properties.isEmpty();
	}

	public final String getProperty(String key, boolean includeParent)
	{
		String p = hasProperties() ? properties.get(key) : null;
		return p == null ? ((parent == null || !includeParent) ? "" : parent.getProperty(key, true)) : p;
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

	public void loadProperties(JsonObject json)
	{
	}

	public static String fixHtmlString(String s)
	{
		return s.isEmpty() ? "" : s.replace("\t", "  ").replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&");
	}

	public String toString()
	{
		return getClass().getSimpleName();
	}

	public static GuideComponent create(@Nullable JsonElement json)
	{
		if (JsonUtils.isNull(json))
		{
			return EmptyGuideComponent.INSTANCE;
		}

		if (json.isJsonPrimitive())
		{
			String s = fixHtmlString(json.getAsString());
			return s.isEmpty() ? EmptyGuideComponent.INSTANCE : new TextGuideComponent(s);
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
				return create(array.get(0));
			}

			CombinedGuideComponent component = new CombinedGuideComponent();

			for (JsonElement e : array)
			{
				GuideComponent c1 = create(e);

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
			component = create(object.get("h1"));
			component.setProperty("bold", "true");
			component.setProperty("underlined", "true");
		}
		else if (object.has("h2"))
		{
			component = create(object.get("h2"));
			component.setProperty("bold", "true");
		}
		else if (object.has("h3"))
		{
			component = create(object.get("h3"));
		}
		else if (object.has("img"))
		{
			component = new ImageGuideComponent(Icon.getIcon(object.get("img_url")));
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
			component = new GuideListComponent();

			for (JsonElement e : object.get("list").getAsJsonArray())
			{
				((GuideListComponent) component).add(create(e));
			}
			//component = new TextGuideComponent("Lists aren't supported yet!").setProperty("bold", "true");
		}
		else if (object.has("table"))
		{
			component = new TextGuideComponent("Tables aren't supported yet!").setProperty("bold", "true");
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
			CreateGuideComponentEvent event = new CreateGuideComponentEvent(object);
			event.post();
			component = event.getComponent();
		}

		if (component.isEmpty())
		{
			component = new TextGuideComponent(TextFormatting.RED + object.toString());
		}
		else
		{
			component.loadProperties(object);
		}

		return component;
	}
}