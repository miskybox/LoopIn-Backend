package com.loopinback.loopinback.model;

public enum Category {
    ONLINE("Evento en línea"),
    PRESENCIAL("Evento presencial");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.name() + " (" + this.description + ")";
    }

    /**
     * 
     * @param value El valor a convertir
     * @return La categoría correspondiente o null si no es válida
     */
    public static Category fromString(String value) {
        if (value == null)
            return null;

        try {
            return Category.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}