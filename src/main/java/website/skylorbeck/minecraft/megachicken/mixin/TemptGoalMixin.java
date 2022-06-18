package website.skylorbeck.minecraft.megachicken.mixin;

import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TemptGoal.class)
public interface TemptGoalMixin {
    //get food item
    @Accessor(value="food")
    Ingredient getItem();
}
