/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.access.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import kdoc.access.actor.service.DefaultActorFactory
import kdoc.access.context.CallContextFactory
import kdoc.access.rbac.plugin.annotation.RbacAPI
import kdoc.access.rbac.view.RbacLoginView
import kdoc.base.env.CallContext
import kdoc.base.env.CallContext.Companion.setContext

/**
 * Refreshes the default actors, and configures the RBAC form login authentication.
 *
 * Demonstrates how to use form-base authentication, in which case
 * principal are not propagated across different requests, so we
 * must use sessions to store the actor information.
 *
 * See: [Basic Authentication Documentation](https://ktor.io/docs/server-basic-auth.html)
 */
@OptIn(RbacAPI::class)
public fun Application.configureRbac() {

    // Refresh the default actors.
    DefaultActorFactory.refresh()

    // Configure the RBAC form login authentication.
    authentication {
        form(name = RbacLoginView.RBAC_LOGIN_PATH) {
            userParamName = RbacLoginView.KEY_USERNAME
            passwordParamName = RbacLoginView.KEY_PASSWORD

            challenge {
                call.sessions.clear(name = CallContext.SESSION_NAME)
                call.respondRedirect(url = RbacLoginView.RBAC_LOGIN_PATH)
            }

            validate { credential ->
                CallContextFactory.from(credential = credential)?.let { callContext ->
                    this.setContext(callContext = callContext)
                    return@validate callContext
                }

                this.sessions.clear(name = CallContext.SESSION_NAME)
                return@validate null
            }
        }
    }
}
