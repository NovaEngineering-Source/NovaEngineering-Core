package github.kasuminova.novaeng.common.hypernet;

import com.mojang.authlib.GameProfile;
import github.kasuminova.novaeng.common.hypernet.perm.UserPerm;
import github.kasuminova.novaeng.common.util.WorldPos;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HyperNet {

    private final UUID owner;
    private final Map<UUID, UserPerm> userPerms = new Object2ObjectLinkedOpenHashMap<>();

    private final Map<Class<? extends NetNode>, Map<WorldPos, NetNode>> onlineNodes = new ConcurrentHashMap<>();

    private String ownerName;
    private String name;

    public HyperNet(final EntityPlayer owner) {
        GameProfile profile = owner.getGameProfile();
        this.owner = profile.getId();
        this.ownerName = profile.getName();
        this.name = profile.getName() + "'s HyperNet";
    }

    public HyperNet(final NBTTagCompound tag, final UUID owner) {
        this.owner = owner;
        this.ownerName = tag.getString("ownerName");
        this.name = tag.getString("name");
    }

    private void addNode(final NetNode node) {
        Map<WorldPos, NetNode> nodes = onlineNodes.computeIfAbsent(node.getClass(), k -> new ConcurrentHashMap<>());
        nodes.put(node.getPos(), node);
    }

    private void removeNode(final NetNode node) {
        Map<WorldPos, NetNode> nodes = onlineNodes.get(node.getClass());
        if (nodes != null) {
            nodes.remove(node.getPos());
        }
    }

    public Map<WorldPos, NetNode> getNodes(final Class<? extends NetNode> nodeClass) {
        return onlineNodes.computeIfAbsent(nodeClass, k -> new ConcurrentHashMap<>());
    }

    public boolean hasPerm(final EntityPlayer player, final UserPerm permission) {
        return userPerms.getOrDefault(player.getGameProfile().getId(), UserPerm.NONE).getLevel() >= permission.getLevel();
    }

    public void addUser(final EntityPlayer player, final UserPerm permission) {
        userPerms.put(player.getGameProfile().getId(), permission);
    }

    public NBTTagCompound writeToNBT() {
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("name", name);
        tag.setString("ownerName", ownerName);
        return tag;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}