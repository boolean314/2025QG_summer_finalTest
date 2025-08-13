package com.example.pmp.util.Decryption

import android.util.Log
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec

object Decryption {
    private const val AES_TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    private const val IV_LENGTH = 16

    /**
     * 解密混合加密数据
     * @param encryptedData Base64(IV + AES密文)
     * @param encryptedKey  Base64(RSA加密的AES密钥)
     * @param privateKeyStr Base64编码的RSA私钥（支持PEM格式）
     * @return 解密后的明文JSON字符串
     */
    fun decrypt(
        encryptedData: String,
        encryptedKey: String,
        privateKeyStr: String
    ): String {
        // 1. 解密AES密钥
        val aesKeyBase64 = String(rsaDecrypt(Base64.getDecoder().decode(encryptedKey), privateKeyStr))
        val aesKeyBytes = Base64.getDecoder().decode(aesKeyBase64) // 得到32字节
        val secretKey = SecretKeySpec(aesKeyBytes, "AES")

        // 2. 解析IV和密文
        val combined = Base64.getDecoder().decode(encryptedData)
        val iv = combined.copyOfRange(0, IV_LENGTH)
        val cipherText = combined.copyOfRange(IV_LENGTH, combined.size)

        // 3. AES解密
        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val plainBytes = cipher.doFinal(cipherText)
        return String(plainBytes, Charsets.UTF_8)
    }

    /**
     * 用RSA私钥解密AES密钥
     */
    private fun rsaDecrypt(data: ByteArray, privateKeyStr: String): ByteArray {
        // 处理PEM格式私钥
        val processedKey = privateKeyStr
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\n", "")
            .replace("\r", "")
            .trim()
        val keyBytes = Base64.getDecoder().decode(processedKey)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val privateKey: PrivateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec)

        val oaepParams = OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT
        )
        val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams)
        return cipher.doFinal(data)
    }
}