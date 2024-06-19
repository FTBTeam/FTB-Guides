---
title: "Adding Markdown"
category: manual
order: 3
---
# Adding Markdown Files

Markdown is a very popular format for easy creation of web documents. Suggested reading: [Markdown Guide](https://www.markdownguide.org/)

You can add markdown (.md) files anywhere under your language directory. They will most commonly go at the same folder level as the [guide.json](getting_started) file, but they can be placed into subfolders if you want. This has no effect on in-game rendering (categories are used for that); it's just a way of organising files.

## Metadata

Each markdown file is required to have a metadata (aka "front matter") block at the top of the file.  This section must start and end with lines containing 3 dashes (`---`) and nothing else, and contains one or more key/value pairs (at the minimum, a `title` field).

* The `title` field is mandatory, and is the text displayed in the index panel.
* The optional `category` field corresponds to the category `id` field in the `guide.json` file described in [Getting Started](getting_started.md).
* The optional `order` field controls the order this page will appear in the index panel relative to other documents; if omitted, it will appear after documents which do have an order. Documents are sorted by their `order` field and then their `title` field for index display purposes.
* The optional `icon` field specifies an icon to be displayed alongside the title text in the index panel.
* The optional `tag` field specifies a list of string _tags_ which are used for searching. In addition, the tags are displayed as clickable links at the top of the document; clicking a tag searches for other documents containing the same tag. Tag must be alphanumeric _only_.
* The optional `hidden_tag` field acts just like the `tag` field, except that hidden tags are not displayed at the top of the document.

Note that although SNBT normally requires the compound tag to be enclosed in curly braces, they're optional here, for convenience.

### Example metadata block

```markdown
---
category: "cat1",  // Optional - defaults to "default"
title: "Test 1",  // Required
order: 1, // Optional - if omitted page is added at end of list
icon: "item:minecraft:book" // Optional
tag: ["tag1", "tag2"] // Optional
hidden_tag: ["tag3", "tag4"] // Optional
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

Inline images are not currently supported.

#### Tables

Tables are an extension to the Markdown syntax which are not currently supported; however this is a possible future feature.

