package website.skylorbeck.minecraft.megachicken.entity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import website.skylorbeck.minecraft.megachicken.Megachicken;

public class MegaChickenModel extends AnimatedGeoModel<MegaChickenEntity> {

    @Override
    public Identifier getModelResource(MegaChickenEntity object) {
        return Megachicken.getId("geo/mega_chicken.geo.json");
    }

    @Override
    public Identifier getTextureResource(MegaChickenEntity object) {
        /*if (object.getCustomName() != null) {
            String name = object.getName().getString();
            if (name.equalsIgnoreCase("mordecai")){
                return Declarar.getMegaChickenId("textures/entity/mordecai.png");
            } else

        }*/
        switch (object.getVariant()) {
            default -> {
                return Megachicken.getId("textures/entity/blackshaded.png");
            }
            case 1 -> {
                return Megachicken.getId("textures/entity/brownshaded.png");
            }
            case 2 -> {
                return Megachicken.getId("textures/entity/whiteshaded.png");
            }
        }
    }

    @Override
    public Identifier getAnimationResource(MegaChickenEntity animatable) {
        return Megachicken.getId("animations/model.animation.json");
    }

    @SuppressWarnings({ "unchecked"})
    @Override
    public void setLivingAnimations(MegaChickenEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }

    @Override
    public IBone getBone(String boneName) {
        return super.getBone(boneName);
    }
}
