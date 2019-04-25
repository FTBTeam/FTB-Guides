package com.feed_the_beast.mods.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.util.text_components.TextComponentParser;
import com.feed_the_beast.mods.ftbguides.gui.GuidePage;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class ComponentPage
{
	public static final Pattern I18N_PATTERN = Pattern.compile("\\{([a-zA-Z0-9\\._\\-]*?)\\}");
	public static final Pattern STRIKETHROUGH_PATTERN = Pattern.compile("\\~\\~(.*?)\\~\\~");
	public static final String STRIKETHROUGH_REPLACE = "&m$1&m";
	public static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.*?)\\*\\*|__(.*?)__");
	public static final String BOLD_REPLACE = "&l$1$2&l";
	public static final Pattern ITALIC_PATTERN = Pattern.compile("\\*(.*?)\\*|_(.*?)_");
	public static final String ITALIC_REPLACE = "&o$1$2&o";
	public static final Pattern LINK_PATTERN = Pattern.compile("^\\[(.*)\\]\\((.*)\\)$");
	public static final Pattern IMAGE_PATTERN = Pattern.compile("^!\\[(.*)\\]\\((.*)\\)$");
	public static final Pattern HR_PATTERN = Pattern.compile("^-{3,}|\\*{3,}|_{3,}$");

	public final GuidePage page;
	public final List<GuideComponent> components;

	public ComponentPage(GuidePage p)
	{
		page = p;
		components = new ArrayList<>();
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

	public void printlnMarkdown(String s)
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

		Matcher i18nMatcher = I18N_PATTERN.matcher(s);

		if (i18nMatcher.find())
		{
			i18nMatcher.reset();

			StringBuffer sb = new StringBuffer(s.length());

			while (i18nMatcher.find())
			{
				i18nMatcher.appendReplacement(sb, I18n.format(i18nMatcher.group(1)));
			}

			i18nMatcher.appendTail(sb);
			s = sb.toString();
		}

		boolean b = false;

		b = !s.equals(s = STRIKETHROUGH_PATTERN.matcher(s).replaceAll(STRIKETHROUGH_REPLACE)) | b;
		b = !s.equals(s = BOLD_PATTERN.matcher(s).replaceAll(BOLD_REPLACE)) | b;
		b = !s.equals(s = ITALIC_PATTERN.matcher(s).replaceAll(ITALIC_REPLACE)) | b;

		if (b)
		{
			s = TextComponentParser.parse(s, null).getFormattedText();
		}

		double scale = 1D;
		boolean bold = false;

		if (s.startsWith("###"))
		{
			s = s.substring(3).trim();
			scale = 1.25D;
		}
		else if (s.startsWith("##"))
		{
			s = s.substring(2).trim();
			scale = 1.25D;
			bold = true;
		}
		else if (s.startsWith("#"))
		{
			s = s.substring(1).trim();
			scale = 1.5D;
			bold = true;
		}

		Matcher matcher = IMAGE_PATTERN.matcher(s);

		if (matcher.find())
		{
			ImageGuideComponent component = new ImageGuideComponent(page.getIcon(matcher.group(2)));
			component.hover = matcher.group(1);
			println(component);
			return;
		}

		matcher = LINK_PATTERN.matcher(s);

		if (matcher.find())
		{
			TextGuideComponent component = new TextGuideComponent(matcher.group(1));
			component.textScale = scale;
			component.bold = bold;
			component.click = matcher.group(2);
			println(component);
			return;
		}

		TextGuideComponent component = new TextGuideComponent(s);
		component.textScale = scale;
		component.bold = bold;
		println(component);
	}
}