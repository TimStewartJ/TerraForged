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

package com.terraforged.mod.worldgen.biome.viability;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.mod.worldgen.Generator;
import com.terraforged.mod.worldgen.terrain.TerrainData;

public record SlopeViability(float normalize, float max) implements Viability {
    public static final DataSpec<SlopeViability> SPEC = DataSpec.builder(
                    "Slope",
            SlopeViability.class,
            (data, spec, context) -> new SlopeViability(
                    spec.get("normalize", data, DataValue::asFloat),
                    spec.get("max", data, DataValue::asFloat)))
            .add("normalize", 1F, SlopeViability::normalize)
                    .add("max", 1F, SlopeViability::max)
            .build();

    @Override
    public float getFitness(int x, int z, TerrainData data, Generator generator) {
        float norm = normalize * getScaler(generator);
        float gradient = data.getGradient(x, z, norm);
        return Viability.getFallOff(gradient, max);
    }
}