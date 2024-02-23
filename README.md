# FTB Guides 3 [![](http://cf.way2muchnoise.eu/317586.svg) ![](https://cf.way2muchnoise.eu/packs/ftb-guides-2.svg) ![](http://cf.way2muchnoise.eu/versions/317586.svg)](https://www.curseforge.com/minecraft/mc-mods/ftb-guides-2)

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

- A folder in your resource pack called `assets/ftb_guides/guides/{language}` by default we recommend using `en_us` as this is what our system will fallback to if the users language is not available.
- Inside of this folder, you'll want to have `//TODO: Add info about how to setup the index structure`
- Once you've got your index setup, you can start adding guides! It's as simple as create a new markdown file (`my-guide.md`)

### Markdown metadata

Each markdown file is required to have a metadata block at the top of the file. This is an SNBT object that must at the very least contain a `title` and a `category` key.

#### Example metadata block

```markdown
---
{
  category: "cat1",
  title: "Test 1",
  "order": 1, // Optional
  "icon": "item:minecraft:book" // Optional
}
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

Under the hood FTB Guides uses [CommonMark](https://spec.commonmark.org/0.31.2/#introduction) for markdown parsing. This means you can use all the features that CommonMark provides. With a couple of exceptions:

#### Images

- Images can not be remote URLs, they must be local to the games resources
  - This means you can't use `![My Image](https://example.com/image.png)`
  - You can use `![My Image](item:minecraft:diamond)`

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
