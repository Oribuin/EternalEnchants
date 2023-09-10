package xyz.oribuin.eternalenchants.enchant;

public enum Priority {
    LOWEST(5),
    LOW(4),
    NORMAL(3),
    HIGH(2),
    HIGHEST(1);

    private final int order;

    Priority(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

}
