package util

import org.junit.jupiter.api.Test

internal class DdosHackKtTest {

    @Test
    fun decrypt() {
        val a = "cff25bc810386f0926036b24552a684e"
        val b = "e2325c04da4e53abd3578739a397be97"
        val c = "9ffc3967ada3c7efa37ce706857814ee"
        println(decrypt(a, b, c))
    }
}