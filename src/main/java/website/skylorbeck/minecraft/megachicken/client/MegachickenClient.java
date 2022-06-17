package website.skylorbeck.minecraft.megachicken.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import website.skylorbeck.minecraft.megachicken.Megachicken;
import website.skylorbeck.minecraft.megachicken.entity.MegaChickenRenderer;

@Environment(EnvType.CLIENT)
public class MegachickenClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Megachicken.MEGA_CHICKEN_ENTITY_ENTITY_TYPE, MegaChickenRenderer::new);
    }
}
