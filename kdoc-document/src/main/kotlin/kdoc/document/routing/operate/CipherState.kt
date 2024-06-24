/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.document.routing.operate

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kdoc.base.env.SessionContext
import kdoc.document.routing.DocumentRouteAPI
import kdoc.document.service.DocumentAuditService
import kdoc.document.service.DocumentStorage
import org.koin.core.parameter.parametersOf
import org.koin.ktor.plugin.scope

@DocumentRouteAPI
internal fun Route.changeDocumentsCipherState() {
    // Changes the cipher state of all documents.
    put("cipher/{cipher}") {
        val cipher: Boolean = call.parameters["cipher"].toBoolean()

        val sessionContext: SessionContext? = SessionContext.from(call = call)
        call.scope.get<DocumentAuditService> { parametersOf(sessionContext) }
            .audit(operation = "change cipher state", log = cipher.toString())

        val documentStorage: DocumentStorage = call.scope.get<DocumentStorage> { parametersOf(sessionContext) }
        val count: Int = documentStorage.changeCipherState(cipher = cipher)

        call.respond(
            status = HttpStatusCode.OK,
            message = count
        )
    }
}
