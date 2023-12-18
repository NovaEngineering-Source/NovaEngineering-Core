package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.container.ContainerModularServerAssembler;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.assembly.AssemblyInvCPUConst;
import github.kasuminova.novaeng.common.hypernet.proc.server.assembly.AssemblyInvCalculateCardConst;
import github.kasuminova.novaeng.common.hypernet.proc.server.assembly.AssemblyInvExtensionConst;
import github.kasuminova.novaeng.common.hypernet.proc.server.assembly.AssemblyInvHeatRadiatorConst;
import github.kasuminova.novaeng.common.util.ServerModuleInv;
import io.netty.util.collection.IntObjectHashMap;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class AssemblySlotManager {
    private final ModularServer modularServer;

    private final Map<String, IntObjectHashMap<SlotConditionItemHandler>> inventorySlots = new HashMap<>();

    public AssemblySlotManager(final ModularServer modularServer) {
        this.modularServer = modularServer;
    }

    public void initSlots() {
        addCPUSlots();
        addCalculateCardSlots();
        addExtensionCardSlots();
        addPowerSlots();
        addHeatRadiatorSlots();
    }

    protected void addCPUSlots() {
        ServerModuleInv invCPU = modularServer.getInvByName("cpu");

        SlotCPUExtItemHandler ext_2 = addSlot(new SlotCPUExtItemHandler(AssemblyInvCPUConst.CPU_EXTENSION_SLOT_ID, invCPU));

        // Default CPU Slots
        addSlot(new SlotCPUItemHandler(0, 0, invCPU));
        addSlot(new SlotCPUItemHandler(1, 1, invCPU));

        // Extension CPU Slots
        addSlot(new SlotCPUItemHandler(2, 2, invCPU).dependsOn(ext_2));
        addSlot(new SlotCPUItemHandler(3, 3, invCPU).dependsOn(ext_2));

        // Default RAM Slots
        addSlot(new SlotRAMItemHandler(0, 4, invCPU));
        addSlot(new SlotRAMItemHandler(1, 5, invCPU));
        addSlot(new SlotRAMItemHandler(2, 6, invCPU));
        addSlot(new SlotRAMItemHandler(3, 7, invCPU));
        addSlot(new SlotRAMItemHandler(4, 8, invCPU));
        addSlot(new SlotRAMItemHandler(5, 9, invCPU));
        addSlot(new SlotRAMItemHandler(6, 10, invCPU));
        addSlot(new SlotRAMItemHandler(7, 11, invCPU));

        // Extension RAM Slots
        addSlot(new SlotRAMItemHandler(8, 12, invCPU).dependsOn(ext_2));
        addSlot(new SlotRAMItemHandler(9, 13, invCPU).dependsOn(ext_2));
        addSlot(new SlotRAMItemHandler(10, 14, invCPU).dependsOn(ext_2));
        addSlot(new SlotRAMItemHandler(11, 15, invCPU).dependsOn(ext_2));
        addSlot(new SlotRAMItemHandler(12, 16, invCPU).dependsOn(ext_2));
        addSlot(new SlotRAMItemHandler(13, 17, invCPU).dependsOn(ext_2));
        addSlot(new SlotRAMItemHandler(14, 18, invCPU).dependsOn(ext_2));
        addSlot(new SlotRAMItemHandler(15, 19, invCPU).dependsOn(ext_2));
    }

    protected void addCalculateCardSlots() {
        ServerModuleInv invCalculateCard = modularServer.getInvByName("calculate_card");

        SlotCalculateCardExtItemHandler ext_0 = addSlot(new SlotCalculateCardExtItemHandler(0, AssemblyInvCalculateCardConst.EXTENSION_SLOT_0_ID, invCalculateCard));
        for (int slotID = 0; slotID < 4; slotID++) {
            addSlot(new SlotCalculateCardItemHandler(slotID, slotID, invCalculateCard).dependsOn(ext_0));
        }
        SlotCalculateCardExtItemHandler ext_1 = addSlot(new SlotCalculateCardExtItemHandler(1, AssemblyInvCalculateCardConst.EXTENSION_SLOT_1_ID, invCalculateCard));
        for (int slotID = 4; slotID < 8; slotID++) {
            addSlot(new SlotCalculateCardItemHandler(slotID, slotID, invCalculateCard).dependsOn(ext_1));
        }
        SlotCalculateCardExtItemHandler ext_2 = addSlot(new SlotCalculateCardExtItemHandler(2, AssemblyInvCalculateCardConst.EXTENSION_SLOT_2_ID, invCalculateCard));
        for (int slotID = 8; slotID < 12; slotID++) {
            addSlot(new SlotCalculateCardItemHandler(slotID, slotID, invCalculateCard).dependsOn(ext_2));
        }
        SlotCalculateCardExtItemHandler ext_3 = addSlot(new SlotCalculateCardExtItemHandler(3, AssemblyInvCalculateCardConst.EXTENSION_SLOT_3_ID, invCalculateCard));
        for (int slotID = 12; slotID < 16; slotID++) {
            addSlot(new SlotCalculateCardItemHandler(slotID, slotID, invCalculateCard).dependsOn(ext_3));
        }
    }

    protected void addExtensionCardSlots() {
        ServerModuleInv invExtensionCard = modularServer.getInvByName("extension");

        SlotExtensionCardExtItemHandler ext_0 = addSlot(new SlotExtensionCardExtItemHandler(0, AssemblyInvExtensionConst.EXTENSION_SLOT_0_ID, invExtensionCard));
        for (int slotID = 0; slotID < 4; slotID++) {
            addSlot(new SlotExtensionCardItemHandler(slotID, slotID, invExtensionCard).dependsOn(ext_0));
        }
        SlotExtensionCardExtItemHandler ext_1 = addSlot(new SlotExtensionCardExtItemHandler(1, AssemblyInvExtensionConst.EXTENSION_SLOT_1_ID, invExtensionCard));
        for (int slotID = 4; slotID < 8; slotID++) {
            addSlot(new SlotExtensionCardItemHandler(slotID, slotID, invExtensionCard).dependsOn(ext_1));
        }
        SlotExtensionCardExtItemHandler ext_2 = addSlot(new SlotExtensionCardExtItemHandler(2, AssemblyInvExtensionConst.EXTENSION_SLOT_2_ID, invExtensionCard));
        for (int slotID = 8; slotID < 12; slotID++) {
            addSlot(new SlotExtensionCardItemHandler(slotID, slotID, invExtensionCard).dependsOn(ext_2));
        }
        SlotExtensionCardExtItemHandler ext_3 = addSlot(new SlotExtensionCardExtItemHandler(3, AssemblyInvExtensionConst.EXTENSION_SLOT_3_ID, invExtensionCard));
        for (int slotID = 12; slotID < 16; slotID++) {
            addSlot(new SlotExtensionCardItemHandler(slotID, slotID, invExtensionCard).dependsOn(ext_3));
        }
    }

    protected void addPowerSlots() {
        ServerModuleInv invPower = modularServer.getInvByName("power");

        for (int slotID = 0; slotID < 4; slotID++) {
            addSlot(new SlotPSUItemHandler(slotID, slotID, invPower));
        }
        for (int slotID = 4; slotID < 8; slotID++) {
            addSlot(new SlotCapacitorItemHandler(slotID - 4, slotID, invPower));
        }
    }

    protected void addHeatRadiatorSlots() {
        ServerModuleInv invHeatRadiator = modularServer.getInvByName("heat_radiator");

        addSlot(new SlotCPUHeatRadiatorItemHandler(AssemblyInvHeatRadiatorConst.CPU_HEAT_RADIATOR_SLOT_ID, invHeatRadiator));
        addSlot(new SlotRAMHeatRadiatorItemHandler(AssemblyInvHeatRadiatorConst.RAM_HEAT_RADIATOR_SLOT_ID, invHeatRadiator));
        addSlot(new SlotCalculateCardHeatRadiatorItemHandler(AssemblyInvHeatRadiatorConst.CALCULATE_CARD_HEAT_RADIATOR_SLOT_ID, invHeatRadiator));
        addSlot(new SlotExtensionCardHeatRadiatorItemHandler(AssemblyInvHeatRadiatorConst.EXTENSION_CARD_HEAT_RADIATOR_SLOT_ID, invHeatRadiator));
        addSlot(new SlotCopperPipeItemHandler(AssemblyInvHeatRadiatorConst.COPPER_PIPE_SLOT_ID, invHeatRadiator));
    }

    public <SLOT extends SlotConditionItemHandler> SLOT addSlot(@Nonnull final SLOT slot) {
        ServerModuleInv inv = slot.getItemHandler();
        int slotID = slot.getSlotIndex();
        inventorySlots.computeIfAbsent(inv.getInvName(), v -> new IntObjectHashMap<>()).put(slotID, slot);
        return slot;
    }

    public void addSlot(@Nonnull final SlotConditionItemHandler slot, @Nonnull final ServerModuleInv serverModuleInv, @Nonnull final String invName, final int slotId) {
        if (serverModuleInv.isSlotAvailable(slotId)) {
            inventorySlots.computeIfAbsent(invName, v -> new IntObjectHashMap<>()).put(slotId, slot);
        }
    }

    public void addAllSlotToContainer(final ContainerModularServerAssembler container) {
        for (final IntObjectHashMap<SlotConditionItemHandler> invSlots : inventorySlots.values()) {
            for (final SlotConditionItemHandler slot : invSlots.values()) {
                container.addSlotToContainer(slot);
            }
        }
    }

    public SlotConditionItemHandler getSlot(@Nonnull final String invName, final int slotId) {
        IntObjectHashMap<SlotConditionItemHandler> invSlots = inventorySlots.get(invName);
        if (invSlots != null) {
            SlotConditionItemHandler slot = invSlots.get(slotId);
            if (slot.getItemHandler().isSlotAvailable(slotId)) {
                return slot;
            }
        }
        return null;
    }

}
