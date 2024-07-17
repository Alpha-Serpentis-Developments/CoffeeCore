package dev.alphaserpentis.coffeecore.data.entity;

/**
 * Empty class to be used as a common superclass for all entity data classes.
 */
public abstract class EntityData {
    /**
     * Creates a new instance of the entity data.
     * @return The subclass of {@link EntityData} to create.
     */
    public abstract EntityData createNewEntityData();
}
