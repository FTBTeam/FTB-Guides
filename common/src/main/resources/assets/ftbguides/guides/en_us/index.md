---
title: "FTB Guides 3"
---
# FTB Guides 3 

FTB Guides 3 is your very own in-game guidebook framework for creating in-game guides, support docs, instructions, to-lists, etc. With rich in-built markdown support ([CommonMark](https://spec.commonmark.org/0.31.2/#introduction)), categories with nesting, Minecraft item rendering, resource pack based, multi-language supported and more, it's a great choice for your next modpack!

## Features

- Full markdown support ([CommonMark](https://spec.commonmark.org/0.31.2/#introduction))
- Categories with nesting
- Minecraft item rendering
- Language support
- In-game GUI
- Searching
- Tagging
- Built-in JEI/REI/EMI support (if installed) to display recipes within a guide

## Usage

To get started using FTB Guides, you'll want to have it installed and have a resource-pack or a mod that can dynamically load resource packs from the file system.

Once you've got this sorted, you'll need the following:

- A folder in your resource pack called `assets/{namespace}/guides/{language}`. By default, we recommend using `en_us` as this is what our system will fall back to if the user's language is not available. You can use almost any namespace you want, although it's recommended to use your mod ID, or something closely related. Please don't use the `ftbguides` namespace.
- Inside this folder, create a guide index file: `guide.json`. See below for an example.
- Once you've got your index setup, you can start adding guides! It's as simple as create a new markdown file (`my-guide.md`)

This is a sample `guide.json` file. It has a mandatory `categories` section, and an optional `theme` section, which can be used to provide colour for your guide pages.

```json
{
  "categories": [
    { "id":  "default", "name":  "Sample Guide", "icon": "item:minecraft:stone" },
    { "id":  "category1", "name":  "Category One", "icon": "ftblibrary:icons/heart" },
    { "id":  "category2", "name":  "Category Two" }
  ],
  "theme": {
    "background_color": "#80000000",
    "index_background_color": "#212121",
    "gui_line_color": "#606060",
    "text_color": "#FFFFFF",
    "links_color": "#98D9FF",
    "code_color": "#EBCB8B"
  }
}
```

The colours provided above are the defaults, and can be omitted if you're happy to use those.

The `categories` section must be provided, and lists each category in your guide, in the order you want it to appear in the GUI index. Each entry has a mandatory `id` and `name` field, and an optional `icon` field.

### Markdown Files

You can add markdown files anywhere within your namespace. They will most commonly go at the same folder level as the `guide.json` file, but they can be placed into subfolders if you want. This has no effect on in-game rendering (categories are used for that); it's just a way of organising files.

Each markdown file is required to have a metadata (aka "front matter") block at the top of the file. This is an SNBT compound tag that must at the very least contain a `title` key (which is displayed in the GUI index). The optional `category` key corresponds to the category `id` field in the `guide.json` file described above.

Note that although SNBT normally requires the compound tag to be enclosed in curly braces, they're optional here, for convenience.

#### Example metadata block

```markdown
---
category: "cat1",  // Optional - defaults to "default"
title: "Test 1",  // Required
order: 1, // Optional - if omitted page is added at end of list
icon: "item:minecraft:book" // Optional
---

# Welcome to my guide book!
```

#### Available metadata keys

| Key | Type | Description |
| --- | --- | --- |
| `category` | String | The category that this guide belongs to. |
| `title` | String | The title of the guide. |
| `order` | Integer | The order of the guide within the category. |
| `icon` | String | The icon to use for the guide. |

### Markdown content

Under the hood FTB Guides uses [CommonMark](https://spec.commonmark.org/0.31.2/#introduction) for markdown parsing. This means you can use all the features that CommonMark provides, with a couple of exceptions:

#### Images

- Images can not be remote URLs; they must be local to the game's resources
  - This means you can't use `![My Image](https://example.com/image.png)`
  - You can use `![My Image](item:minecraft:diamond)` or `![My Image](minecraft:gui/painting/burning_skull)`

Please see the [FTB Library icon path](#ftb-library-icon-path) section for more information on how to use icons.

### FTB Library icon path

Our library provides a powerful syntax for referencing assets to render. The guides (markdown files) are able to use this to render items, blocks, fluids or just general resources.

`// TODO: Link out or describe the syntax`

#### Icons

#### Items / Blocks / Fluids

#### Resources

### Categories

Categories are a way to group guides together. They can be nested to create a hierarchy of guides. You can define nested categories by using a `.` to separate the category names.

#### Example category structure

**Standard**
```markdown
---
{
  category: "cat1",
  title: "Test 1",
  "order": 1, // Optional
  "icon": "item:minecraft:book" // Optional
}
---
```

**Nested**
```markdown
---
{
  category: "cat1.test1",
  title: "Test 1A",
  "order": 1, // Optional
  "icon": "item:minecraft:book" // Optional
}
---
```

### Reloading

We support reloading via the `F3 - T` resource reloading hotkey. Please note `/reload` will not work as this is not a client side reload. You will **not** have to restart the game to see changes to your guides.

## License

FTB Guides is licensed under All Rights Reserved. You are not allowed to use FTB Guides in any modpack, public or private, without explicit permission from the author. You are not allowed to use FTB Guides in any other way without explicit permission from the author.

You **CAN** use FTB Guides in any modpack without explicit permission from FTB.

## Reporting bugs / requesting features

If you find a bug or have a feature request, please report it on the [issue tracker](https://go.ftb.team/support-mod-issues)

## Contributing

Due to our license, all contributions must sign a CLA. Upon creating a pull request, you will be prompted to sign the CLA. If you do not sign the CLA, your pull request will be closed within due time. 

Our CLA is available [here](https://go.ftb.team/doc-mod-dev-cla).

## Support

If you need help with FTB Guides, please use our [support system](https://go.ftb.team/support-mod-issues). You could always join our [Discord](https://go.ftb.team/discord) and ask for help there but please note this is a community server and is not intended for direct support. 
