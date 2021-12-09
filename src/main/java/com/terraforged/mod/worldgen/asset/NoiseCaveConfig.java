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

package com.terraforged.mod.worldgen.asset;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.terraforged.mod.util.seed.ContextSeedable;
import com.terraforged.mod.worldgen.noise.NoiseCodec;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Function;
import java.util.function.Supplier;

public class NoiseCaveConfig implements ContextSeedable<NoiseCaveConfig>  {
    public static final Codec<NoiseCaveConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Biome.CODEC.fieldOf("biome").forGetter(c -> c.biome),
            NoiseCodec.CODEC.fieldOf("elevation").forGetter(c -> c.elevation),
            NoiseCodec.CODEC.fieldOf("shape").forGetter(c -> c.shape),
            NoiseCodec.CODEC.fieldOf("floor").forGetter(c -> c.floor),
            Codec.INT.fieldOf("size").forGetter(c -> c.size),
            Codec.INT.optionalFieldOf("min_y", -32).forGetter(c -> c.minY),
            Codec.INT.fieldOf("max_y").forGetter(c -> c.maxY)
    ).apply(instance, NoiseCaveConfig::new));

    private final Supplier<Biome> biome;
    private final Module elevation;
    private final Module shape;
    private final Module floor;
    private final int size;
    private final int minY;
    private final int maxY;
    private final int rangeY;

    public NoiseCaveConfig(Supplier<Biome> biome, Module elevation, Module shape, Module floor, int size, int minY, int maxY) {
        this.biome = biome;
        this.elevation = elevation;
        this.shape = shape;
        this.floor = floor;
        this.size = size;
        this.minY = minY;
        this.maxY = maxY;
        this.rangeY = maxY - minY;
    }

    @Override
    public NoiseCaveConfig withSeed(long seed) {
        var elevation = withSeed(seed, this.elevation, Module.class);
        var shape = withSeed(seed, this.shape, Module.class);
        var floor = withSeed(seed, this.floor, Module.class);
        return new NoiseCaveConfig(biome, elevation, shape, floor, size, minY, maxY);
    }

    public Biome getBiome() {
        return biome.get();
    }

    public int getHeight(int x, int z) {
        return getScaleValue(x, z, minY, rangeY, elevation);
    }

    public int getCavernSize(int x, int z) {
        return getScaleValue(x, z, 0, size, shape);
    }

    public int getFloorDepth(int x, int z, int size) {
        return getScaleValue(x, z, 0, size, floor);
    }

    @Override
    public String toString() {
        return "NoiseCaveConfig{" +
                "biome=" + biome +
                ", elevation=" + elevation +
                ", shape=" + shape +
                ", floor=" + floor +
                ", size=" + size +
                ", minY=" + minY +
                ", maxY=" + maxY +
                ", rangeY=" + rangeY +
                '}';
    }

    private static int getScaleValue(int x, int z, int min, int range, Module noise) {
        if (range <= 0) return 0;

        return min + NoiseUtil.floor(noise.getValue(x, z) * range);
    }

    public static NoiseCaveConfig create0(int seed, ResourceKey<Biome> biome, Function<ResourceKey<Biome>, Biome> registry) {
        return new NoiseCaveConfig(
                Suppliers.memoize(() -> registry.apply(biome)),
                Source.simplex(seed += 233, 300, 2).scale(0.8).bias(0.1),
                Source.ridge(seed += 678145, 150, 3).clamp(0.8, 1.0).map(0, 1),
                Source.simplex(seed += 98673, 20, 2).clamp(0.0, 0.25).map(0, 1),
                20, -32, 150
        );
    }

    public static NoiseCaveConfig create1(int seed, ResourceKey<Biome> biome, Function<ResourceKey<Biome>, Biome> registry) {
        return new NoiseCaveConfig(
                Suppliers.memoize(() -> registry.apply(biome)),
                Source.simplex(seed += 153, 300, 2).scale(0.8).bias(0.1),
                Source.ridge(seed += 13, 150, 3).clamp(0.8, 1.0).map(0, 1),
                Source.simplex(seed += 43465, 20, 2).clamp(0.0, 0.25).map(0, 1),
                20, 64, 200
        );
    }
}