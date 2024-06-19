package dev.ftb.mods.ftbguides.registry;

import dev.ftb.mods.ftbguides.FTBGuides;
import net.minecraft.world.item.Item;

public class GuideBookItem extends Item {
    public GuideBookItem() {
        super(new Properties().stacksTo(1).tab(FTBGuides.ITEM_GROUP));
    }

}
