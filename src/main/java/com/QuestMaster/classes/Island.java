package com.QuestMaster.classes;

public enum Island {
    NONE(""),
    CRIMSON_ISLE("Crimson Isle"),
    CRYSTAL_HOLLOWS("Crystal Hollows"),
    DEEP_CAVERNS("Deep Caverns"),
    CATACOMBS("Catacombs"),
    DUNGEON_HUB("Dungeon Hub"),
    DWARVEN_MINES("Dwarven Mines"),
    END("The End"),
    FARMING_ISLANDS("The Farming Islands"),
    GOLD_MINE("Gold Mine"),
    HUB("Hub"),
    INSTANCED("Instanced"),
    JERRY_WORKSHOP("Jerry's Workshop"),
    PRIVATE_ISLAND("Private Island"),
    PARK("The Park"),
    SPIDERS_DEN("Spider's Den"),
    GARDEN("Garden");

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