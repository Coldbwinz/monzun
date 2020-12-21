package com.example.monzun.enums;

/**
 * Значение полиморфных типов источников у прикрепленных файлов
 */
public enum AttachmentPolytableTypeConstants {
    STARTUP("startup"),
    TRACKING("tracking"),
    WEEK_REPORT("week_report"),
    TASK("task");

    private final String type;

    AttachmentPolytableTypeConstants(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
