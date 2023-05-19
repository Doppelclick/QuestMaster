package com.QuestMaster.classes;

import java.io.Serializable;

public enum Island implements Serializable {
    NONE(""),
    CATACOMBS("Catacombs"),
    CRIMSON_ISLE("Crimson Isle"),
    CRYSTAL_HOLLOWS("Crystal Hollows"),
    DEEP_CAVERNS("Deep Caverns"),
    DUNGEON_HUB("Dungeon Hub"),
    DWARVEN_MINES("Dwarven Mines"),
    END("The End"),
    FARMING_ISLANDS("The Farming Islands"),
    GARDEN("Garden"),
    GOLD_MINE("Gold Mine"),
    HUB("Hub"),
    INSTANCED("Instanced"),
    JERRY_WORKSHOP("Jerry's Workshop"),
    PARK("The Park"),
    PRIVATE_ISLAND("Private Island"),
    RIFT("The Rift"),
    SPIDERS_DEN("Spider's Den");

    final String text;

    Island(String text) {
        this.text = text;
    }

    public static Island fromTab(String text) {
        for (Island island : Island.values()) {
            if (island.text.equalsIgnoreCase(text)) return island;
        }
        return NONE;
    }
}