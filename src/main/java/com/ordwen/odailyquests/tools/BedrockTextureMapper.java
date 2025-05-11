package com.ordwen.odailyquests.tools;

import org.bukkit.Material;

public class BedrockTextureMapper {

    public static String getTexturePath(Material material) {
        String base = "textures/items/";
        String name = material.name().toLowerCase();
        String filename;

        // Handle special cases first
        switch(material) {
            case GOLDEN_APPLE:
                filename = "apple_golden.png";
                break;
            case ENCHANTED_BOOK:
                filename = "book_enchanted.png";
                break;
            case WRITTEN_BOOK:
                filename = "book_written.png";
                break;
            case BOW:
                filename = "bow_standby.png";
                break;
            case CROSSBOW:
                filename = "crossbow_standby.png";
                break;
            case OAK_DOOR:
                filename = "door_wood.png";
                break;
            case IRON_DOOR:
                filename = "door_iron.png";
                break;
            case ACACIA_BOAT:
            case BIRCH_BOAT:
            case DARK_OAK_BOAT:
            case JUNGLE_BOAT:
            case SPRUCE_BOAT:
                filename = "boat_" + name.replace("_boat", "") + ".png";
                break;
            case MAP:
                filename = "map_empty.png";
                break;
            case FILLED_MAP:
                filename = "map_filled.png";
                break;
            case TOTEM_OF_UNDYING:
                filename = "totem.png";
                break;
            case PLAYER_HEAD:
                filename = "skull_pottery_sherd.png";  // Not exact, just example
                break;
            default:
                filename = handleDefaultCases(name, material);
        }

        return base + filename;
    }

    private static String handleDefaultCases(String name, Material material) {
        // Handle colored items
        if (name.endsWith("_bed")) {
            return "bed_" + name.replace("_bed", "") + ".png";
        }
        if (name.contains("_dye")) {
            return "dye_powder_" + name.replace("_dye", "") + ".png";
        }
        if (name.endsWith("_sign")) {
            return "sign_" + name.replace("_sign", "") + ".png";
        }
        if (name.endsWith("_door")) {
            return "door_" + name.replace("_door", "") + ".png";
        }
        if (name.endsWith("_boat")) {
            return "boat_" + name.replace("_boat", "") + ".png";
        }

        // Handle special patterns
        if (name.startsWith("music_disc_")) {
            return "music_disc_" + name.split("_")[2] + ".png";
        }

        return name + ".png";
    }
}