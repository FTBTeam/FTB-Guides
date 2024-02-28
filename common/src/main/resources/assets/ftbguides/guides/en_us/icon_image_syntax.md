---
title: "Icon Syntax"
category: manual
order: 3
---
# Icon and Image Syntax

Images and icons are loaded via FTB Library, which provides a powerful syntax for referencing assets to render. The guides (markdown files) are able to use this to render items, blocks, fluids or just general resources.

## Icons

Images can be created via the image's resource location, as loaded by Minecraft in a resource pack. The following markdown:

```
![Isles](minecraft:textures/gui/presets/isles.png)
```

will load the 256x256 built-in `isles.png` image:

![Isles](minecraft:textures/gui/presets/isles.png)

If that's a bit big, you can reduce its size:

```
![Isles](minecraft:textures/gui/presets/isles.png){width=64 height=64}
```

![Isles](minecraft:textures/gui/presets/isles.png){width=64 height=64}

You can omit either the width or height, and FTB Guides will try to "do the right thing", sizing the image while preserving aspect ratio.

## Items & Blocks

You can load item and block textures if you know the item or block's registry ID (this is shown in the item tooltip when F3+H advanced tooltip mode is active). To do this, use the `item:` prefix as follows:

```
![cobble](item:minecraft:cobblestone){width=48}
![gold_block](item:minecraft:gold_block){width=48}
```

![diamond](item:minecraft:diamond){width=48}
![gold_block](item:minecraft:gold_block){width=48}

## Other Tricks

FTB Library has a few other tricks for displaying images:

```
![diamond](#404040+item:minecraft:diamond;padding=5;tint=#8080FF;border=#FFFF00){width=48}
```

![diamond](#404040+item:minecraft:diamond;padding=5;tint=#8080FF;border=#FFFF00){width=48}

Here there are actually _two_ images, separated by the `+` character. The first image is a solid block of dark gray (`#404040`), and second image has a yellow border, and padding of 5 pixels between the border and the image. The following property/value pairs are accepted, separated by a ";" character:

* `padding=<int>` - padding in pixels around the image
* `border=<color>` - a color spec; typically you would use a hex color like `#FFFF00` (RRGGBB)
* `tint=<color>` - a hex color spec to tint the image

---

Previous: [Adding Markdown Files](adding_markdown.md)