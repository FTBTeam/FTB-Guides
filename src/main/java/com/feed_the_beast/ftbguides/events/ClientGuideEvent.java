package com.feed_the_beast.ftbguides.events;

import com.feed_the_beast.ftbguides.gui.GuidePage;
import com.feed_the_beast.ftbguides.gui.GuideTitlePage;
import com.feed_the_beast.ftbguides.gui.GuideType;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class ClientGuideEvent extends FTBGuidesEvent
{
	private final GuidePage root;
	private final Map<String, GuideTitlePage> map;
	private final GuideType mod;

	public ClientGuideEvent(GuidePage r, Map<String, GuideTitlePage> m, GuideType typeMod)
	{
		root = r;
		map = m;
		mod = typeMod;
	}

	public GuideTitlePage getModPage(String modid)
	{
		GuideTitlePage page = map.get(modid);

		if (page == null)
		{
			page = new GuideTitlePage(modid, root, mod);
			page.isPresent = true;
			ModContainer mod = Loader.instance().getIndexedModList().get(modid);

			if (mod != null)
			{
				page.authors.addAll(mod.getMetadata().authorList);

				if (!mod.getMetadata().description.isEmpty())
				{
					for (String s : mod.getMetadata().description.split("\n"))
					{
						page.println(s);
					}
				}

				page.title = new TextComponentString(mod.getName());
			}

			map.put(modid, page);
		}

		return page;
	}

	public GuideTitlePage getOtherPage(String id)
	{
		GuideTitlePage page = map.get(id);

		if (page == null)
		{
			page = new GuideTitlePage(id, root, GuideType.OTHER);
			page.isPresent = true;
			map.put(id, page);
		}

		return page;
	}
}