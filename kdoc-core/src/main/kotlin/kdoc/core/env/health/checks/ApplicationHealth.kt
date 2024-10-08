/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.core.env.health.checks

import kdoc.core.env.EnvironmentType
import kdoc.core.env.health.annotation.HealthCheckAPI
import kdoc.core.settings.AppSettings
import kotlinx.serialization.Serializable

/**
 * Used to check general application's health checks
 * that cannot be categorized by the other concrete health check.
 *
 * @property errors List of errors found during the health check.
 * @property apiSchemaEnabled Whether the API schema generation is enabled.
 */
@HealthCheckAPI
@Serializable
public data class ApplicationHealth(
    val errors: MutableList<String>,
    val apiSchemaEnabled: Boolean
) {
    internal constructor() : this(
        errors = mutableListOf(),
        apiSchemaEnabled = AppSettings.apiSchema.environments.contains(AppSettings.runtime.environment)
    )

    init {
        val environment: EnvironmentType = AppSettings.runtime.environment

        if (environment == EnvironmentType.PROD) {
            if (apiSchemaEnabled) {
                errors.add("${this::class.simpleName}. API schema is enabled in '$environment' environment.")
            }
        }
    }
}
