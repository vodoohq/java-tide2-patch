//? if fabric {
package com.li64.tide.compat.seasons;

import com.li64.tide.compat.OptionalModBridge;
import net.minecraft.world.level.Level;

public class FabricSeasonsCompat {
    public static Season getSeason(Level level) {
        Object season = OptionalModBridge.callStatic("io.github.lucaargolo.seasons.FabricSeasons", "getCurrentSeason", level);
        return switch (season == null ? "SPRING" : season.toString().toUpperCase(java.util.Locale.ROOT)) {
            case "SUMMER" -> Season.SUMMER;
            case "FALL", "AUTUMN" -> Season.FALL;
            case "WINTER" -> Season.WINTER;
            default -> Season.SPRING;
        };
    }
}
//?}
