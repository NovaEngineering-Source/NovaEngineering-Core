package github.kasuminova.novaeng.common.profiler;

import com.mojang.authlib.GameProfile;
import github.kasuminova.novaeng.common.util.ClassUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SPacketProfiler {

    private static final Map<GameProfile, Map<Class<?>, AtomicLong>> PLAYER_CLIENT_PACKETS = new ConcurrentHashMap<>();

    public static void onPacketReceived(final NetworkManager networkManager, final Object packet) {
        INetHandler netHandler = networkManager.getNetHandler();
        if (!(netHandler instanceof NetHandlerPlayServer handlerServer)) {
            return;
        }

        GameProfile profile = handlerServer.player.getGameProfile();
        Map<Class<?>, AtomicLong> packetCounter = PLAYER_CLIENT_PACKETS.computeIfAbsent(profile, v -> new ConcurrentHashMap<>());

        packetCounter.computeIfAbsent(packet.getClass(), v -> new AtomicLong(0)).getAndIncrement();
    }

    public static void onPacketReceived(final EntityPlayer player, final Object packet) {
        GameProfile profile = player.getGameProfile();
        Map<Class<?>, AtomicLong> packetCounter = PLAYER_CLIENT_PACKETS.computeIfAbsent(profile, v -> new ConcurrentHashMap<>());

        packetCounter.computeIfAbsent(packet.getClass(), v -> new AtomicLong(0)).getAndIncrement();
    }

    public static List<String> getProfilerMessages() {
        Map<Class<?>, AtomicLong> packetCounter = new ConcurrentHashMap<>();
        PLAYER_CLIENT_PACKETS.forEach((profile, map) ->
                map.forEach((pClass, counter) ->
                        packetCounter.computeIfAbsent(pClass, v -> new AtomicLong(0)).addAndGet(counter.get())));

        @SuppressWarnings("SimplifyStreamApiCallChains")
        List<Map.Entry<Class<?>, AtomicLong>> largest = packetCounter.entrySet().stream()
                .sorted((o1, o2) -> Long.compare(o2.getValue().get(), o1.getValue().get()))
                .limit(Math.min(50, packetCounter.size() / 2 + packetCounter.size() % 2))
                .collect(Collectors.toList());

        @SuppressWarnings("SimplifyStreamApiCallChains")
        List<Map.Entry<Class<?>, AtomicLong>> smallest = packetCounter.entrySet().stream()
                .sorted(Comparator.comparingLong(o -> o.getValue().get()))
                .limit(Math.min(50, packetCounter.size() / 2))
                .collect(Collectors.toList());

        Map<Class<?>, Map<GameProfile, AtomicLong>> pClassCounter = new ConcurrentHashMap<>();
        PLAYER_CLIENT_PACKETS.forEach((profile, map) -> map.forEach((pClass, counter) -> pClassCounter
                .computeIfAbsent(pClass, v -> new ConcurrentHashMap<>())
                .computeIfAbsent(profile, v -> new AtomicLong(0))
                .addAndGet(counter.get()))
        );

        List<String> messages = new ArrayList<>();
        messages.add(TextFormatting.BLUE + "已接收的网络包（最大排序，至多 50）：");
        largest.forEach(entry -> {
            Class<?> pClass = entry.getKey();
            AtomicLong counter = entry.getValue();
            messages.add(TextFormatting.BLUE + "  " + getPacketClassName(pClass) + ": " + TextFormatting.GOLD + counter.get());
        });

        messages.add(TextFormatting.BLUE + "已接收的网络包（最小排序，至多 50）：");
        smallest.forEach(entry -> {
            Class<?> pClass = entry.getKey();
            AtomicLong counter = entry.getValue();
            messages.add(TextFormatting.BLUE + "  " + getPacketClassName(pClass) + ": " + TextFormatting.GOLD + counter.get());
        });

        messages.add(TextFormatting.RED + "统计中的可疑网络包：");
        pClassCounter.forEach((pClass, map) -> {
            if (map.size() == 1) {
                map.forEach((profile, counter) -> {
                    messages.add(TextFormatting.RED + profile.getName() + " (" + profile.getId() + ")");
                    messages.add(TextFormatting.BLUE + "  " + getPacketClassName(pClass) + ": " + TextFormatting.GOLD + counter.get());
                });
            }
        });

        return messages;
    }

    public static List<String> getFullProfilerMessages() {
        List<String> messages = new ArrayList<>();

        PLAYER_CLIENT_PACKETS.forEach((profile, map) -> {
            messages.add(TextFormatting.BLUE + profile.getName() + " (" + profile.getId() + ")");
            map.entrySet().stream()
                    .sorted((o1, o2) -> Long.compare(o2.getValue().get(), o1.getValue().get()))
                    .forEach(entry -> {
                        Class<?> pClass = entry.getKey();
                        AtomicLong counter = entry.getValue();
                        messages.add(TextFormatting.BLUE + "  " + getPacketClassName(pClass) + ": " + TextFormatting.GOLD + counter.get());
                    });
        });

        return messages;
    }

    private static String getPacketClassName(final Class<?> pClass) {
        if (ClassUtils.getAllInterfaces(pClass).contains(Packet.class)) {
            return pClass.getSimpleName();
        }
        return pClass.getName();
    }

}
