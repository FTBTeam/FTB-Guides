package com.feed_the_beast.mods.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.util.text_components.TextComponentParser;
import com.feed_the_beast.mods.ftbguides.gui.GuidePage;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class ComponentPage
{
	public static final Pattern COMMENT_PATTERN = Pattern.compile("\\<\\!--(?:.|\\s)*?--\\>\\s?", Pattern.MULTILINE);
	public static final Pattern I18N_PATTERN = Pattern.compile("\\{([a-zA-Z0-9\\._\\-]*?)\\}", Pattern.MULTILINE);
	public static final Pattern REFERENCE_PATTERN = Pattern.compile("^\\[\\s*(.*?)\\s*\\]\\:\\s*(.*?)\\s*$\\s?", Pattern.MULTILINE);
	public static final Pattern STRIKETHROUGH_PATTERN = Pattern.compile("(?<!\\\\)(\\~\\~)(.+?)\\1");
	public static final String STRIKETHROUGH_REPLACE = "&m$2&m";
	public static final Pattern BOLD_PATTERN = Pattern.compile("(?<!\\\\)(\\*\\*|__)(.+?)\\1");
	public static final String BOLD_REPLACE = "&l$2&l";
	public static final Pattern ITALIC_PATTERN = Pattern.compile("(?<!\\\\)(\\*|_)(.+?)\\1");
	public static final String ITALIC_REPLACE = "&o$2&o";
	public static final Pattern LINK_PATTERN = Pattern.compile("^(!)?\\[(.*)\\]\\((.*)\\)$");
	public static final Pattern HR_PATTERN = Pattern.compile("^-{3,}|\\*{3,}|_{3,}$");
	public static final Pattern HEADING_PATTERN = Pattern.compile("^(#+)\\s*(.*)$");
	public static final Pattern POST_PROCESSING_PATTERN = Pattern.compile("\\\\(\\\\|\\*|_|\\~)");
	public static final String POST_PROCESSING_REPLACE = "$1";

	public final GuidePage page;
	public final List<GuideComponent> components;
	private final Map<String, String> references;

	public ComponentPage(GuidePage p)
	{
		page = p;
		components = new ArrayList<>();
		references = new HashMap<>();
	}

	public String getReference(String key)
	{
		String s = references.get(key);
		return s == null ? "" : s;
	}

	public void println(GuideComponent component)
	{
		if (!components.isEmpty() && component.isInline())
		{
			components.add(LineBreakGuideComponent.INSTANCE);
		}

		components.add(component);
	}

	public void println(String text)
	{
		println(text.isEmpty() ? LineBreakGuideComponent.INSTANCE : new TextGuideComponent(text));
	}

	public void println()
	{
		println(LineBreakGuideComponent.INSTANCE);
	}

	public void println(@Nullable ITextComponent component)
	{
		if (component != null)
		{
			for (ITextComponent c : component)
			{
				TextGuideComponent t = new TextGuideComponent(c.getUnformattedComponentText());
				t.bold = c.getStyle().getBold();
				t.italic = c.getStyle().getItalic();
				t.strikethrough = c.getStyle().getStrikethrough();
				t.underlined = c.getStyle().getUnderlined();

				ClickEvent clickEvent = c.getStyle().getClickEvent();

				if (clickEvent != null)
				{
					t.click = clickEvent.getAction().getCanonicalName() + ":" + clickEvent.getValue();
				}

				components.add(t);
			}
		}

		components.add(LineBreakGuideComponent.INSTANCE);
	}

	public void processAsMarkdown(String text)
	{
		if (text.isEmpty())
		{
			return;
		}

		text = COMMENT_PATTERN.matcher(text).replaceAll("");

		Matcher i18nMatcher = I18N_PATTERN.matcher(text);

		while (i18nMatcher.find())
		{
			i18nMatcher.reset();

			StringBuffer sb = new StringBuffer(text.length());

			while (i18nMatcher.find())
			{
				i18nMatcher.appendReplacement(sb, I18n.format(i18nMatcher.group(1)));
			}

			i18nMatcher.appendTail(sb);
			text = sb.toString();
			i18nMatcher = I18N_PATTERN.matcher(text);
		}

		Matcher refMatcher = REFERENCE_PATTERN.matcher(text);

		while (refMatcher.find())
		{
			String key = refMatcher.group(1);
			String value = refMatcher.group(2);

			if (key.startsWith("#"))
			{
				if (key.length() > 1)
				{
					page.properties.put(key.substring(1), new JsonPrimitive(value));
				}
			}
			else
			{
				references.put(key, value);
			}
		}

		text = refMatcher.replaceAll("");

		String[] lines = text.split("\n");

		for (String s : lines)
		{
			printlnMarkdown(s);
		}
	}

	private void printlnMarkdown(String s)
	{
		s = s.trim();

		if (s.isEmpty())
		{
			println(LineBreakGuideComponent.INSTANCE);
			return;
		}
		else if (HR_PATTERN.matcher(s).matches())
		{
			println(HRGuideComponent.INSTANCE);
			return;
		}

		boolean b = false;

		b = !s.equals(s = STRIKETHROUGH_PATTERN.matcher(s).replaceAll(STRIKETHROUGH_REPLACE)) | b;
		b = !s.equals(s = BOLD_PATTERN.matcher(s).replaceAll(BOLD_REPLACE)) | b;
		b = !s.equals(s = ITALIC_PATTERN.matcher(s).replaceAll(ITALIC_REPLACE)) | b;

		if (b)
		{
			s = TextComponentParser.parse(s, null).getFormattedText();
		}

		s = POST_PROCESSING_PATTERN.matcher(s).replaceAll(POST_PROCESSING_REPLACE);

		double scale = 1D;
		boolean bold = false;

		int heading = 0;

		Matcher matcher = HEADING_PATTERN.matcher(s);

		if (matcher.find())
		{
			heading = matcher.group(1).length();
			s = matcher.group(2);
		}

		if (heading >= 3)
		{
			scale = 1.25D;
		}
		else if (heading == 2)
		{
			scale = 1.25D;
			bold = true;
		}
		else if (heading == 1)
		{
			scale = 1.5D;
			bold = true;
		}

		matcher = LINK_PATTERN.matcher(s);

		if (matcher.find())
		{
			if (matcher.group(1) != null && matcher.group(1).equals("!"))
			{
				ImageGuideComponent component = new ImageGuideComponent(this, page.getIcon(matcher.group(3)));
				component.hover = matcher.group(2);
				println(component);
				return;
			}
			else
			{
				TextGuideComponent component = new TextGuideComponent(matcher.group(2));
				component.textScale = scale;
				component.bold = bold;
				component.click = matcher.group(3);
				println(component);
				return;
			}
		}

		TextGuideComponent component = new TextGuideComponent(s);
		component.textScale = scale;
		component.bold = bold;
		println(component);
	}
}