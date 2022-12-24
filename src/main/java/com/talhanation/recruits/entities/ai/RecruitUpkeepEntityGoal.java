package com.talhanation.recruits.entities.ai;

import com.talhanation.recruits.entities.AbstractRecruitEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RecruitUpkeepEntityGoal extends Goal {
    public AbstractRecruitEntity recruit;
    public Optional<Entity> entity;
    public Container container;

    public RecruitUpkeepEntityGoal(AbstractRecruitEntity recruit) {
        this.recruit = recruit;
    }

    @Override
    public boolean canUse() {
        return recruit.needsToEat();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    private boolean hasFoodInInv(){
        return recruit.getInventory().items
                .stream()
                .anyMatch(ItemStack::isEdible);
    }

    @Override
    public void tick() {
        super.tick();
        this.entity = findEntityPos();
        //Main.LOGGER.debug("searching upkeep entity");
        if (entity.isPresent() && !this.hasFoodInInv()) {

            if(entity.get() instanceof AbstractHorse horse){
                this.container = horse.inventory;
                //Main.LOGGER.debug("found horse");
            }

            if(entity.get() instanceof InventoryCarrier carrier){
                this.container = carrier.getInventory();
                //Main.LOGGER.debug("found carrier");
            }

            if (entity.get() instanceof Container containerEntity) {
                this.container = containerEntity;
                //Main.LOGGER.debug("found containerEntity");
            }

            this.recruit.getNavigation().moveTo(entity.get().getX(), entity.get().getY(), entity.get().getZ(), 1.15D);
            //Main.LOGGER.debug("Moving to entity");
            if (entity.get().closerThan(recruit, 3) && container != null) {

                this.recruit.getNavigation().stop();
                this.recruit.getLookControl().setLookAt(entity.get().getX(), entity.get().getY() + 1, entity.get().getZ(), 10.0F, (float) this.recruit.getMaxHeadXRot());

                //Main.LOGGER.debug("Getting food from inv");

                for(int i = 0; i < 3; i++) {
                    ItemStack foodItem = this.getFoodFromInv(container);
                    ItemStack food;
                    if (foodItem != null) {
                        food = foodItem.copy();
                        food.setCount(1);
                        recruit.getInventory().addItem(food);
                        foodItem.shrink(1);
                    }
                    else{
                        //Main.LOGGER.debug("Chest empty");
                        break;
                    }
                }
            }
        }
        else {
            //Main.LOGGER.debug("Entity not found");
        }
    }


    private Optional<Entity> findEntityPos() {
        if(this.recruit.getUpkeepUUID() != null) {
            return recruit.level.getEntitiesOfClass(Entity.class, recruit.getBoundingBox().inflate(20.0D))
                    .stream()
                    .filter(entity -> entity.getUUID().equals(recruit.getUpkeepUUID())).findAny();
        }
        else return Optional.empty();
    }

    @Nullable
    private ItemStack getFoodFromInv(Container inv){
        ItemStack itemStack = null;
        for(int i = 0; i < inv.getContainerSize(); i++){
            if(inv.getItem(i).isEdible()){
                itemStack = inv.getItem(i);
                break;
            }
        }
        return itemStack;
    }
}
