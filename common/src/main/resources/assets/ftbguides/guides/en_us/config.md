---
title: Configuration
order: 5
category: manual
---
# Configuration

FTB Guides has a small client-side config file, where a few preferences can be defined. The file can be found in `<instance-dir>/local/ftbguides-client.snbt`.

Config settings:

* `home` - a string value storing the page ID of the default page opened by the `/ftbguides open` command. 
    Default: `ftbguides:index`.
* `pinned` - whether the index side panel in the viewer GUI should stay open. This is automatically updated when the pin button (top-left) is clicked.
    Default: true.
* `search_this_guide_only` - if true, search results will only include pages in the same guide namespace as the current page; if false, results will include pages from *all* known guide namespaces. This is automatically updated when the corresponding button in the search popup panel is clicked.
    Default: true.

