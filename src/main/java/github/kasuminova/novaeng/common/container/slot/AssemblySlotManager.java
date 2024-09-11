package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.container.ContainerModularServerAssembler;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import github.kasuminova.novaeng.common.hypernet.computer.assembly.AssemblyInvCPUConst;
import github.kasuminova.novaeng.common.hypernet.computer.assembly.AssemblyInvCalculateCardConst;
import github.kasuminova.novaeng.common.hypernet.computer.assembly.AssemblyInvExtensionConst;
import github.kasuminova.novaeng.common.hypernet.computer.assembly.AssemblyInvPowerConst;
import github.kasuminova.novaeng.common.util.TileItemHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class AssemblySlotManager {
    private final ModularServer modularServer;

    private final Map<String, Int2ObjectMap<SlotConditionItemHandler>> inventorySlots = new HashMap<>();

    public AssemblySlotManager(final ModularServer modularServer) {
        this.modularServer = modularServer;
    }

    public void initSlots() {
        addCPUSlots();
        addCalculateCardSlots();
        addExtensionCardSlots();
        addPowerSlots();
    }

    protected void addCPUSlots() {
        TileItemHandler invCPU = modularServer.getInvByName("cpu");

        SlotCPUExtItemHandler ext_2 = addSlot(new SlotCPUExtItemHandler(AssemblyInvCPUConst.CPU_EXTENSION_SLOT_ID, invCPU));

        // Default CPU Slots
        for (int i = 0; i < 2; i++) {
            addSlot(new SlotCPUItemHandler(i, i, invCPU))
                    .softDependsOn(addSlot(new SlotCPUHeatRadiatorItemHandler(i, AssemblyInvCPUConst.CPU_HEAT_RADIATOR_SLOT_ID_START + i, invCPU)));
        }
        // Extension CPU Slots
        for (int i = 0; i < 2; i++) {
            addSlot(new SlotCPUItemHandler(2 + i, 2 + i, invCPU))
                    .softDependsOn(addSlot(new SlotCPUHeatRadiatorItemHandler(2 + i, AssemblyInvCPUConst.CPU_HEAT_RADIATOR_SLOT_ID_START + 2 + i, invCPU))
                            .dependsOn(ext_2))
                    .dependsOn(ext_2);
        }

        // Default RAM Slots
        for (int i = 0; i < 8; i++) {
            addSlot(new SlotRAMItemHandler(i, AssemblyInvCPUConst.RAM_SLOT_ID_START + i, invCPU))
                    .softDependsOn(addSlot(new SlotRAMHeatRadiatorItemHandler(i, AssemblyInvCPUConst.RAM_HEAT_RADIATOR_SLOT_ID_START + i, invCPU)));
        }
        // Extension RAM Slots
        for (int i = 0; i < 8; i++) {
            addSlot(new SlotRAMItemHandler(8 + i, AssemblyInvCPUConst.RAM_SLOT_ID_START + 8 + i, invCPU))
                    .softDependsOn(addSlot(new SlotRAMHeatRadiatorItemHandler(8 + i, AssemblyInvCPUConst.RAM_HEAT_RADIATOR_SLOT_ID_START + 8 + i, invCPU))
                            .dependsOn(ext_2))
                    .dependsOn(ext_2);
        }
    }

    protected void addCalculateCardSlots() {
        TileItemHandler invCalculateCard = modularServer.getInvByName("calculate_card");

        for (int extSlotID = 0; extSlotID < AssemblyInvCalculateCardConst.LINES; extSlotID++) {
            SlotCalculateCardExtItemHandler calculateCardExt = addSlot(new SlotCalculateCardExtItemHandler(
                    extSlotID, AssemblyInvCalculateCardConst.EXT_SLOT_ID_START + extSlotID, invCalculateCard
            ));

            for (int slotID = 0; slotID < AssemblyInvCalculateCardConst.LINE_SLOTS; slotID++) {
                int id = (extSlotID * AssemblyInvCalculateCardConst.LINE_SLOTS) + slotID;
                addSlot(new SlotCalculateCardItemHandler(id, id, invCalculateCard)
                        .softDependsOn(addSlot(new SlotCalculateCardHeatRadiatorItemHandler(id, AssemblyInvCalculateCardConst.HEAT_RADIATOR_SLOT_ID_START + id, invCalculateCard))
                                .dependsOn(calculateCardExt)))
                        .dependsOn(calculateCardExt);
            }
        }
    }

    protected void addExtensionCardSlots() {
        TileItemHandler invExtensionCard = modularServer.getInvByName("extension");

        for (int extSlotID = 0; extSlotID < AssemblyInvExtensionConst.LINES; extSlotID++) {
            SlotExtensionCardExtItemHandler calculateCardExt = addSlot(new SlotExtensionCardExtItemHandler(
                    extSlotID, AssemblyInvExtensionConst.EXT_SLOT_ID_START + extSlotID, invExtensionCard
            ));

            for (int slotID = 0; slotID < AssemblyInvExtensionConst.LINE_SLOTS; slotID++) {
                int id = (extSlotID * AssemblyInvExtensionConst.LINE_SLOTS) + slotID;
                addSlot(new SlotExtensionCardItemHandler(id, id, invExtensionCard)
                        .softDependsOn(addSlot(new SlotExtensionCardHeatRadiatorItemHandler(id, AssemblyInvExtensionConst.HEAT_RADIATOR_SLOT_ID_START + id, invExtensionCard))
                                .dependsOn(calculateCardExt)))
                        .dependsOn(calculateCardExt);
            }
        }
    }

    protected void addPowerSlots() {
        TileItemHandler invPower = modularServer.getInvByName("power");

        for (int slotID = 0; slotID < 4; slotID++) {
            addSlot(new SlotPSUItemHandler(slotID, slotID, invPower));
        }
        for (int slotID = 0; slotID < 4; slotID++) {
            addSlot(new SlotCapacitorItemHandler(slotID, AssemblyInvPowerConst.CAPACITOR_SLOT_ID_START + slotID, invPower));
        }
    }

    public <SLOT extends SlotConditionItemHandler> SLOT addSlot(@Nonnull final SLOT slot) {
        TileItemHandler inv = slot.getItemHandler();
        int slotID = slot.getSlotIndex();
        inventorySlots.computeIfAbsent(inv.getInvName(), v -> new Int2ObjectOpenHashMap<>()).put(slotID, slot);
        return slot;
    }

    public void addSlot(@Nonnull final SlotConditionItemHandler slot, @Nonnull final TileItemHandler tileItemHandler, @Nonnull final String invName, final int slotId) {
        if (tileItemHandler.isSlotAvailable(slotId)) {
            inventorySlots.computeIfAbsent(invName, v -> new Int2ObjectOpenHashMap<>()).put(slotId, slot);
        }
    }

    public void addAllSlotToContainer(final ContainerModularServerAssembler container) {
        for (final Int2ObjectMap<SlotConditionItemHandler> invSlots : inventorySlots.values()) {
            for (final SlotConditionItemHandler slot : invSlots.values()) {
                container.addSlotToContainer(slot);
            }
        }
    }

    public SlotConditionItemHandler getSlot(@Nonnull final String invName, final int slotId) {
        Int2ObjectMap<SlotConditionItemHandler> invSlots = inventorySlots.get(invName);
        if (invSlots != null) {
            SlotConditionItemHandler slot = invSlots.get(slotId);
            if (slot.getItemHandler().isSlotAvailable(slotId)) {
                return slot;
            }
        }
        return null;
    }

}
