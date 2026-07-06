package net.spudacious5705.shops.util.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class DeferredHolder<R, T extends R> implements Supplier<T> {
    private final ResourceKey<R> key;
    private T value;

    DeferredHolder(ResourceKey<R> key) {
        this.key = key;
    }

    public ResourceKey<R> getKey() {
        return key;
    }

    public ResourceLocation getId() {
        return key.location();
    }

    void bind(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
