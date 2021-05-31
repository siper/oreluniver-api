package util

data class TimedEntry<T>(val data: T, val updatedAt: Long)

class TimedCache<K, V>(private val itemLifetime: Long) {
    private val cache = mutableMapOf<K, TimedEntry<V>>()

    operator fun get(key: K): V? {
        val value = cache[key] ?: return null
        if (value.updatedAt + itemLifetime < System.currentTimeMillis()) {
            cache.remove(key)
            return null
        }
        return value.data
    }

    operator fun set(key: K, value: V?) {
        if (value == null) {
            cache.remove(key)
            return
        }
        cache[key] = TimedEntry(value, System.currentTimeMillis())
    }
}