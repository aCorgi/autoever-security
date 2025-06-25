package com.task.autoeversecurity.util

import com.task.autoeversecurity.property.AES256Properties
import com.task.autoeversecurity.util.Constants.AES_ALGORITHM
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class AES256Encryptor(
    private val aes256Properties: AES256Properties,
) {
    fun encrypt(plainText: String): String {
        val keySpec = SecretKeySpec(aes256Properties.secretKey.toByteArray(Charsets.UTF_8), AES_ALGORITHM)
        val iv = ByteArray(aes256Properties.ivSize)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(aes256Properties.algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val combined = iv + encrypted
        return Base64.getEncoder().encodeToString(combined)
    }

    fun decrypt(encryptedText: String): String {
        val combined = Base64.getDecoder().decode(encryptedText)
        val iv = combined.copyOfRange(0, aes256Properties.ivSize)
        val encrypted = combined.copyOfRange(aes256Properties.ivSize, combined.size)

        val keySpec = SecretKeySpec(aes256Properties.secretKey.toByteArray(Charsets.UTF_8), AES_ALGORITHM)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(aes256Properties.algorithm)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }
}
