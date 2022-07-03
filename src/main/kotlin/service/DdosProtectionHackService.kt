package service

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.date.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class DdosProtectionHackService {
    private val toNumbersRegex = Regex("toNumbers\\(\\\"(.*?)\\\"\\)")
    private var ddosCookie: Cookie? = null

    suspend fun handleDdosProtection(request: HttpRequestBuilder, sender: Sender): HttpRequestBuilder {
        val newRequest = request.applyCookie(ddosCookie)
        val originalCall = sender.execute(newRequest)
        val body = originalCall.response.bodyAsText()
        return if (body.contains("toNumbers") || body.isEmpty()) {
            ddosCookie = getDecryptedCookie(body)
            newRequest.applyCookie(ddosCookie)
        } else {
            newRequest
        }
    }

    private fun HttpRequestBuilder.applyCookie(cookie: Cookie?): HttpRequestBuilder {
        if (cookie == null) {
            return this
        }
        return this.apply {
            cookie(
                name = cookie.name,
                value = cookie.value,
                expires = cookie.expires,
                path = cookie.path,
                domain = cookie.domain
            )
        }
    }

    private fun getDecryptedCookie(body: String): Cookie {
        val results = toNumbersRegex
            .findAll(body)
            .toList()
            .map { formatDecryptionData(it.value) }
        require(results.size == 3)
        return Cookie(decrypt(results[0], results[1], results[2]))
    }

    private fun formatDecryptionData(value: String): String {
        return value
            .replace("toNumbers(", "")
            .replace("\"", "")
            .replace(")", "")
    }

    private fun decrypt(a: String, b: String, c: String): String {
        val cipherBytes: ByteArray = hexStringToByteArray(c)
        val iv: ByteArray = hexStringToByteArray(b)
        val keyBytes: ByteArray = hexStringToByteArray(a)

        val aesKey: SecretKey = SecretKeySpec(keyBytes, "AES")

        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, aesKey, IvParameterSpec(iv))

        val result = cipher.doFinal(cipherBytes)
        return result.toHexString()
    }

    private fun hexStringToByteArray(value: String): ByteArray {
        return value
            .chunked(2)
            .map {
                it
                    .toUByte(16)
                    .toByte()
            }
            .toByteArray()
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

    data class Cookie(
        val value: String,
        val name: String = "BPC",
        val expires: GMTDate = GMTDate(2145916555),
        val path: String = "/",
        val domain: String = "oreluniver.ru"
    )
}