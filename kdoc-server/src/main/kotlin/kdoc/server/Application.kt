/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.server

import io.ktor.server.application.*
import io.ktor.server.netty.*
import kdoc.access.plugins.configureBasicAuthentication
import kdoc.access.plugins.configureJwtAuthentication
import kdoc.access.plugins.configureRbac
import kdoc.access.plugins.configureSessions
import kdoc.base.plugins.*
import kdoc.base.settings.AppSettings
import kdoc.server.plugins.configureKoin
import kdoc.server.plugins.configureRoutes
import kdoc.server.utils.ApplicationsUtils

/**
 * Application main entry point.
 * Launches the Ktor server using Netty as the application engine.
 *
 * See: [Choosing an engine](https://ktor.io/docs/server-engines.html)
 *
 * See: [Configure an engine](https://ktor.io/docs/server-engines.html#configure-engine)
 *
 * See: [Application Monitoring With Server Events](https://ktor.io/docs/server-events.html)
 *
 * @param args Command line arguments passed to the application.
 */
fun main(args: Array<String>) {
    EngineMain.main(args)
}

/**
 * Application configuration module, responsible for setting up the server with various plugins.
 * Execution order is important as some configurations depend on the prior initialization of others.
 *
 * See: [Modules](https://ktor.io/docs/server-modules.html)
 *
 * See: [Plugins](https://ktor.io/docs/server-plugins.html)
 */
fun Application.kdocModule() {

    AppSettings.load(applicationConfig = environment.config)

    configureKoin()

    configureDatabase()

    configureCors()

    configureSecureConnection()

    configureHeaders()

    configureHttp()

    configureCallLogging()

    configureSerialization()

    configureRateLimit()

    configureRbac()

    configureBasicAuthentication()

    configureJwtAuthentication()

    configureSessions()

    configureRoutes()

    configuredApiSchema()

    configureMicroMeterMetrics()

    configureStatusPages()

    configureDoubleReceive()

    configureThymeleaf()

    ApplicationsUtils.watchServer(environment = this.environment)
}
