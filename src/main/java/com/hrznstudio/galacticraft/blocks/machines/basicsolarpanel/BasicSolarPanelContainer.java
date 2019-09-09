package com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel;

import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import com.hrznstudio.galacticraft.blocks.machines.MachineContainer;
import com.hrznstudio.galacticraft.container.slot.ItemSpecificSlot;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.container.Container;
import net.minecraft.container.Property;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelContainer extends MachineContainer<BasicSolarPanelBlockEntity> {

    public static final ContainerFactory<Container> FACTORY = createFactory(BasicSolarPanelBlockEntity.class, BasicSolarPanelContainer::new);

    private final Inventory inventory;
    private final Property status = Property.create();
    private BasicSolarPanelBlockEntity solarPanel;

    public BasicSolarPanelContainer(int syncId, PlayerEntity playerEntity, BasicSolarPanelBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity);
        this.solarPanel = blockEntity;
        this.inventory = new InventoryFixedWrapper(solarPanel.getInventory()) {
            @Override
            public boolean canPlayerUseInv(PlayerEntity player) {
                return BasicSolarPanelContainer.this.canUse(player);
            }
        };
        addProperty(status);

        this.addSlot(new ItemSpecificSlot(this.inventory, 0, 8, 53, GalacticraftItems.BATTERY));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerEntity.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerEntity.inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int slotId) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slotList.get(slotId);

        if (slot != null && slot.hasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (itemStack.isEmpty()) {
                return itemStack;
            }

            if (slotId < this.solarPanel.getInventory().getSlotCount()) {
                if (!this.insertItem(itemStack1, this.solarPanel.getInventory().getSlotCount(), this.slotList.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack1, 0, this.solarPanel.getInventory().getSlotCount(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack1.getCount() == 0) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }


    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public void sendContentUpdates() {
        status.set(blockEntity.status.ordinal());
        super.sendContentUpdates();
    }

    @Override
    public void setProperties(int index, int value) {
        super.setProperties(index, value);
        blockEntity.status = BasicSolarPanelStatus.get(status.get());
    }
}
