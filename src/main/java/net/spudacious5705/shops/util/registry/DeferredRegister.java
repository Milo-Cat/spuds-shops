package net.spudacious5705.shops.util.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.inventory.MenuType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DeferredRegister<T> {
    protected final Registry<T> registry;
    protected final String namespace;
    protected final List<PendingRegistration<T>> pending = new ArrayList<>();

    protected DeferredRegister(Registry<T> registry, String namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    public static BlockRegister createBlocks(String modId) {
        return new BlockRegister(modId);
    }

    public static ItemRegister createItems(String modId) {
        return new ItemRegister(modId);
    }

    public static DeferredRegister<BlockEntityType<?>> createBlockEntities(String modId) {
        return new DeferredRegister<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, modId);
    }

    public static DeferredRegister<MenuType<?>> createMenuTypes(String modId) {
        return new DeferredRegister<>(BuiltInRegistries.MENU, modId);
    }

    public static DeferredRegister<CreativeModeTab> createCreativeTabs(String modId) {
        return new DeferredRegister<>(BuiltInRegistries.CREATIVE_MODE_TAB, modId);
    }

    public static <T> DeferredRegister<T> create(ResourceKey<Registry<T>> registryKey, String modId) {
        @SuppressWarnings("unchecked")
        Registry<T> resolved = (Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.location());
        return new DeferredRegister<>(resolved, modId);
    }

    public <V extends T> DeferredHolder<T, V> register(String path, Supplier<V> supplier) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        ResourceKey<T> key = ResourceKey.create(registry.key(), id);
        DeferredHolder<T, V> holder = new DeferredHolder<>(key);
        pending.add(new PendingRegistration<>(holder, supplier, id));
        return holder;
    }

    public void register(Object ignoredEventBus) {
        for (PendingRegistration<T> entry : pending) {
            entry.apply(registry);
        }
    }

    protected record PendingRegistration<T>(DeferredHolder<T, ?> holder, Supplier<?> supplier, ResourceLocation id) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        void apply(Registry<T> registry) {
            T value = (T) supplier.get();
            Registry.register(registry, id, value);
            ((DeferredHolder) holder).bind(value);
        }
    }

    public static final class BlockRegister extends DeferredRegister<Block> {
        public BlockRegister(String modId) {
            super(BuiltInRegistries.BLOCK, modId);
        }

        public <B extends Block> DeferredBlock<B> register(String path, Supplier<B> supplier) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
            ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
            DeferredBlock<B> holder = new DeferredBlock<>(key);
            pending.add(new PendingRegistration<>(holder, supplier, id));
            return holder;
        }
    }

    public static final class ItemRegister extends DeferredRegister<Item> {
        public ItemRegister(String modId) {
            super(BuiltInRegistries.ITEM, modId);
        }

        public <I extends Item> DeferredItem<I> register(String path, Supplier<I> supplier) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
            DeferredItem<I> holder = new DeferredItem<>(key);
            pending.add(new PendingRegistration<>(holder, supplier, id));
            return holder;
        }
    }
}
