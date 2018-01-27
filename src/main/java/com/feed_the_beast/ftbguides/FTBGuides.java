package com.feed_the_beast.ftbguides;

import com.feed_the_beast.ftblib.FTBLibFinals;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import java.io.FileReader;

@Mod(modid = FTBGuidesFinals.MOD_ID, name = FTBGuidesFinals.MOD_NAME, version = FTBGuidesFinals.VERSION, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.12,)", dependencies = "required-after:" + FTBLibFinals.MOD_ID)
public class FTBGuides
{
	@Mod.Instance(FTBGuidesFinals.MOD_ID)
	public static FTBGuides INST;

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

	public static void main(String[] args) throws Exception
	{
		/*
		Document document = Jsoup.connect("https://raw.githubusercontent.com/LatvianModder/FTBGuidesWeb/master/ftbutilities/chunk_claiming/index.html").get();

		for (Element element : document.body().children())
		{
			switch (element.tagName())
			{
				case "div":
				{
					System.out.println("Panel: " + element.html());
				}
				break;
				case "p":
				{
					System.out.println("Paragraph: " + element.html());
				}
				break;
				case "a":
				{
					System.out.println("Hyperlink: " + element.html());
				}
				break;
				case "br":
				{
					System.out.println("Line Break");
				}
				break;
				case "hr":
				{
					System.out.println("Horizontal Line");
				}
				break;
				case "img":
				{
					System.out.println("Image: " + element.attr("src"));
				}
				break;
				case "ul":
				{
					System.out.println("Unordered List: " + element.children().eachText());
				}
				break;
				case "ol":
				{
					System.out.println("Ordered List: " + element.children().eachText());
				}
				break;
				case "recipe":
				{
					System.out.println("Recipe: " + element.html());
				}
				break;
				case "table":
				{
					System.out.println("Table: " + element.html());
				}
				break;
				default:
					System.out.println("Unknown tag: " + element.tagName());
			}
		}
		*/

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		//HttpURLConnection connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/LatvianModder/FTBGuidesWeb/master/ftbutilities/chunk_claiming/index.html").openConnection();
		//XMLEventReader eventReader = inputFactory.createXMLEventReader(connection.getInputStream());
		XMLEventReader eventReader = inputFactory.createXMLEventReader(new FileReader("K:\\dev\\php\\guides\\ftbutilities\\chunk_claiming\\index.html"));
		eventReader.close();
	}
}