package util

import kotlinx.serialization.json.*
import java.time.format.DateTimeFormatter

inline fun <reified T> JsonObject?.require(key: String): T {
    return when(T::class) {
        Int::class -> this?.get(key)?.jsonPrimitive?.int as? T
        String::class -> this?.get(key)?.jsonPrimitive?.content as? T
        Long::class -> this?.get(key)?.jsonPrimitive?.long as? T
        else -> error("Not supported class ${T::class.java.canonicalName}")
    } ?: error("No key in json $key")
}