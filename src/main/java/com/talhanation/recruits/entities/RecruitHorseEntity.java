package com.talhanation.recruits.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class RecruitHorseEntity extends PathfinderMob {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(Horse.class, EntityDataSerializers.INT);

    public RecruitHorseEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.maxUpStep = 1.25F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setTypeVariant(nbt.getInt("Variant"));

    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    protected void updateControlFlags() {
        super.updateControlFlags();
    }

    public boolean canBeControlledByRider() {
        return !this.isNoAi() && this.getControllingPassenger() instanceof LivingEntity;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        player.sendSystemMessage(Component.literal("This is not your Horse!"));
        return InteractionResult.SUCCESS;
    }


    protected void registerGoals() {
        //this.goalSelector.addGoal(0, new HorseAIRecruitRide(this, 1.6));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance diff, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag nbt) {
        setRandomSpawnBonus();
        setRandomVariant();
        return spawnData;
    }

    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }



    public void setRandomVariant() {
        int variant = this.random.nextInt(7);
        this.setTypeVariant(variant);
    }

    public void setRandomSpawnBonus(){
        getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("heath_bonus", this.random.nextGaussian() * 0.20D, AttributeModifier.Operation.MULTIPLY_BASE));
        getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier("attack_bonus", this.random.nextGaussian() * 0.20D, AttributeModifier.Operation.MULTIPLY_BASE));
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier("knockback_bonus", this.random.nextGaussian() * 0.20D, AttributeModifier.Operation.MULTIPLY_BASE));
        getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier("speed_bonus", this.random.nextGaussian() * 0.20D, AttributeModifier.Operation.MULTIPLY_BASE));

    }

    //ATTRIBUTES
    public static AttributeSupplier setAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .build();

    }

    public void setTypeVariant(int variant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, variant);
    }

    public int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }


    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.HORSE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.HORSE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        super.getHurtSound(p_184601_1_);
        return SoundEvents.HORSE_HURT;
    }

    protected void playStepSound(BlockPos blockpos, BlockState blockState) {
        this.playSound(SoundEvents.HORSE_GALLOP, 0.15F, 1.0F);
    }

    public void travel(Vec3 vec) {

        if (this.isAlive()) {
            if (this.isVehicle() && this.canBeControlledByRider()) {
                AbstractRecruitEntity recruitEntity = (AbstractRecruitEntity) this.getControllingPassenger();
                this.setYRot(recruitEntity.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(recruitEntity.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                if (recruitEntity.getNavigation().isStuck()) this.jumpFromGround();
            } else {
                this.flyingSpeed = 0.02F;
                super.travel(vec);
            }
        }
    }

}
