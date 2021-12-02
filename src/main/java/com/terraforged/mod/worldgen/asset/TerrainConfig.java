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

import com.mojang.serialization.Codec;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.spec.DataSpecs;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.engine.module.Ridge;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.mod.codec.SpecCodec;
import com.terraforged.mod.registry.DataSeedable;
import com.terraforged.mod.util.map.WeightMap;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseSpec;

public record TerrainConfig(Terrain type, float weight, Module heightmap) implements DataSeedable<TerrainConfig>, WeightMap.Weighted {
    public static final TerrainConfig NONE = new TerrainConfig(TerrainType.NONE, 0, Source.ZERO);

    public static final DataSpec<TerrainConfig> SPEC = DataSpec.builder(
            TerrainConfig.class,
            (data, spec, context) -> new TerrainConfig(
                    spec.get("terrain", data, TerrainConfig::decodeType),
                    spec.get("weight", data, DataValue::asFloat),
                    spec.get("noise", data, Module.class)))
            .add("terrain", defaultType(), TerrainConfig::encodeType)
            .add("weight", 0.0, TerrainConfig::weight)
            .addObj("noise", Module.class, TerrainConfig::heightmap)
            .build();

    public static final Codec<TerrainConfig> CODEC = SpecCodec.of(SPEC);

    @Override
    public DataSpec<TerrainConfig> getSpec() {
        return SPEC;
    }

    private static String defaultType() {
        return TerrainType.NONE.getName();
    }

    private static String encodeType(TerrainConfig config) {
        return config.type.getName();
    }

    private static Terrain decodeType(DataValue value) {
        return TerrainType.get(value.asString());
    }

    static {
        NoiseSpec.init();
        DataSpecs.register(SPEC);
        DataSpecs.register(Ridge.spec());
    }
}
