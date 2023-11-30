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
        // CPU Slots
        ServerModuleInv invCPU = modularServer.getInvByName("cpu");
        for (int slotID = 0; slotID < 4; slotID++) {
            addSlot(new SlotCPUItemHandler(invCPU, slotID), invCPU, "cpu", slotID);
        }
        for (int slotID = AssemblyInvCPUConst.RAM_SLOT_ID_START; slotID < 20; slotID++) {
            addSlot(new SlotRAMItemHandler(invCPU, slotID), invCPU, "cpu", slotID);
        }
        addSlot(new SlotCPUExtItemHandler(invCPU, AssemblyInvCPUConst.CPU_EXTENSION_SLOT_ID), invCPU, "cpu",
                AssemblyInvCPUConst.CPU_EXTENSION_SLOT_ID);

        // Calculate Card Slots
        ServerModuleInv invCalculateCard = modularServer.getInvByName("calculate_card");
        for (int slotID = 0; slotID < 16; slotID++) {
            addSlot(new SlotCalculateCardItemHandler(invCalculateCard, slotID), invCalculateCard, "calculate_card", slotID);
        }
        addSlot(new SlotCalculateCardExtItemHandler(invCalculateCard, AssemblyInvCalculateCardConst.EXTENSION_SLOT_0_ID),
                invCalculateCard, "calculate_card", AssemblyInvCalculateCardConst.EXTENSION_SLOT_0_ID);
        addSlot(new SlotCalculateCardExtItemHandler(invCalculateCard, AssemblyInvCalculateCardConst.EXTENSION_SLOT_1_ID),
                invCalculateCard, "calculate_card", AssemblyInvCalculateCardConst.EXTENSION_SLOT_1_ID);
        addSlot(new SlotCalculateCardExtItemHandler(invCalculateCard, AssemblyInvCalculateCardConst.EXTENSION_SLOT_2_ID),
                invCalculateCard, "calculate_card", AssemblyInvCalculateCardConst.EXTENSION_SLOT_2_ID);
        addSlot(new SlotCalculateCardExtItemHandler(invCalculateCard, AssemblyInvCalculateCardConst.EXTENSION_SLOT_3_ID),
                invCalculateCard, "calculate_card", AssemblyInvCalculateCardConst.EXTENSION_SLOT_3_ID);

        // Extension Card Slots
        ServerModuleInv invExtension = modularServer.getInvByName("extension");
        for (int slotID = 0; slotID < 16; slotID++) {
            addSlot(new SlotExtensionCardItemHandler(invExtension, slotID), invExtension, "extension", slotID);
        }
        addSlot(new SlotExtensionCardExtItemHandler(invExtension, AssemblyInvExtensionConst.EXTENSION_SLOT_0_ID),
                invExtension, "extension", AssemblyInvExtensionConst.EXTENSION_SLOT_0_ID);
        addSlot(new SlotExtensionCardExtItemHandler(invExtension, AssemblyInvExtensionConst.EXTENSION_SLOT_1_ID),
                invExtension, "extension", AssemblyInvExtensionConst.EXTENSION_SLOT_1_ID);
        addSlot(new SlotExtensionCardExtItemHandler(invExtension, AssemblyInvExtensionConst.EXTENSION_SLOT_2_ID),
                invExtension, "extension", AssemblyInvExtensionConst.EXTENSION_SLOT_2_ID);
        addSlot(new SlotExtensionCardExtItemHandler(invExtension, AssemblyInvExtensionConst.EXTENSION_SLOT_3_ID),
                invExtension, "extension", AssemblyInvExtensionConst.EXTENSION_SLOT_3_ID);

        // Power Slots
        ServerModuleInv invPower = modularServer.getInvByName("power");
        for (int slotID = 0; slotID < 4; slotID++) {
            addSlot(new SlotPSUItemHandler(invPower, slotID), invPower, "power", slotID);
        }
        for (int slotID = 4; slotID < 8; slotID++) {
            addSlot(new SlotCapacitorItemHandler(invPower, slotID), invPower, "power", slotID);
        }

        // Heat Radiator Slots
        ServerModuleInv invHeatRadiator = modularServer.getInvByName("heat_radiator");
        addSlot(new SlotCPUHeatRadiatorItemHandler(invHeatRadiator, AssemblyInvHeatRadiatorConst.CPU_HEAT_RADIATOR_SLOT_ID),
                invHeatRadiator, "heat_radiator", AssemblyInvHeatRadiatorConst.CPU_HEAT_RADIATOR_SLOT_ID);
        addSlot(new SlotRAMHeatRadiatorItemHandler(invHeatRadiator, AssemblyInvHeatRadiatorConst.RAM_HEAT_RADIATOR_SLOT_ID),
                invHeatRadiator, "heat_radiator", AssemblyInvHeatRadiatorConst.RAM_HEAT_RADIATOR_SLOT_ID);
        addSlot(new SlotCalculateCardHeatRadiatorItemHandler(invHeatRadiator, AssemblyInvHeatRadiatorConst.CALCULATE_CARD_HEAT_RADIATOR_SLOT_ID),
                invHeatRadiator, "heat_radiator", AssemblyInvHeatRadiatorConst.CALCULATE_CARD_HEAT_RADIATOR_SLOT_ID);
        addSlot(new SlotExtensionCardHeatRadiatorItemHandler(invHeatRadiator, AssemblyInvHeatRadiatorConst.EXTENSION_CARD_HEAT_RADIATOR_SLOT_ID),
                invHeatRadiator, "heat_radiator", AssemblyInvHeatRadiatorConst.EXTENSION_CARD_HEAT_RADIATOR_SLOT_ID);
        addSlot(new SlotCopperPipeItemHandler(invHeatRadiator, AssemblyInvHeatRadiatorConst.COPPER_PIPE_SLOT_ID),
                invHeatRadiator, "heat_radiator", AssemblyInvHeatRadiatorConst.COPPER_PIPE_SLOT_ID);
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
        return invSlots == null ? null : invSlots.get(slotId);
    }

}
