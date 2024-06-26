---
title: "Viewing Markdown Files"
order: 2
category: manual
---
# Viewing Markdown Files

There are a couple of ways to open the FTB Guides GUI to view the markdown files that the mod knows about:

## Commands

The command:

```
/ftbguides open
```

opens the viewer to the default page. The default page is defined in the [client-side config file](config.md).

The command:

```
/ftbguides open <page-id>
```

opens the viewer to the given page ID. The page ID needs to be known in advance, so this is generally of use for opening the viewer from a modpack-defined object, e.g. an FTB Quests command reward, or perhaps a [Command Block](https://minecraft.wiki/w/Command_Block).

### Anchors

It is also possible to include an _anchor_ with the page ID, using the `#` separator. Anchors are automatically generated from the heading elements found in the markdown document, and are generated by lowercasing the heading and replacing space with dash `-` symbols.

For example, to open the viewer to the **Viewing Markdown Files** heading in this document, you would use:

```
/ftbguides open ftbguides:viewing_files#viewing-markdown-files
```

## Items

Any item with the top-level NBT string tag `ftbguides:page` will open the viewer to the value of that tag when right-clicked.

### Mod Developers

You are completely free to create your own guide book items, setting up the appropriate NBT when constructing your itemstack for your creative tab.

### Modpack Developers

As a convenience, FTB Guides also provides a guidebook item (with no NBT by default), which pack developers can use (e.g. by adding recipes for, or using mods like KubeJS to create instances). This guidebook item dynamically colors itself based on the value of the `ftbguides:page` NBT tag (specifically, the _namespace_ of the page ID referred to).

