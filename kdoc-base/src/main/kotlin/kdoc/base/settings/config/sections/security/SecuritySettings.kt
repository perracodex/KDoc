/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.base.settings.config.sections.security

import kdoc.base.settings.config.parser.IConfigSection
import kdoc.base.settings.config.sections.security.sections.ConstraintsSettings
import kdoc.base.settings.config.sections.security.sections.EncryptionSettings
import kdoc.base.settings.config.sections.security.sections.RbacSettings
import kdoc.base.settings.config.sections.security.sections.auth.BasicAuthSettings
import kdoc.base.settings.config.sections.security.sections.auth.JwtAuthSettings
import kotlinx.serialization.Serializable

/**
 * Top level section for the Security related settings.
 *
 * @property isEnabled Whether to enable Basic and JWT authentication.
 * @property useSecureConnection Whether to use a secure connection or not.
 * @property encryption Settings related to encryption, such as the encryption keys.
 * @property constraints Settings related to security constraints, such endpoints rate limits.
 * @property basicAuth Settings related to basic authentication, such as the realm and provider name.
 * @property jwtAuth Settings related to JWT authentication, such as the JWT secrets.
 * @property rbac Settings related to RBAC authentication.
 */
@Serializable
data class SecuritySettings(
    val isEnabled: Boolean,
    val useSecureConnection: Boolean,
    val encryption: EncryptionSettings,
    val constraints: ConstraintsSettings,
    val basicAuth: BasicAuthSettings,
    val jwtAuth: JwtAuthSettings,
    val rbac: RbacSettings
) : IConfigSection {
    companion object {
        /** The minimum length for a security key, such as encryption and secret keys. */
        const val MIN_KEY_LENGTH: Int = 12
    }
}
