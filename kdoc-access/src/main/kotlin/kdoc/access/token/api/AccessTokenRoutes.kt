/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.access.token.api

import io.ktor.server.routing.*
import kdoc.access.token.annotation.TokenAPI
import kdoc.access.token.api.operate.createTokenRoute
import kdoc.access.token.api.operate.refreshTokenRoute

/**
 * Defines routes for handling access tokens within the authentication system,
 * using JWT and Basic Authentication as specified in Ktor's documentation.
 *
 * • `create`: Generates a new JWT token using Basic Authentication.
 *   This endpoint is rate-limited to prevent abuse and requires valid Basic Authentication credentials.
 *
 * • `refresh`: Allows a client to refresh their existing JWT token. This endpoint does not require
 *   Basic Authentication but does require a valid JWT token in the 'Authorization' header.
 *   Depending on the state of the provided token (valid, expired, or invalid), it either returns
 *   the same token, generates a new one, or denies access.
 *
 * #### References
 * - [Ktor JWT Authentication](https://ktor.io/docs/server-jwt.html)
 * - [Basic Authentication](https://ktor.io/docs/server-basic-auth.html)
 */
@OptIn(TokenAPI::class)
public fun Route.accessTokenRoutes() {
    createTokenRoute()
    refreshTokenRoute()
}
