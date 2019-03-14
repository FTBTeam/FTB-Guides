package com.feed_the_beast.mods.ftbguides.gui;

import com.feed_the_beast.ftblib.lib.util.misc.NameMap;
import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public enum GuideType implements IStringSerializable
{
	MODPACK("modpack"),
	MOD("mod"),
	OTHER("other");

	public static final NameMap<GuideType> NAME_MAP = NameMap.create(OTHER, values());

	private final String name;
	public final String title;
	public final String titlePlural;

	GuideType(String n)
	{
		name = n;
		title = "ftbguides.lang.type." + name;
		titlePlural = "ftbguides.lang.type." + name + ".plural";
	}

	@Override
	public String getName()
	{
		return name;
	}
}