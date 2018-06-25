package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.gui.components.TableGuideComponent;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author LatvianModder
 */
public class GuiConverter
{
	public static void main(String[] args)
	{
		new GuiConverter();
	}

	private int filesFixed = 0;
	private int currentList = -1;
	private int currentLine = 0;

	private GuiConverter()
	{
		File folder = null;

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			folder = fc.getSelectedFile();
		}

		if (folder == null || !folder.exists())
		{
			FTBGuides.LOGGER.error("Directory not found!");
		}
		else
		{
			try
			{
				scanAndFix(folder, 0);
				FTBGuides.LOGGER.info("Fixed " + filesFixed + " files!");
			}
			catch (Exception ex)
			{
				FTBGuides.LOGGER.info("Error while fixing files!" + ex);
				ex.printStackTrace();
			}
		}

		System.exit(0);
	}

	private void scanAndFix(File file, int level) throws Exception
	{
		if (level == 1 && file.getName().equals("data.json"))
		{
			return;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < level; i++)
		{
			sb.append('\t');
		}

		sb.append(" - ");
		sb.append(file.getAbsolutePath());
		FTBGuides.LOGGER.info(sb.toString());

		if (file.isFile() && file.getName().endsWith(".json"))
		{
			JsonElement json = DataReader.get(file).safeJson();

			if (json.isJsonArray())
			{
				try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(FileUtils.newFile(new File(file.getParentFile(), file.getName().equals("index.json") ? "README.md" : file.getName().replace(".json", ".md")))), StandardCharsets.UTF_8);
					 BufferedWriter br = new BufferedWriter(fw))
				{
					currentLine = 0;

					for (JsonElement json1 : json.getAsJsonArray())
					{
						write(false, json1, br);
						br.write('\n');
						currentLine++;
					}

					filesFixed++;
				}
			}
		}
		else if (file.isDirectory())
		{
			File[] files = file.listFiles();

			if (files != null && files.length > 0)
			{
				for (File f : files)
				{
					scanAndFix(f, level + 1);
				}
			}
		}
	}

	private void write(boolean inline, JsonElement json, BufferedWriter writer) throws Exception
	{
		boolean needsNewLine = currentLine > 0 && !inline;

		if (json.isJsonPrimitive())
		{
			if (needsNewLine)
			{
				writer.write('\n');
			}

			writer.write(json.getAsString());
			return;
		}
		else if (json.isJsonArray())
		{
			for (JsonElement json1 : json.getAsJsonArray())
			{
				write(true, json1, writer);
			}

			return;
		}
		else if (!json.isJsonObject())
		{
			return;
		}

		JsonObject o = json.getAsJsonObject();

		for (int i = 1; i <= 3; i++)
		{
			if (o.has("h" + i))
			{
				if (needsNewLine)
				{
					writer.write('\n');
				}

				for (int j = 0; j < i; j++)
				{
					writer.write('#');
				}

				writer.write(' ');
				write(true, o.get("h" + i), writer);
				return;
			}
		}

		if (o.has("hr"))
		{
			if (needsNewLine)
			{
				writer.write('\n');
			}

			writer.write("---\n");
		}
		else if (o.has("img"))
		{
			if (needsNewLine)
			{
				writer.write('\n');
			}

			String click = o.has("click") ? o.get("click").getAsString() : "";

			if (!click.isEmpty())
			{
				writer.write('[');
			}

			writer.write('!');
			writer.write('[');

			if (o.has("hover"))
			{
				writer.write(o.get("hover").getAsString());
			}

			writer.write(']');
			writer.write('(');
			writer.write(o.get("img").getAsString());
			writer.write(")");

			if (!click.isEmpty())
			{
				writer.write(']');
				writer.write('(');
				writer.write(click);
				writer.write(')');
			}
		}
		else if (o.has("list"))
		{
			currentList++;

			if (needsNewLine)
			{
				writer.write('\n');
			}

			boolean first = true;

			for (JsonElement json1 : o.get("list").getAsJsonArray())
			{
				if (first)
				{
					first = false;
				}
				else
				{
					writer.write('\n');
				}

				for (int i = 0; i < currentList; i++)
				{
					writer.write('\t');
				}

				writer.write('*');
				writer.write(' ');
				write(true, json1, writer);
			}

			if (currentList == 0)
			{
				writer.write('\n');
			}

			currentList--;
		}
		else if (o.has("table"))
		{
			if (needsNewLine)
			{
				writer.write('\n');
			}

			JsonArray table = o.get("table").getAsJsonArray();
			int cols = o.has("head") ? o.get("head").getAsJsonArray().size() : 0;

			for (JsonElement e : table)
			{
				if (e.isJsonArray())
				{
					cols = Math.max(cols, e.getAsJsonArray().size());
				}
			}

			String[] head = new String[cols];
			TableGuideComponent.Align[] align = new TableGuideComponent.Align[cols];
			Arrays.fill(align, TableGuideComponent.Align.NONE);

			if (o.has("head"))
			{
				JsonArray heade = o.get("head").getAsJsonArray();

				for (int i = 0; i < Math.min(heade.size(), cols); i++)
				{
					JsonElement e = heade.get(i);

					if (e.isJsonPrimitive())
					{
						head[i] = e.getAsString();
					}
					else if (e.isJsonObject())
					{
						JsonObject o1 = e.getAsJsonObject();

						if (o1.has("text"))
						{
							head[i] = o1.get("text").getAsString();
						}

						if (o1.has("style"))
						{
							JsonObject o2 = o1.get("style").getAsJsonObject();
						}
					}
				}
			}

			for (int i = 0; i < cols; i++)
			{
				if (head[i] == null)
				{
					head[i] = "";
				}
			}

			if (currentLine > 0)
			{
				writer.write('\n');
			}

			writer.write('|');

			for (String h : head)
			{
				writer.write(' ');
				writer.write(h);
				writer.write(' ');
				writer.write('|');
			}

			writer.write('\n');
			writer.write('|');

			for (int i = 0; i < cols; i++)
			{
				writer.write(align[i].md);
			}

			writer.write('\n');

			for (JsonElement e : table)
			{
				if (e.isJsonArray())
				{
					writer.write('|');

					for (JsonElement e1 : e.getAsJsonArray())
					{
						writer.write(' ');
						write(true, e1, writer);
						writer.write(' ');
						writer.write('|');
					}

					writer.write('\n');
				}
			}
		}
		else if (o.has("text"))
		{
			if (needsNewLine)
			{
				writer.write('\n');
			}

			String text = o.get("text").getAsString();

			if (o.has("code") && o.get("code").getAsBoolean())
			{
				text = "`" + text + "`";
			}
			else
			{
				if (o.has("bold") && o.get("bold").getAsBoolean())
				{
					text = "**" + text + "**";
				}
				if (o.has("italic") && o.get("italic").getAsBoolean())
				{
					text = "*" + text + "*";
				}
				if (o.has("strikethrough") && o.get("strikethrough").getAsBoolean())
				{
					text = "~~" + text + "~~";
				}
			}

			if (o.has("click"))
			{
				text = "[" + text + "](" + o.get("click").getAsString() + ")";
			}

			writer.write(text);
		}
		else if (o.has("code"))
		{
			if (needsNewLine)
			{
				writer.write('\n');
			}

			writer.write('`');
			write(true, o.get("code"), writer);
			writer.write('`');
		}
		else if (o.has("codeblock"))
		{
			if (needsNewLine)
			{
				writer.write('\n');
			}

			writer.write("```");
			//language
			writer.write('\n');

			for (JsonElement json1 : o.get("codeblock").getAsJsonArray())
			{
				writer.write(json1.getAsString());
				writer.write('\n');
			}

			writer.write("```\n");
		}
	}
}