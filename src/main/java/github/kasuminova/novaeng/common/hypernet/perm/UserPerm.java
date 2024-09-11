package github.kasuminova.novaeng.common.hypernet.perm;

public enum UserPerm {

    NONE(0),
    DEFAULT(1),
    ADMIN(2),
    OWNER(3);

    private final byte level;

    UserPerm(int level) {
        this.level = (byte) level;
    }

    public byte getLevel() {
        return level;
    }

}