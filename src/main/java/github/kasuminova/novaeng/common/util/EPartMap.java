package github.kasuminova.novaeng.common.util;

import github.kasuminova.novaeng.common.tile.ecotech.EPart;
import github.kasuminova.novaeng.common.tile.ecotech.EPartController;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class EPartMap<P extends EPart<?>> {

    protected final Map<Class<?>, List<P>> parts = new IdentityHashMap<>();

    public P addPart(final P part) {
        parts.computeIfAbsent(part.getClass(), k -> new ObjectArrayList<>()).add(part);
        ClassUtils.getAllSuperClasses(part.getClass(), EPart.class).stream()
                .map(clazz -> parts.computeIfAbsent(clazz, k -> new ObjectArrayList<>()))
                .forEach(partList -> partList.add(part));
        return part;
    }

    public <PT extends P> List<PT> getParts(final Class<PT> partClass) {
        return (List<PT>) parts.getOrDefault(partClass, new ObjectArrayList<>());
    }

    public void forEachPart(final Consumer<P> consumer) {
        parts.values().stream().flatMap(Collection::stream).forEach(consumer);
    }

    public void assemble(final EPartController<P> controller) {
        forEachPart(part -> {
            part.setController(controller);
            part.onAssembled();
        });
    }

    public void disassemble() {
        forEachPart(part -> {
            part.onDisassembled();
            part.setController(null);
        });
        parts.clear();
    }

    public void clear() {
        forEachPart(part -> part.setController(null));
        parts.clear();
    }

}
