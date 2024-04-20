package dev.alphaserpentis.coffeecore.helper

/**
 * Helper class to validate objects and throw exceptions if they are not valid. To be used primarily in Java classes.
 */
object Validate {
    /**
     * Throws a [NullPointerException] if the provided value is null.
     * @param value The value to check.
     * @return The value if it is not null.
     */
    @JvmStatic
    fun <T> throwOnNull(value: T?): T {
        return value ?: throw NullPointerException("Provided value is null!")
    }

    /**
     * Throws a [NullPointerException] if any of the provided values are null.
     * @param values The values to check.
     * @return The values if they are not null.
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <T> throwOnNull(vararg values: T?): Array<out T> {
        values.forEachIndexed { index, it ->
            if (it == null)
                throw NullPointerException("At index $index, provided value is null!")
        }
        return values as Array<out T>
    }

    /**
     * Throws an [IllegalArgumentException] if the provided [Collection] is empty or null
     * @param value The collection to check.
     * @return The collection if it is not empty or null
     */
    @JvmStatic
    fun <T, C : Collection<T>> throwOnEmpty(value: C?): C {
        if (value.isNullOrEmpty())
            throw IllegalArgumentException("Provided collection is empty!")
        else
            return value
    }

    /**
     * Throws an [IllegalArgumentException] if the provided [String] is blank or null
     * @param value The string to check.
     * @return The string if it is not blank or null
     */
    @JvmStatic
    fun throwOnBlank(value: String?): String {
        if (value.isNullOrBlank())
            throw IllegalArgumentException("Provided string is blank!")
        else
            return value
    }
}