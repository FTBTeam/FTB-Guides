---
title: "Cross-mod Integration"
order: 6
category: manual
---
# FTB Quests

If **FTB Quests** is installed, it is possible to create links in your Markdown content to directly open the FTB Quests GUI to a specific quest. To do this, create a link in the form `quest:<hex-id>`, where `hex-id` is a 16-character quest ID. You can get this ID by right clicking any quest in the FTB Quests GUI (while in edit mod), and selecting *Copy ID*.

You can also construct links in the usual Markdown fashion, e.g.

    [Open the Quest](quest:31E5CD2A7B9E9B4E)

creates a clickable "Open the Quest" link in your document.

# FTB XMod Compat

**FTB XMod Compat** is required if you want to include clickable recipe links (see [Recipe Syntax](recipe_syntax.md) for more information) in your Markdown. FTB XMod Compat auto-detects which recipe mod you have installed (JEI, REI, or EMI are supported), and directs clicks on recipe links to open the recipe viewer in the appropriate mod.
