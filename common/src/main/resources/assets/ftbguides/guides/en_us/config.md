---
title: Configuration
order: 7
category: manual
---
# Configuration

FTB Guides has a small client-side config file, where a few preferences can be defined. The file can be found in `<instance-dir>/local/ftbguides-client.snbt`.

Config settings:

* `home` - a string value storing the page ID of the default page opened by the `/ftbguides open` command. 
    Default: `ftbguides:index`.
* `custom_gui_scale` - a numeric value determining the GUI scale used while the FTB Guides GUI is open. This is clamped to a limit based on the current screen resolution of your Minecraft client. The value has the same meaning as the vanilla GUI scale option (see Options -> Video Settings -> GUI Scale).
    Default: 0, meaning to follow the default Minecraft GUI scale.
* `pinned` - whether the index side panel in the viewer GUI should stay open. This is automatically updated when the pin button (top-left) is clicked.
    Default: true.
* `search_this_guide_only` - if true, search results will only include pages in the same guide namespace as the current page; if false, results will include pages from *all* known guide namespaces. This is automatically updated when the corresponding button in the search popup panel is clicked.
    Default: true.

