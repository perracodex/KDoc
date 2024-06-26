/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.base.settings.config.sections.security.sections

import kdoc.base.settings.config.parser.IConfigSection
import kdoc.base.settings.config.sections.security.SecuritySettings
import kotlinx.serialization.Serializable

/**
 * Encryption key settings.
 *
 * @property atRest Settings related to encryption at rest.
 * @property atTransit Settings related to encryption in transit.
 * @property atTransitExpiration Expiration time for encryption in transit, in seconds.
 * @property hmac Settings related to HMAC encryption.
 */
@Serializable
data class EncryptionSettings(
    val atRest: Spec,
    val atTransit: Spec,
    val atTransitExpiration: Long,
    val hmac: Hmac
) : IConfigSection {

    /**
     * Configuration settings for a specific encryption.
     *
     * @property algorithm Algorithm used for encrypting/decrypting data.
     * @property salt Salt used for encrypting/decrypting data.
     * @property key Secret key for encrypting/decrypting data.
     * @property sign Signature key to sign the encrypted data.
     */
    @Serializable
    data class Spec(
        val algorithm: String,
        val salt: String,
        val key: String,
        val sign: String
    ) : IConfigSection {
        init {
            require(algorithm.isNotBlank()) { "Missing encryption algorithm." }
            require(salt.isNotBlank()) { "Missing encryption salt." }
            require(key.isNotBlank() && (key.length >= SecuritySettings.MIN_KEY_LENGTH)) {
                "Invalid encryption key. Must be >= ${SecuritySettings.MIN_KEY_LENGTH} characters long."
            }
            require(sign.isNotBlank() && (sign.length >= SecuritySettings.MIN_KEY_LENGTH)) {
                "Invalid sign key. Must be >= ${SecuritySettings.MIN_KEY_LENGTH} characters long."
            }
        }
    }

    /**
     * Configuration settings for HMAC encryption.
     *
     * @property algorithm Algorithm used for encrypting/decrypting data.
     * @property key Secret key for encrypting/decrypting data.
     */
    @Serializable
    data class Hmac(
        val algorithm: String,
        val key: String
    ) : IConfigSection {
        init {
            require(algorithm.isNotBlank()) { "Missing HMAC algorithm." }
            require(key.isNotBlank() && (key.length >= SecuritySettings.MIN_KEY_LENGTH)) {
                "Invalid HMAC key. Must be >= ${SecuritySettings.MIN_KEY_LENGTH} characters long."
            }
        }
    }
}
