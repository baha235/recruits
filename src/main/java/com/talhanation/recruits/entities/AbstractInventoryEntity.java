package com.talhanation.recruits.entities;

import com.talhanation.recruits.inventory.RecruitSimpleContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Predicate;

import static net.minecraft.world.entity.EquipmentSlot.*;

public abstract class AbstractInventoryEntity extends PathfinderMob {


    //iv slots
    //4 = offhand
    //5 = mainhand
    //0,1,2,3 = armor
    //rest = inv

    public RecruitSimpleContainer inventory;
    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

    public AbstractInventoryEntity(EntityType<? extends AbstractInventoryEntity> entityType, Level world) {
        super(entityType, world);
        this.createInventory();
    }

    ///////////////////////////////////TICK/////////////////////////////////////////

    public void aiStep() {
        super.aiStep();
    }

    public void tick() {
        super.tick();
    }

    ////////////////////////////////////DATA////////////////////////////////////

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        ListTag listnbt = new ListTag();
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundnbt = new CompoundTag();
                compoundnbt.putByte("Slot", (byte) i);
                itemstack.save(compoundnbt);
                listnbt.add(compoundnbt);
            }
        }

        nbt.put("Items", listnbt);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        ListTag listnbt = nbt.getList("Items", 10);//muss 10 sein amk sonst nix save
        this.createInventory();

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundTag compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;
            if (j < this.inventory.getContainerSize()) {
                this.inventory.setItem(j, ItemStack.of(compoundnbt));
            }
        }

        ListTag armorItems = nbt.getList("ArmorItems", 10);
        for (int i = 0; i < this.armorItems.size(); ++i) {
            int index = this.getInventorySlotIndex(Mob.getEquipmentSlotForItem(ItemStack.of(armorItems.getCompound(i))));
            this.inventory.setItem(index, ItemStack.of(armorItems.getCompound(i)));
        }

        ListTag handItems = nbt.getList("HandItems", 10);
        for (int i = 0; i < this.handItems.size(); ++i) {
            int index = i == 0 ? 5 : 4; //5 = mainhand 4 = offhand
            this.inventory.setItem(index, ItemStack.of(handItems.getCompound(i)));
        }
    }


    ////////////////////////////////////GET////////////////////////////////////

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public int getInventorySize() {
        return 15;
    }

    public int getInventoryColumns() {
        return 3;
    }

    public int getInventorySlotIndex(EquipmentSlot slot) {
        switch (slot) {
            case HEAD -> {
                return 0;
            }
            case CHEST -> {
                return 1;
            }
            case LEGS -> {
                return 2;
            }
            case FEET -> {
                return 3;
            }
            case OFFHAND -> {
                return 4;
            }
            case MAINHAND -> {
                return 5;
            }
        }
        return 6;
    }
    @Nullable
    public EquipmentSlot getEquipmentSlotIndex(int id) {
        switch (id) {
            case 0 -> {return HEAD;}
            case 1 -> {return CHEST;}
            case 2 -> {return LEGS;}
            case 3 -> {return FEET;}
            case 4 -> {return OFFHAND;}
            case 5 -> {return MAINHAND;}
        }
        return null;
    }

    ////////////////////////////////////SET////////////////////////////////////

    @Override
    public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {
        super.setItemSlot(slotIn, stack);
        switch (slotIn) {
            case HEAD ->{
                if (this.inventory.getItem(0).isEmpty())
                    this.inventory.setItem(0, this.armorItems.get(slotIn.getIndex()));
            }
            case CHEST-> {
                if (this.inventory.getItem(1).isEmpty())
                    this.inventory.setItem(1, this.armorItems.get(slotIn.getIndex()));
            }
            case LEGS-> {
                if (this.inventory.getItem(2).isEmpty())
                    this.inventory.setItem(2, this.armorItems.get(slotIn.getIndex()));
            }
            case FEET-> {
                if (this.inventory.getItem(3).isEmpty())
                    this.inventory.setItem(3, this.armorItems.get(slotIn.getIndex()));
            }
            case OFFHAND-> {
                if (this.inventory.getItem(4).isEmpty())
                    this.inventory.setItem(4, this.handItems.get(slotIn.getIndex()));
            }
            case MAINHAND-> {
                if (this.inventory.getItem(5).isEmpty())
                    this.inventory.setItem(5, this.handItems.get(slotIn.getIndex()));
            }
        }

    }
    public @NotNull SlotAccess getSlot(int slot) {
        return slot == 499 ? new SlotAccess() {
            public ItemStack get() {
                return new ItemStack(Items.CHEST);
            }

            public boolean set(ItemStack stack) {
                if (stack.isEmpty()) {

                    AbstractInventoryEntity.this.createInventory();

                    return true;
                } else {
                    return false;
                }
            }
        } : super.getSlot(slot);
    }



    ////////////////////////////////////OTHER FUNCTIONS////////////////////////////////////

    protected void createInventory() {
        SimpleContainer inventory = this.inventory;
        this.inventory = new RecruitSimpleContainer(this.getInventorySize(), this){

        };
        if (inventory != null) {
            int i = Math.min(inventory.getContainerSize(), this.inventory.getContainerSize());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = inventory.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }
        this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
    }

    public void die(DamageSource dmg) {
        super.die(dmg);
        for (int i = 0; i < this.inventory.getContainerSize(); i++)
            Containers.dropItemStack(this.level, getX(), getY(), getZ(), this.inventory.getItem(i));
    }

    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack itemstack = itemEntity.getItem();

        if (this.canEquipItem(itemstack)) {
            this.equipItem(itemstack);
            this.onItemPickup(itemEntity);
            this.take(itemEntity, itemstack.getCount());
            itemEntity.discard();
        }
        else {
            RecruitSimpleContainer inventory = this.inventory;
            boolean flag = inventory.canAddItem(itemstack);
            if (!flag) {
                return;
            }
            this.onItemPickup(itemEntity);
            this.take(itemEntity, itemstack.getCount());
            ItemStack itemstack1 = inventory.addItem(itemstack);
            if (itemstack1.isEmpty()) {
                itemEntity.remove(RemovalReason.KILLED);
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }
    }

    public void equipItem(ItemStack itemStack) {
        EquipmentSlot equipmentslot = getEquipmentSlotForItem(itemStack);
        ItemStack currentArmor = this.getItemBySlot(equipmentslot);
        this.spawnAtLocation(currentArmor);
        this.setItemSlot(equipmentslot, itemStack);
        this.inventory.setItem(getInventorySlotIndex(equipmentslot), itemStack);
        this.playEquipSound(itemStack);
    }

    public boolean canEquipItem(@NotNull ItemStack itemStack) {
        EquipmentSlot equipmentslot = getEquipmentSlotForItem(itemStack);
        ItemStack currentArmor = this.getItemBySlot(equipmentslot);
        boolean flag = this.canReplaceCurrentItem(itemStack, currentArmor);

        return flag && this.canHoldItem(itemStack);
    }

    @Override
    public boolean wantsToPickUp(@NotNull ItemStack itemStack){
       if (itemStack.getItem() instanceof ArmorItem){
               return canEquipItem(itemStack);
       }
       else
           return itemStack.isEdible();
    }


    public abstract Predicate<ItemEntity> getAllowedItems();

    public abstract void openGUI(Player player);

    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (this.isAlive() && capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && itemHandler != null)
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (itemHandler != null) {
            net.minecraftforge.common.util.LazyOptional<?> oldHandler = itemHandler;
            itemHandler = null;
            oldHandler.invalidate();
        }
    }
}