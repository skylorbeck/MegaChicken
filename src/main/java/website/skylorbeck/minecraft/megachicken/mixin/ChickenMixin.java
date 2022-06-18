package website.skylorbeck.minecraft.megachicken.mixin;

import net.minecraft.entity.passive.ChickenEntity;
import org.spongepowered.asm.mixin.Mixin;
import website.skylorbeck.minecraft.megachicken.Megachicken;
import website.skylorbeck.minecraft.megachicken.entity.MegaChickenEntity;
import website.skylorbeck.minecraft.skylorlib.IMegable;

@Mixin(ChickenEntity.class)
public class ChickenMixin implements IMegable {
    @Override
    public void Megafy() {
        ChickenEntity chicken = (ChickenEntity) (Object)(this);
        MegaChickenEntity megaChicken = Megachicken.MEGA_CHICKEN_ENTITY_ENTITY_TYPE.create(chicken.world);
        assert megaChicken != null;
        megaChicken.setPos(chicken.getX(), chicken.getY()+1, chicken.getZ());
        megaChicken.setVariant(chicken.getRandom().nextInt(3));
        if (chicken.getCustomName() != null) {
            megaChicken.setCustomName(chicken.getCustomName());
        }
        chicken.world.spawnEntity(megaChicken);
        chicken.discard();
    }
}
