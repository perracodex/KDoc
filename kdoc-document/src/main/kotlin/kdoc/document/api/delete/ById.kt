/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.document.api.delete

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kdoc.core.context.SessionContext
import kdoc.core.context.getContext
import kdoc.core.persistence.utils.toUuid
import kdoc.document.api.DocumentRouteAPI
import kdoc.document.service.DocumentAuditService
import kdoc.document.service.DocumentService
import org.koin.core.parameter.parametersOf
import org.koin.ktor.plugin.scope
import kotlin.uuid.Uuid

@DocumentRouteAPI
internal fun Route.deleteDocumentByIdRoute() {
    /**
     * Delete a document by ID.
     * @OpenAPITag Document - Delete
     */
    delete("v1/document/{document_id}/") {
        val documentId: Uuid = call.parameters.getOrFail(name = "document_id").toUuid()

        val sessionContext: SessionContext = call.getContext()
        call.scope.get<DocumentAuditService> { parametersOf(sessionContext) }
            .audit(operation = "delete by id", documentId = documentId)

        val service: DocumentService = call.scope.get<DocumentService> { parametersOf(sessionContext) }
        val deletedCount: Int = service.delete(documentId = documentId)
        call.respond(status = HttpStatusCode.OK, message = deletedCount)
    }
}
