package com.feed_the_beast.mods.ftbguides;

import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class GuideReloadListener implements ISelectiveResourceReloadListener
{
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
	{
		if (resourcePredicate.test(VanillaResourceType.LANGUAGES))
		{
			FTBGuides.setShouldReload();
		}
	}
}