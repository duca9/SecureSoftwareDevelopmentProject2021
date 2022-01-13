package com.zuehlke.securesoftwaredevelopment.config;

public class EntityChanged {
    private final String type;
    private final String before;
    private final String after;

    public EntityChanged(String type, String before, String after) {
        this.type = type;
        this.before = before;
        this.after = after;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "type='" + type + '\'' +
                ", before='" + before + '\'' +
                ", after='" + after + '\'' +
                '}';
    }
}
