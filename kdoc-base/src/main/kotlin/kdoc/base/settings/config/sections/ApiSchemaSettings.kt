/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.base.settings.config.sections

import kdoc.base.env.EnvironmentType
import kdoc.base.settings.config.parser.IConfigSection
import kotlinx.serialization.Serializable

/**
 * Contains settings related to Swagger and OpenAPI.
 *
 * @property environments The list of environments under which the documentation is enabled.
 * @property schemaRoot The root endpoint path for API schema documentation.
 * @property schemaResourceFile The OpenAPI resource documentation file.
 * @property openApiEndpoint The endpoint to the OpenAPI specification.
 * @property swaggerEndpoint The endpoint to the Swagger UI.
 * @property redocEndpoint The endpoint path to the Redoc UI.
 */
@Serializable
data class ApiSchemaSettings(
    val environments: List<EnvironmentType>,
    val schemaRoot: String,
    val schemaResourceFile: String,
    val openApiEndpoint: String,
    val swaggerEndpoint: String,
    val redocEndpoint: String
) : IConfigSection
