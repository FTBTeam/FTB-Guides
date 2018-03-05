package com.feed_the_beast.ftbguides.cmd;

import com.feed_the_beast.ftbguides.client.FTBGuidesClient;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdOpenGuides extends CmdBase
{
	public CmdOpenGuides()
	{
		super("open_guides", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			FTBGuidesClient.pageToOpen = args[0];
		}

		FTBGuidesClient.openGuidesGui();
	}
}