/*
 * MIT License
 *
 * Copyright (c) 2021 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.terraforged.mod.worldgen.biome;

import com.terraforged.engine.world.biome.type.BiomeType;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class BiomeUtils {
    public static List<Biome> getOverworldBiomes(RegistryAccess access) {
        return getOverworldBiomes(access.registryOrThrow(Registry.BIOME_REGISTRY));
    }

    public static List<Biome> getOverworldBiomes(Registry<Biome> biomes) {
        var sorter = getBiomeSorter(biomes);
        return MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(biomes).possibleBiomes().stream()
                .sorted(sorter)
                .toList();
    }

    public static Comparator<Biome> getBiomeSorter(Registry<Biome> biomes) {
        return (o1, o2) -> {
            var k1 = biomes.getKey(o1);
            var k2 = biomes.getKey(o2);
            Objects.requireNonNull(k1);
            Objects.requireNonNull(k2);
            return k1.compareTo(k2);
        };
    }

    public static BiomeType getType(Biome biome) {
        return switch (biome.getBiomeCategory()) {
            case MESA, DESERT -> BiomeType.DESERT;
            case PLAINS -> getByTemp(biome, BiomeType.TUNDRA, BiomeType.GRASSLAND, BiomeType.STEPPE);
            case TAIGA -> getByTemp(biome, BiomeType.TUNDRA, BiomeType.TAIGA);
            case ICY -> BiomeType.TUNDRA;
            case SAVANNA -> BiomeType.SAVANNA;
            case JUNGLE -> BiomeType.TROPICAL_RAINFOREST;
            case FOREST -> getByRain(biome, BiomeType.TEMPERATE_RAINFOREST, BiomeType.TEMPERATE_FOREST);
            default -> BiomeType.ALPINE;
        };
    }

    public static BiomeType getByRain(Biome biome, BiomeType wetter, BiomeType dryer) {
        return biome.getDownfall() > 0.66 ? wetter : dryer;
    }

    public static BiomeType getByTemp(Biome biome, BiomeType colder, BiomeType warmer) {
        return biome.getPrecipitation() == Biome.Precipitation.SNOW ? colder : warmer;
    }

    public static BiomeType getByTemp(Biome biome, BiomeType cold, BiomeType temperate, BiomeType hot) {
        if (biome.getPrecipitation() == Biome.Precipitation.SNOW) return cold;
        if (biome.getPrecipitation() == Biome.Precipitation.NONE || biome.getBaseTemperature() > 1.0) return hot;
        return temperate;
    }
}