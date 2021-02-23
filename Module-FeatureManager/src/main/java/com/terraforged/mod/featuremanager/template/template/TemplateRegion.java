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

package com.terraforged.mod.featuremanager.template.template;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TemplateRegion {

    private static final int SIZE = 1;

    private int centerX, centerZ;
    private int minX, minZ;
    private int maxX, maxZ;

    public TemplateRegion init(BlockPos pos) {
        centerX = pos.getX() >> 4;
        centerZ = pos.getZ() >> 4;
        minX = centerX - SIZE;
        minZ = centerZ - SIZE;
        maxX = centerX + SIZE;
        maxZ = centerZ + SIZE;
        return this;
    }

    public boolean containsBlock(IWorld world, BlockPos pos) {
        return containsChunk(world, pos.getX() >> 4, pos.getZ() >> 4);
    }

    public boolean containsChunk(IWorld world, int cx, int cz) {
        return cx >= minX && cx <= maxX && cz >= minZ && cz <= maxZ;
    }
}
