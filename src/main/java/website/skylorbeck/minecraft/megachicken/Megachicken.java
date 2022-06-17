package website.skylorbeck.minecraft.megachicken;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.GeckoLib;
import website.skylorbeck.minecraft.megachicken.entity.MegaChickenEntity;

public class Megachicken implements ModInitializer {
    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        FabricDefaultAttributeRegistry.register(MEGA_CHICKEN_ENTITY_ENTITY_TYPE, MegaChickenEntity.createMegaChickenAttributes());
    }

    public static Identifier getId(String path) {
        return new Identifier("megachicken", path);
    }

    public static final EntityType<MegaChickenEntity> MEGA_CHICKEN_ENTITY_ENTITY_TYPE = Registry.register(Registry.ENTITY_TYPE, getId("mega_chicken"),
            EntityType.Builder.create(MegaChickenEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(1.75f, 2.25f)
                    .maxTrackingRange(10)
                    .build(getId("mega_chicken").toString()));
}
