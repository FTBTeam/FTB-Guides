package com.feed_the_beast.mods.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.util.misc.NameMap;
import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public enum ListOrdering implements IStringSerializable
{
	NONE("none")
			{
				private GuideComponent component = new TextGuideComponent("  ");

				@Override
				public GuideComponent createComponent(int index)
				{
					return component;
				}
			},
	BULLET("bullet")
			{
				@Override
				public GuideComponent createComponent(int index)
				{
					return BulletGuideComponent.INSTANCE;
				}
			},
	NUMBER("number")
			{
				@Override
				public GuideComponent createComponent(int index)
				{
					return index < 0 ? BulletGuideComponent.INSTANCE : new TextGuideComponent(Integer.toString(index));
				}
			},
	LETTER("letter")
			{
				private TextGuideComponent[] components;

				@Override
				public GuideComponent createComponent(int index)
				{
					if (components == null)
					{
						int s = 'z' - 'a' + 1;
						components = new TextGuideComponent[s * 2];

						for (int i = 0; i < s; i++)
						{
							components[i] = new TextGuideComponent(Character.toString((char) ('a' + index)));
							components[i + s] = new TextGuideComponent(Character.toString((char) ('A' + index)));
						}
					}

					return index < 0 || index >= components.length ? BulletGuideComponent.INSTANCE : components[index];
				}
			};

	public static final NameMap<ListOrdering> NAME_MAP = NameMap.create(BULLET, values());

	private final String name;

	ListOrdering(String n)
	{
		name = n;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public GuideComponent createComponent(int index)
	{
		return EmptyGuideComponent.INSTANCE;
	}
}