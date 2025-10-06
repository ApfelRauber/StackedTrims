package io.github.apfelrauber.stacked_trims.component;

import com.mojang.serialization.Codec;
import io.github.apfelrauber.stacked_trims.StackedTrims;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.util.List;

public class StackedTrimsComponents {

    public static DataComponentType<List<ArmorTrim>> STACKED_TRIMS;

    public static DataComponentType<List<ArmorTrim>> createStackedTrimsComponent() {
        return DataComponentType.<List<ArmorTrim>>builder()
                .persistent(Codec.list(ArmorTrim.CODEC))
                .networkSynchronized(ByteBufCodecs.collection(
                        java.util.ArrayList::new,
                        ArmorTrim.STREAM_CODEC
                ))
                .build();
    }
}