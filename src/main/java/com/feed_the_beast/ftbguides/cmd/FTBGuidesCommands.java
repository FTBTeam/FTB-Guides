package com.feed_the_beast.ftbguides.cmd;

import com.feed_the_beast.ftblib.events.RegisterFTBClientCommandsEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@EventHandler(Side.CLIENT)
public class FTBGuidesCommands
{
	@SubscribeEvent
	public static void registerClientCommands(RegisterFTBClientCommandsEvent event)
	{
		event.add(new CmdOpenGuide());
	}
}