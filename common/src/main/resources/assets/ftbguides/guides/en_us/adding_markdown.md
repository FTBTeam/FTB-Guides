---
title: "Adding Markdown"
category: manual
order: 2
---
# Adding Markdown Files

Markdown is a very popular format for easy creation of web documents. Suggested reading: [Markdown Guide](https://www.markdownguide.org/)

You can add markdown (.md) files anywhere under your language directory. They will most commonly go at the same folder level as the [guide.json](getting_started) file, but they can be placed into subfolders if you want. This has no effect on in-game rendering (categories are used for that); it's just a way of organising files.

Each markdown file is required to have a metadata (aka "front matter") block at the top of the file. This is an SNBT compound tag that must at the very least contain a `title` key (which is displayed in the GUI index). The optional `category` key corresponds to the category `id` field in the `guide.json` file described above. This section must start and end with lines containing 3 dashes (`---`) and nothing else.

Note that although SNBT normally requires the compound tag to be enclosed in curly braces, they're optional here, for convenience.

### Example metadata block

```markdown
---
category: "cat1",  // Optional - defaults to "default"
title: "Test 1",  // Required
order: 1, // Optional - if omitted page is added at end of list
icon: "item:minecraft:book" // Optional
---

# Welcome to my guide book!

The rest of the markdown...
```

## Features and Limitations

### Markdown content

Under the hood, FTB Guides uses [CommonMark](https://spec.commonmark.org/0.31.2/#introduction) for markdown parsing. This means you can use nearly all the features that CommonMark provides, with a couple of exceptions:

#### Images

Images may not be remote URLs; they must be local to the game's resources, i.e. visible to Minecraft via a resource pack. To display an image, use its [resource location](https://minecraft.wiki/w/Resource_location) as understood by Minecraft.
  - This means you can't use `![My Image](https://example.com/image.png)`
  - You can however use `![My Image](item:minecraft:diamond)` or `![My Image](minecraft:gui/painting/burning_skull)`
  - Custom images can be added in your resource pack, if you wish

#### Tables

Tables are an extension to the Markdown syntax which are not currently supported; however this is a possible future feature.

--- 

Previous: [Getting Started](getting_started.md)

Next: [Icon Syntax](icon_image_syntax.md) section for more information on how to use icons.
