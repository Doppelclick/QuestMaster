package com.QuestMaster.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Set;
import java.util.regex.Pattern;

public class SkyblockItemHandler {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(EnumMap.class, (InstanceCreator<EnumMap>) type -> {
                Type[] types = (((ParameterizedType) type).getActualTypeArguments());
                return new EnumMap((Class<?>) types[0]);
            })
            .registerTypeAdapterFactory(new GsonInitialisableTypeAdapter())
            .registerTypeAdapter(Pattern.class, new PatternAdapter())
            .create();

    public static class PetInfo {
        String type;
        Double exp;
        String tier;
        Boolean active = false;
        Boolean hideInfo = false;
        String helditem = null;
        int candyUsed = 0;
        String skin = null;
        String uuid = null;
    }

    public static String actualItemID(ItemStack item) {
        NBTTagCompound extraAttr = getExtraAttributes(item);
        if (item != null) {
            String id = getSkyBlockItemID(extraAttr);
            if (id != null) {
                switch (id) { //The following is from skytils under then GNU 3.0 license
                    case "PET":
                        if (extraAttr.getString("petInfo").startsWith("{")) {
                            PetInfo petInfo = GSON.fromJson(extraAttr.getString("petInfo"), PetInfo.class);
                            id = "PET-" + petInfo.type + "-" + petInfo.tier;
                        }
                        break;

                    case "ATTRIBUTE_SHARD":
                        if (extraAttr.hasKey("attributes")) {
                            NBTTagCompound attributes = extraAttr.getCompoundTag("attributes");
                            Set<String> attributeSet = attributes.getKeySet();
                            if (!attributeSet.isEmpty()) {
                                String attribute = attributeSet.iterator().next();
                                id = "ATTRIBUTE_SHARD-" + attribute.toUpperCase() + "-" + attributes.getInteger(attribute);
                            }
                        }
                        break;

                    case "ENCHANTED_BOOK":
                        if (extraAttr.hasKey("enchantments")) {
                            NBTTagCompound enchants = extraAttr.getCompoundTag("enchantments");
                            Set<String> enchantSet = enchants.getKeySet();
                            if (!enchantSet.isEmpty()) {
                                String enchant = enchantSet.iterator().next();
                                id = "ENCHANTED_BOOK-" + enchant.toUpperCase() + "-" + enchants.getInteger(enchant);
                            }
                        }
                        break;

                    case "POTION":
                        if (extraAttr.hasKey("potion") && extraAttr.hasKey("potion_level")) {
                            id = "POTION-" +
                                    extraAttr.getString("potion").toUpperCase() +
                                    "-" + extraAttr.getInteger("potion_level") +
                                    (extraAttr.hasKey("enhanced") ? "-ENHANCED" : "") +
                                    (extraAttr.hasKey("extended") ? "-EXTENDED" : "") +
                                    (extraAttr.hasKey("splash") ? "-SPLASH" : "");
                        }
                        break;

                    case "RUNE":
                        if (extraAttr.hasKey("runes")) {
                            NBTTagCompound runes = extraAttr.getCompoundTag("runes");
                            Set<String> runeSet = runes.getKeySet();
                            if (!runeSet.isEmpty()) {
                                String rune = runeSet.iterator().next();
                                id = "RUNE-" + rune.toUpperCase() + "-" + runes.getInteger(rune);
                            }
                        }
                        break;
                }
                return id;
            }
        }
        return "ERROR_ITEM";
    }

    public static String getSkyBlockItemID(ItemStack item) {
        if (item == null) return null;
        NBTTagCompound extraAttributes = getExtraAttributes(item);
        if (extraAttributes == null) return null;
        if (extraAttributes.hasKey("id", 8)) {
            return extraAttributes.getString("id");
        }
        return null;
    }

    public static String getSkyBlockItemID(NBTTagCompound extraAttributes) {
        if (extraAttributes != null) {
            if (extraAttributes.hasKey("id")) {
                String id = extraAttributes.getString("id");
                if (!id.isEmpty()) return id;
            }
        }
        return null;
    }

    public static NBTTagCompound getExtraAttributes(ItemStack item) {
        if (item == null) return null;
        if (!item.hasTagCompound()) return null;
        return item.getSubCompound("ExtraAttributes", false);
    }
}
