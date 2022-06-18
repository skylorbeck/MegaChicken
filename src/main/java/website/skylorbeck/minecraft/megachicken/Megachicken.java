package website.skylorbeck.minecraft.megachicken;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.OnAStickItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.GeckoLib;
import website.skylorbeck.minecraft.megachicken.entity.MegaChickenEntity;
import website.skylorbeck.minecraft.skylorlib.Registrar;

public class Megachicken implements ModInitializer {
    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        FabricDefaultAttributeRegistry.register(MEGA_CHICKEN_ENTITY_ENTITY_TYPE, MegaChickenEntity.createMegaChickenAttributes());
        Registrar.regItem("cake_on_a_stick_",CAKE_ON_A_STICK,"megachicken");
    }

    public static Identifier getId(String path) {
        return new Identifier("megachicken", path);
    }

    public static final EntityType<MegaChickenEntity> MEGA_CHICKEN_ENTITY_ENTITY_TYPE = Registry.register(Registry.ENTITY_TYPE, getId("mega_chicken"),
            EntityType.Builder.create(MegaChickenEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(1.75f, 2.25f)
                    .maxTrackingRange(10)
                    .build(getId("mega_chicken").toString()));
    public static final Item CAKE_ON_A_STICK = new OnAStickItem<MegaChickenEntity>(new FabricItemSettings().rarity(Rarity.EPIC).group(ItemGroup.TRANSPORTATION).maxCount(1).maxDamage(70),MEGA_CHICKEN_ENTITY_ENTITY_TYPE,7);
}
