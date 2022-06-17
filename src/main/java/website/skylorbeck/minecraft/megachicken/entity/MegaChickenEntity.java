package website.skylorbeck.minecraft.megachicken.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Arrays;
import java.util.UUID;

public class MegaChickenEntity extends ChickenEntity implements IAnimatable {
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.BEETROOT_SEEDS, Items.PUMPKIN_SEEDS, Items.APPLE, Items.CARROT, Items.BEETROOT, Items.POTATO, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE});
    private final AnimationFactory factory = new AnimationFactory(this);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(MegaChickenEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public MegaChickenEntity(EntityType<? extends ChickenEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createMegaChickenAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(nbt.getInt("Variant"));
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.hasPassengers()) {
            return super.interactMob(player, hand);
        }
        if (this.isBreedingItem(itemStack)) {
            return this.interactBird(player, itemStack);
        }
        ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
        if (actionResult.isAccepted()) {
            return actionResult;
        }
        return ActionResult.success(this.world.isClient);
    }

    public ActionResult interactBird(PlayerEntity player, ItemStack stack) {
        boolean bl = this.receiveFood(player, stack);
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        if (this.world.isClient) {
            return ActionResult.CONSUME;
        }
        return bl ? ActionResult.SUCCESS : ActionResult.PASS;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.ofItems(Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE), false));
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        BlockPos blockPos = this.getBlockPos();
        Biome biome = world.getBiome(blockPos).value();
        this.setVariant(biome.isCold(blockPos) ? 5 : biome.isHot(blockPos) ? 6 : this.random.nextInt(5));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0, 0.5f * this.getStandingEyeHeight(), this.getWidth() * 0.2f);
    }

    public double getMountedHeightOffset() {
        return this.getDimensions(this.getPose()).height * 0.4;
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        super.updatePassengerPosition(passenger);
        if (passenger instanceof MobEntity mobEntity) {
            this.bodyYaw = mobEntity.bodyYaw;
        }
        float mobEntity = MathHelper.sin(this.bodyYaw * ((float) Math.PI / 180));
        float f = MathHelper.cos(this.bodyYaw * ((float) Math.PI / 180));
        float g = 0.5f;
        passenger.setPosition(this.getX() + (double) (g * mobEntity), this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset(), this.getZ() - (double) (g * f));
        if (passenger instanceof LivingEntity) {
            ((LivingEntity) passenger).bodyYaw = this.bodyYaw;
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (fallDistance > 1.0f) {
            this.playSound(SoundEvents.ENTITY_RAVAGER_STEP, 0.4f, 1.0f);
        }
        return false;
    }

    protected boolean receiveFood(PlayerEntity player, ItemStack item) {
        boolean bl = false;
        float health = 0.0f;
        if (BREEDING_INGREDIENT.test(item)) {
            health = 2f;
        } else if (item.isOf(Items.GOLDEN_CARROT)) {
            health = 4.0f;
        } else if (item.isOf(Items.GOLDEN_APPLE) || item.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
            health = 10.0f;
        }
        if (this.getHealth() < this.getMaxHealth() && health > 0.0f) {
            this.heal(health);
            bl = true;
        }
        if (bl) {
            this.emitGameEvent(GameEvent.EAT, this);
        }
        return bl;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_RAVAGER_STEP, 0.4f, 1.0f);
    }

    public float getSoundPitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f - 1.0f;
    }


    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.ENTITY_CHICKEN_HURT;
    }


    //gecko
    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "master_controller", 5, this::locomotion_predicate));
    }

    private <E extends IAnimatable> PlayState locomotion_predicate(AnimationEvent<E> event) {
        MegaChickenEntity megaChicken = (MegaChickenEntity) event.getAnimatable();

//        if (event.isMoving()) {
        Vec3d vec3d = megaChicken.getVelocity().normalize();
        if (vec3d.x > 0.05f || vec3d.x < -0.05f || vec3d.z > 0.05f || vec3d.z < -0.05f)
            event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", true));
        else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", true));
        //todo add animation for flying
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

}