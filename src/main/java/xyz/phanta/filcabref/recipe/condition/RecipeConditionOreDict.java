package xyz.phanta.filcabref.recipe.condition;

import com.google.gson.JsonObject;
import io.github.phantamanta44.libnine.util.helper.OreDictUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

public class RecipeConditionOreDict implements IConditionFactory {

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        String oreKey = json.get("ore").getAsString();
        return () -> OreDictUtils.exists(oreKey);
    }

}
