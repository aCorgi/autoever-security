package com.task.autoeversecurity.component

import com.task.autoeversecurity.property.Aes256Properties
import com.task.autoeversecurity.util.Constants.AES_ALGORITHM
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class Aes256EncryptionManager(
    private val aes256Properties: Aes256Properties,
) {
    fun encrypt(plainText: String): String {
        val keySpec = createKeySpec()
        val iv = generateIv()
        val cipher = initCipher(Cipher.ENCRYPT_MODE, keySpec, iv)

        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return encodeWithIv(iv, encrypted)
    }

    fun decrypt(encryptedText: String): String {
        val (iv, encrypted) = decodeAndSplit(encryptedText)
        val keySpec = createKeySpec()
        val cipher = initCipher(Cipher.DECRYPT_MODE, keySpec, iv)

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }

    private fun createKeySpec(): SecretKeySpec {
        val keyBytes = aes256Properties.secretKey.toByteArray(Charsets.UTF_8)
        return SecretKeySpec(keyBytes, AES_ALGORITHM)
    }

    private fun generateIv(): ByteArray {
        return ByteArray(aes256Properties.ivSize).apply {
            SecureRandom().nextBytes(this)
        }
    }

    private fun initCipher(
        mode: Int,
        keySpec: SecretKeySpec,
        iv: ByteArray,
    ): Cipher {
        val cipher = Cipher.getInstance(aes256Properties.algorithm)
        cipher.init(mode, keySpec, IvParameterSpec(iv))
        return cipher
    }

    private fun encodeWithIv(
        iv: ByteArray,
        encrypted: ByteArray,
    ): String {
        val combined = iv + encrypted
        return Base64.getEncoder().encodeToString(combined)
    }

    private fun decodeAndSplit(encoded: String): Pair<ByteArray, ByteArray> {
        val combined = Base64.getDecoder().decode(encoded)
        val iv = combined.copyOfRange(0, aes256Properties.ivSize)
        val encrypted = combined.copyOfRange(aes256Properties.ivSize, combined.size)
        return iv to encrypted
    }
}
