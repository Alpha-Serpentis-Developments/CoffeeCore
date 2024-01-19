package dev.alphaserpentis.coffeecore.data.entity;

/**
 * Represents an entity type.
 * @param id The entity type's id.
 * @param entityDataClass The entity type's data class to deserialize to.
 */
public record EntityType(String id, Class<?> entityDataClass) {

    public String getId() {
        return id;
    }

    public Class<?> getEntityDataClass() {
        return entityDataClass;
    }

    @Override
    public String toString() {
        return id;
    }
}
