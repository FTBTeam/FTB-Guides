package com.feed_the_beast.ftbguides;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.OtherMods;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
		modid = FTBGuides.MOD_ID,
		name = FTBGuides.MOD_NAME,
		version = FTBGuides.VERSION,
		acceptableRemoteVersions = "*",
		dependencies = FTBLib.THIS_DEP + ";after:" + OtherMods.TINKERS_CONSTRUCT
)
public class FTBGuides
{
	public static final String MOD_ID = "ftbguides";
	public static final String MOD_NAME = "FTB Guides";
	public static final String VERSION = "0.0.0.ftbguides";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	@SidedProxy(serverSide = "com.feed_the_beast.ftbguides.FTBGuidesCommon", clientSide = "com.feed_the_beast.ftbguides.client.FTBGuidesClient")
	public static FTBGuidesCommon PROXY;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		PROXY.preInit();
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		PROXY.postInit();
	}
}