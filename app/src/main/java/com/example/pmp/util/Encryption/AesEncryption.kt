package com.example.pmp.util.Encryption

import android.util.Log
import com.example.pmp.data.model.AesKey
import com.example.pmp.data.model.GlobalData
import org.json.JSONObject
import java.security.KeyFactory
import java.security.SecureRandom
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

object AesEncryption {
    private const val AES_TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val AES_KEY_SIZE = 256 // 服务端指定的密钥长度
    private const val IV_LENGTH = 16 // AES-CBC固定16字节IV
    private const val RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"

    /**
     * 加密包含两个字段的JSON数据
     * @param aesKey AES密钥（Base64编码）
     * @param serverPublicKey 服务端提供的RSA公钥（Base64编码，支持PEM格式带头部）
     * @return Pair(encryptedData, encryptedKey) 对应服务端接口的两个字段
     */
    fun encryptWithServerKey(
        aesKey: String,
        serverPublicKey: String
    ): Pair<String, String> {
        try {
            // 1. 构造JSON
            val json = JSONObject().apply {
                put("aesKey" , aesKey)
            }.toString()

            // 2. 生成32字节AES密钥
            val aesKey = generateAESKey().also {
                println("原始AES密钥长度: ${it.encoded.size}字节")
                println("原始AES密钥Base64: ${Base64.getEncoder().encodeToString(it.encoded)}")
            }

            // 3. 生成随机IV
            val iv = ByteArray(IV_LENGTH).apply { SecureRandom().nextBytes(this) }
            val ivSpec = IvParameterSpec(iv)

            // 4. AES加密
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec)
            val encrypted = cipher.doFinal(json.toByteArray(Charsets.UTF_8))

            // 5. IV+密文拼接，Base64编码
            val combined = iv + encrypted
            val encryptedData = Base64.getEncoder().encodeToString(combined)
                .replace("\n", "")

            // 6. RSA加密AES密钥
            val encryptedKey = rsaEncrypt(aesKey.encoded, serverPublicKey).also {
                println("RSA加密后的密钥Base64: $it")
            }

            GlobalData.Rsakey = encryptedKey
            Log.d("LoginEncryption", "RSA加密后的密钥: ${GlobalData.Rsakey}")

            return Pair(encryptedData, encryptedKey)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("加密失败: ${e.message}")
        }
    }

    /**
     * 生成AES密钥（按服务端指定长度）
     */
    private fun generateAESKey(): SecretKey {
        return KeyGenerator.getInstance("AES").apply {
            init(AES_KEY_SIZE, SecureRandom.getInstanceStrong())
        }.generateKey().also { key ->
            if (key.encoded.size != 32) {
                throw IllegalStateException("生成的AES密钥长度不是32字节: ${key.encoded.size}")
            }
        }
    }

    /**
     * RSA加密AES密钥（使用服务端提供的公钥）
     */
    private fun rsaEncrypt(data: ByteArray, publicKeyStr: String): String {

        try {
            // 处理PEM格式公钥
            val processedKey = publicKeyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", "")
                .replace("\r", "")
                .trim()

            // 解析公钥
            val keyBytes = Base64.getDecoder().decode(processedKey)
            val keySpec = X509EncodedKeySpec(keyBytes)
            val publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec)

            // 配置OAEP参数（必须与后端完全一致）
            val oaepParams = OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            )

            // 初始化加密器
            val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams)

            // 执行加密
            val encrypted = cipher.doFinal(data)
            return Base64.getEncoder().encodeToString(encrypted)
                .replace("\n", "")
        } catch (e: Exception) {
            throw RuntimeException("RSA加密失败", e)
        }
    }
}