package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftblib.lib.util.LangKey;
import com.feed_the_beast.ftblib.lib.util.misc.NameMap;
import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public enum GuideType implements IStringSerializable
{
	SERVER_INFO("server_info"),
	MODPACK("modpack"),
	MOD("mod"),
	OTHER("other");

	public static final NameMap<GuideType> NAME_MAP = NameMap.create(OTHER, values());

	private final String name;
	public final LangKey title;
	public final LangKey titlePlural;

	GuideType(String n)
	{
		name = n;
		title = LangKey.of("ftbguides.lang.type." + name);
		titlePlural = LangKey.of("ftbguides.lang.type." + name + ".plural");
	}

	@Override
	public String getName()
	{
		return name;
	}
}