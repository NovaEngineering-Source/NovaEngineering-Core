package github.kasuminova.novaeng.common.util;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class ClassUtils {
    private static final Map<Class<?>, Set<Class<?>>> CLASS_INTERFACES_CACHE = new WeakHashMap<>();
    private static final Map<Class<?>, Set<Class<?>>> CLASS_SUPERCLASSES_CACHE = new WeakHashMap<>();

    public static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        Set<Class<?>> cache = CLASS_INTERFACES_CACHE.get(clazz);
        if (cache != null) {
            return cache;
        }
        synchronized (CLASS_INTERFACES_CACHE) {
            cache = CLASS_INTERFACES_CACHE.get(clazz);
            if (cache != null) {
                return cache;
            }
            Set<Class<?>> interfaces = getAllInterfaces(clazz, Sets.newIdentityHashSet());
            CLASS_INTERFACES_CACHE.put(clazz, interfaces);
            return interfaces;
        }
    }

    public static Set<Class<?>> getAllSuperClasses(Class<?> clazz, Class<?> topClass) {
        Set<Class<?>> cache = CLASS_SUPERCLASSES_CACHE.get(clazz);
        if (cache != null) {
            return cache;
        }
        synchronized (CLASS_SUPERCLASSES_CACHE) {
            cache = CLASS_SUPERCLASSES_CACHE.get(clazz);
            if (cache != null) {
                return cache;
            }
            Set<Class<?>> superClasses = getAllSuperClasses(clazz, topClass, Sets.newIdentityHashSet());
            CLASS_SUPERCLASSES_CACHE.put(clazz, superClasses);
            return superClasses;
        }
    }

    protected static Set<Class<?>> getAllInterfaces(Class<?> clazz, Set<Class<?>> interfaceSet) {
        interfaceSet.addAll(Arrays.asList(clazz.getInterfaces()));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            getAllInterfaces(superClass, interfaceSet);
        }
        return interfaceSet;
    }

    protected static Set<Class<?>> getAllSuperClasses(Class<?> clazz, Class<?> topClass, Set<Class<?>> superClasses) {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != topClass) {
            superClasses.add(superClass);
            getAllSuperClasses(superClass, topClass, superClasses);
        }
        return superClasses;
    }

}
