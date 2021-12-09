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

package com.terraforged.mod.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.terraforged.mod.TerraForged;
import com.terraforged.mod.util.Init;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ModRegistries extends Init {
    public static final ModRegistries INSTANCE = new ModRegistries();

    private final List<Holder<?>> holders = new ArrayList<>();
    private final Map<ResourceKey<? extends Registry<?>>, MappedRegistry<?>> registries = new IdentityHashMap<>();

    @Override
    protected void doInit() {}

    protected record Holder<T>(Registry<T> registry, Codec<T> direct) {
        public ResourceKey<? extends Registry<T>> key() {
            return registry.key();
        }
    }

    public static void commit() {
        INSTANCE.init();
    }

    public static List<Holder<?>> getHolders() {
        return INSTANCE.holders;
    }

    public static <T> void createRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
        if (INSTANCE.isDone()) {
            TerraForged.LOG.warn("Attempted to register extension after init: {}", key);
            return;
        }

        var registry = new MappedRegistry<>(key, Lifecycle.stable());
        var holder = new Holder<>(registry, codec);

        INSTANCE.holders.add(holder);
        INSTANCE.registries.put(key, registry);
    }

    public static <T> ResourceKey<T> register(ResourceKey<Registry<T>> registryKey, String name, T value) {
        var registry = getRegistry(registryKey);
        var entryKey = ResourceKey.create(registryKey, TerraForged.location(name));
        registry.register(entryKey, value, Lifecycle.stable());
        return entryKey;
    }

    @SuppressWarnings("unchecked")
    private static <T> MappedRegistry<T> getRegistry(ResourceKey<Registry<T>> key) {
        var registry = INSTANCE.registries.get(key);
        if (registry == null) throw new Error("Missing registry: " + key);
        return (MappedRegistry<T>) registry;
    }
}