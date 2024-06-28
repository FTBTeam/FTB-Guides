package dev.ftb.mods.ftbguides.docs;

import net.minecraft.resources.ResourceLocation;
import org.commonmark.node.CustomBlock;

public class RecipeNode extends CustomBlock {
    private ResourceLocation recipeID;

    public RecipeNode(ResourceLocation recipeId) {
    }

    public ResourceLocation getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(ResourceLocation recipeID) {
        this.recipeID = recipeID;
    }

    @Override
    protected String toStringAttributes() {
        return "recipe_id=" + recipeID;
    }
}
