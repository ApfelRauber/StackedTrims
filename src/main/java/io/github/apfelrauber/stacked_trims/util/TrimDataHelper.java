package io.github.apfelrauber.stacked_trims.util;

import com.mojang.serialization.DataResult;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 装饰数据处理工具类
 */
public class TrimDataHelper {
    
    private static final String TRIMS_KEY = "StackedTrims";
    private static final String VANILLA_TRIM_KEY = "Trim";
    
    /**
     * 获取物品上的所有装饰
     */
    public static Optional<List<ArmorTrim>> getTrims(RegistryAccess registryAccess, ItemStack stack) {
        if (!canHaveTrims(stack)) {
            return Optional.empty();
        }
        
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TRIMS_KEY)) {
            return Optional.empty();
        }
        
        ListTag trimsList = tag.getList(TRIMS_KEY, Tag.TAG_COMPOUND);
        if (trimsList.isEmpty()) {
            return Optional.empty();
        }
        
        List<ArmorTrim> trims = new ArrayList<>();
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);
        
        for (Tag trimTag : trimsList) {
            DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(ops, trimTag);
            result.result().ifPresent(trims::add);
        }
        
        return trims.isEmpty() ? Optional.empty() : Optional.of(trims);
    }
    
    /**
     * 添加装饰到物品
     */
    public static boolean addTrim(RegistryAccess registryAccess, ItemStack stack, ArmorTrim trim, int maxStack, boolean allowDuplicates) {
        if (!canHaveTrims(stack)) {
            return false;
        }
        
        CompoundTag tag = stack.getOrCreateTag();
        
        // 迁移旧的单个装饰数据
        migrateVanillaTrim(registryAccess, tag);
        
        ListTag trimsList = tag.getList(TRIMS_KEY, Tag.TAG_COMPOUND);
        
        // 检查堆叠限制
        if (trimsList.size() >= maxStack) {
            return false;
        }
        
        // 检查重复装饰
        if (!allowDuplicates && hasDuplicateTrim(registryAccess, trimsList, trim)) {
            return false;
        }
        
        // 添加新装饰
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);
        DataResult<Tag> result = ArmorTrim.CODEC.encodeStart(ops, trim);
        
        if (result.result().isPresent()) {
            trimsList.add(result.result().get());
            tag.put(TRIMS_KEY, trimsList);
            
            // 更新原版装饰数据以保持兼容性
            tag.put(VANILLA_TRIM_KEY, result.result().get());
            return true;
        }
        
        return false;
    }
    
    /**
     * 移除指定数量的装饰
     */
    public static boolean removeTrims(ItemStack stack, int count) {
        if (!canHaveTrims(stack)) {
            return false;
        }
        
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TRIMS_KEY)) {
            return false;
        }
        
        ListTag trimsList = tag.getList(TRIMS_KEY, Tag.TAG_COMPOUND);
        if (trimsList.isEmpty()) {
            return false;
        }
        
        // 移除指定数量的装饰（从末尾开始）
        int toRemove = Math.min(count, trimsList.size());
        for (int i = 0; i < toRemove; i++) {
            trimsList.remove(trimsList.size() - 1);
        }
        
        if (trimsList.isEmpty()) {
            tag.remove(TRIMS_KEY);
            tag.remove(VANILLA_TRIM_KEY);
        } else {
            tag.put(TRIMS_KEY, trimsList);
            // 更新原版装饰数据为最后一个装饰
            tag.put(VANILLA_TRIM_KEY, trimsList.get(trimsList.size() - 1));
        }
        
        return true;
    }
    
    /**
     * 获取装饰数量
     */
    public static int getTrimCount(ItemStack stack) {
        if (!canHaveTrims(stack)) {
            return 0;
        }
        
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TRIMS_KEY)) {
            return 0;
        }
        
        return tag.getList(TRIMS_KEY, Tag.TAG_COMPOUND).size();
    }
    
    /**
     * 获取最后一个装饰（用于原版兼容性）
     */
    public static Optional<ArmorTrim> getLastTrim(RegistryAccess registryAccess, ItemStack stack) {
        Optional<List<ArmorTrim>> trims = getTrims(registryAccess, stack);
        if (trims.isPresent() && !trims.get().isEmpty()) {
            List<ArmorTrim> trimList = trims.get();
            return Optional.of(trimList.get(trimList.size() - 1));
        }
        return Optional.empty();
    }
    
    /**
     * 检查物品是否可以拥有装饰
     */
    private static boolean canHaveTrims(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ItemTags.TRIMMABLE_ARMOR);
    }
    
    /**
     * 迁移原版装饰数据
     */
    private static void migrateVanillaTrim(RegistryAccess registryAccess, CompoundTag tag) {
        if (tag.contains(VANILLA_TRIM_KEY) && !tag.contains(TRIMS_KEY)) {
            ListTag trimsList = new ListTag();
            trimsList.add(tag.get(VANILLA_TRIM_KEY));
            tag.put(TRIMS_KEY, trimsList);
        }
    }
    
    /**
     * 检查是否存在重复装饰
     */
    private static boolean hasDuplicateTrim(RegistryAccess registryAccess, ListTag trimsList, ArmorTrim newTrim) {
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);
        
        for (Tag trimTag : trimsList) {
            DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(ops, trimTag);
            if (result.result().isPresent()) {
                ArmorTrim existingTrim = result.result().get();
                if (existingTrim.material().equals(newTrim.material()) && 
                    existingTrim.pattern().equals(newTrim.pattern())) {
                    return true;
                }
            }
        }
        
        return false;
    }
}