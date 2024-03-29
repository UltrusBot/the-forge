package com.sussysyrup.theforge.api.recipe;

import com.sussysyrup.theforge.items.PartItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ForgeToolRecipeRegistry {

    private static HashMap<String, ForgeToolRecipe> recipeStringRegistry = new HashMap<>();
    private static HashMap<String, ForgeToolRecipe> toolTypeRecipeRegistry = new HashMap<>();

    public static ItemStack lookup(List<ItemStack> inventory)
    {
        List<String> keys = new ArrayList<>();

        Item item;

        for(ItemStack stack: inventory)
        {
            item = stack.getItem();

            if(item instanceof PartItem item1)
            {
                keys.add(item1.getPartName());
            }
            else
            {
                keys.add("empty");
            }
        }

        String key = createKey(keys.get(0), keys.get(1), keys.get(2), keys.get(3), keys.get(4));

        if(recipeStringRegistry.containsKey(key)) {
            return recipeStringRegistry.get(key).getTool(inventory);
        }
        else
        {
            return null;
        }
    }

    public static ForgeToolRecipe getRecipeByType(String toolName)
    {
        return toolTypeRecipeRegistry.get(toolName);
    }

    public static String createKey(String handle, String binding, String head, String ex1, String ex2)
    {
        return handle + "," + binding + "," + head + "," + ex1 + "," + ex2;
    }

    public static void register(String toolType, String key, ForgeToolRecipe recipe)
    {
        recipe.setKey(key);
        recipeStringRegistry.put(key, recipe);
        toolTypeRecipeRegistry.put(toolType, recipe);
    }
}
