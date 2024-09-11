package github.kasuminova.novaeng.common.util;

import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import com.github.bsideup.jabel.Desugar;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Map;

public class AEItemStackSet {

    private final Map<Entry, Entry> entries = new Object2ObjectOpenHashMap<>();
    private final List<Entry> entryList = new ObjectArrayList<>();

    public int add(IAEItemStack stack) {
        Entry entry = entries.get(new Entry(stack, -1));
        if (entry == null) {
            entry = new Entry(stack.copy().setStackSize(1), entryList.size());
            entries.put(entry, entry);
            entryList.add(entry);
        }
        return entry.id();
    }

    protected void addInternal(IAEItemStack stack) {
        Entry entry = new Entry(stack, entryList.size());
        entryList.add(entry);
        entries.put(entry, entry);
    }

    public IAEItemStack get(int id) {
        return entryList.get(id).stack().copy();
    }

    public void writeToBuffer(final ByteBuf buf) {
        buf.writeInt(entryList.size());
        for (Entry entry : entryList) {
            try {
                entry.stack().writeToPacket(buf);
            } catch (Throwable ignored) {
            }
        }
    }

    public void fromBuffer(final ByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            addInternal(AEItemStack.fromPacket(buf));
        }
    }

    @Desugar
    private record Entry(IAEItemStack stack, int id) {

        @Override
        public int hashCode() {
            return stack.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof Entry entry)) {
                return false;
            }
            return stack.equals(entry.stack);
        }

    }

}
