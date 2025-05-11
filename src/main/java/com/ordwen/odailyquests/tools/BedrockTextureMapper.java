package com.ordwen.odailyquests.tools;

import org.bukkit.Material;

public class BedrockTextureMapper {

    public static String getTexturePath(Material material) {
        boolean isBlock = material.isBlock();
        String base = isBlock ? "textures/blocks/" : "textures/items/";
        if (material == Material.CAKE) { base = "textures/items/";}
        String name = material.name().toLowerCase();
        String filename;

        // Handle special cases that differ between blocks and items
        if (isBlock) {
            filename = handleBlockCases(name, material);
        } else {
            filename = handleItemCases(name, material);
        }

        return base + filename;
    }



    private static String handleItemCases(String name, Material material) {
        // Existing item handling from previous implementation
        switch(material) {
            case CAKE:
                return "cake_top.png"; // Same texture for cake item in Bedrock
            case GOLDEN_APPLE:
                return "apple_golden.png";
            case ENCHANTED_BOOK:
                return "book_enchanted.png";
            case WRITTEN_BOOK:
                return "book_written.png";
            case BOW:
                return "bow_standby.png";
            case CROSSBOW:
                return "crossbow_standby.png";
            case MAP:
                return "map_empty.png";
            case FILLED_MAP:
                return "map_filled.png";
            case TOTEM_OF_UNDYING:
                return "totem.png";
        }

        if (name.endsWith("_boat")) {
            return "boat_" + name.replace("_boat", "") + ".png";
        }

        return name + ".png";
    }

    private static String handleBlockCases(String name, Material material) {
        // Block-specific transformations
        switch(material) {
            case WHITE_BED:
            case BLACK_BED:
            case BLUE_BED:
            case RED_BED:
            case GREEN_BED:
            case PURPLE_BED:
            case YELLOW_BED:
            case BROWN_BED:
            case CYAN_BED:
            case LIGHT_BLUE_BED:
            case LIME_BED:
            case MAGENTA_BED:
            case PINK_BED:
            case GRAY_BED:
            case LIGHT_GRAY_BED:
                return "bed_" + name.replace("_bed", "") + ".png";
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case CRIMSON_DOOR:
            case WARPED_DOOR:
            case CHERRY_DOOR:
            case MANGROVE_DOOR:
                return "door_" + name.replace("_door", "") + ".png";
            case OAK_SIGN:
            case BIRCH_SIGN:
            case JUNGLE_SIGN:
            case SPRUCE_SIGN:
            case DARK_OAK_SIGN:
            case ACACIA_SIGN:
            case CRIMSON_SIGN:
            case WARPED_SIGN:
            case CHERRY_SIGN:
            case MANGROVE_SIGN:
                return "sign_" + name.replace("_sign", "") + ".png";
        }

        if (name.startsWith("potted_")) {
            return "flower_pot_" + name.replace("potted_", "") + ".png";
        }
        if (name.endsWith("_planks")) {
            return "planks_" + name.replace("_planks", "") + ".png";
        }
        if (name.endsWith("_log")) {
            return "log_" + name.replace("_log", "") + ".png";
        }

        // Add more block patterns here
        return name + ".png";
    }
}
