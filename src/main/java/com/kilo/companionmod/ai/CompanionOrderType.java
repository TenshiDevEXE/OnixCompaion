package com.kilo.companionmod.ai;

public enum CompanionOrderType {
    FOLLOW("Seguir", "El compañero te sigue a todas partes"),
    STAY("Quedarse", "El compañero se queda quieto en su posición"),
    PATROL("Patrulla", "El compañero patrulla un área alrededor de su hogar"),
    GUARD("Guarda", "El compañero protege al jugador y ataca amenazas cercanas"),
    FETCH("Recolección", "El compañero recoge ítems cercanos y los guarda en su inventario"),
    MINE("Minería", "El compañero mina bloques cercanos bajo órdenes"),
    EXPLORE("Exploración", "El compañero explora el área y marca zonas de interés"),
    BUILD("Construcción", "El compañero ayuda a colocar bloques según órdenes"),
    AGGRESSIVE("Agresivo", "El compañero ataca a cualquier entidad hostil que vea"),
    DEFENSIVE("Defensivo", "El compañero solo ataca si el jugador es atacado"),
    PASSIVE("Pasivo", "El compañero no ataca, solo sigue");

    public final String displayName;
    public final String description;

    CompanionOrderType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
