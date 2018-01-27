package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftbguides.FTBGuidesCommon;
import com.feed_the_beast.ftbguides.gui.Guides;
import com.feed_the_beast.ftblib.FTBLibFinals;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class FTBGuidesClient extends FTBGuidesCommon
{
	public static final KeyBinding KEY_GUIDE = new KeyBinding("key.ftbguides.guide", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_G, FTBLibFinals.KEY_CATEGORY);

	@Override
	public void preInit()
	{
		super.preInit();
		FTBGuidesClientConfig.sync();
		ClientRegistry.registerKeyBinding(KEY_GUIDE);
	}

	@Override
	public void postInit()
	{
		super.postInit();

		if (ClientUtils.MC.getResourceManager() instanceof SimpleReloadableResourceManager)
		{
			((SimpleReloadableResourceManager) ClientUtils.MC.getResourceManager()).registerReloadListener(resourceManager -> Guides.setShouldReload());
		}
	}
}