package com.feed_the_beast.mods.ftbguides.events;

import com.feed_the_beast.mods.ftbguides.gui.GuidePage;
import com.feed_the_beast.mods.ftbguides.gui.components.HRGuideComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ClientGuideEvent extends FTBGuidesEvent
{
	private final GuidePage root;
	private final Map<String, GuidePage> cache;

	public ClientGuideEvent(GuidePage r)
	{
		root = r;
		cache = new HashMap<>();
	}

	public GuidePage getPage(String modid)
	{
		GuidePage page = cache.get(modid);

		if (page == null)
		{
			page = root.getSub(modid);
			ModContainer mod = Loader.instance().getIndexedModList().get(modid);

			if (mod != null)
			{
				if (!mod.getMetadata().description.isEmpty())
				{
					for (String s : mod.getMetadata().description.split("\n"))
					{
						page.text.println(s);
					}

					page.text.println(HRGuideComponent.INSTANCE);
				}

				page.title = new TextComponentString(mod.getName());
			}

			cache.put(modid, page);
		}

		return page;
	}
}