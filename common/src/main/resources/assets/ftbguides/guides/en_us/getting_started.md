---
title: "Getting Started"
category: manual
order: 1
---
# Getting Started

Creating a guide book in FTB Guides consists of the following steps:

- Create a [resource pack](https://minecraft.wiki/w/Resource_pack). You can also use mods like [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs) to assist with this process.
- Create a folder in your resource pack called `assets/{namespace}/guides/{language}`.
  - You can create multiple language subfolders, but we recommend creating at least a `en_us` language folder, as this is what our system will fall back to if the user's language is not available.
  - You can use almost any namespace you want, although it's recommended to use your mod ID, or something closely related. Please don't use the `ftbguides` namespace, which is the namespace used by the guide you're reading now (assuming you're in-game and not on a web browser!)
- Inside this folder, create a guide index file: `guide.json`. See below for an example.
- Once you've got your index setup, you can start adding guides! It's as simple as creating new markdown files (`my-guide.md`)

## Guide.json file

The `guide.json` file acts as the top-level category index, and can also be used to provide some basic color theming for your guide.

Here is a sample `guide.json` file. It has a mandatory `categories` section, and an optional `theme` section:

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

The `categories` section must be provided, and must list every category in your guide, in the order you want them to appear in the GUI index. Each entry has a mandatory `id` and `name` field, and an optional `icon` field. The `id` field is used internally to connect page files to a category, and the `name` and `icon` fields are used for display purposes.

It's important to understand that each `guide.json` is specific to the namespace it's in, and is used to display the index when viewing a Markdown file in the same namespace. This also means that each namespace has its own index, and potentially its own colour scheme.

---

Next: [Adding Markdown Files](adding_markdown.md)
