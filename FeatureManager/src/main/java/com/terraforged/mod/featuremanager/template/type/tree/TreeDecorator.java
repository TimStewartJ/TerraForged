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

package com.terraforged.mod.featuremanager.template.type.tree;


import com.terraforged.mod.featuremanager.template.decorator.Decorator;
import com.terraforged.mod.featuremanager.util.DummySet;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class TreeDecorator implements Decorator<TreeDecoratorBuffer> {

    private static final DummySet<BlockPos> DUMMY_SET = DummySet.get();

    private final net.minecraft.world.gen.treedecorator.TreeDecorator decorator;
    private final net.minecraft.world.gen.treedecorator.TreeDecorator modifiedDecorator;

    public TreeDecorator(net.minecraft.world.gen.treedecorator.TreeDecorator decorator, net.minecraft.world.gen.treedecorator.TreeDecorator modifiedDecorator) {
        this.decorator = decorator;
        this.modifiedDecorator = modifiedDecorator;
    }

    public net.minecraft.world.gen.treedecorator.TreeDecorator getDecorator(boolean modified) {
        return modified ? modifiedDecorator : decorator;
    }

    @Override
    public void apply(TreeDecoratorBuffer buffer, Random random, boolean modified) {
        if (buffer.getLogs().isEmpty() || buffer.getLeaves().isEmpty()) {
            return;
        }

        getDecorator(modified).place(
                buffer.getDelegate(),
                random,
                buffer.getLogs(),
                buffer.getLeaves(),
                // used to track blocks added by the decorator - TF doesn't need it
                TreeDecorator.DUMMY_SET,
                buffer.getBounds()
        );
    }
}
