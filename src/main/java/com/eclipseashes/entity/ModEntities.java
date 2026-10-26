package com.eclipseashes.entity;

import com.eclipseashes.EclipseAshes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
    public static final ResourceKey<EntityType<?>> TRAINING_DUMMY_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, EclipseAshes.id("training_dummy"));

    public static final EntityType<TrainingDummyEntity> TRAINING_DUMMY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            TRAINING_DUMMY_KEY,
            EntityType.Builder.of(TrainingDummyEntity::new, MobCategory.MISC)
                    .sized(0.5F, 1.975F)
                    .build(TRAINING_DUMMY_KEY)
    );

    private ModEntities() {
    }

    public static void initialize() {
        // Loading this class registers the entity type.
    }
}
