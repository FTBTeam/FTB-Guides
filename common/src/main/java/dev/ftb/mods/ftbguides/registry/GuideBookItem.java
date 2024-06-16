package dev.ftb.mods.ftbguides.registry;

import dev.ftb.mods.ftbguides.FTBGuides;
import net.minecraft.world.item.Item;

public class GuideBookItem extends Item {
    public GuideBookItem() {
        super(new Properties().stacksTo(1).arch$tab(ModItems.ITEM_GROUP.get())); // I don't like this arch!
    }

}
