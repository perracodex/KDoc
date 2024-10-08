/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.document.api.operate

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kdoc.core.context.getContext
import kdoc.core.persistence.utils.toUuidOrNull
import kdoc.core.security.utils.SecureUrl
import kdoc.core.settings.AppSettings
import kdoc.core.utils.NetworkUtils
import kdoc.document.api.DocumentRouteAPI
import kdoc.document.service.DocumentAuditService
import org.koin.core.parameter.parametersOf
import org.koin.ktor.plugin.scope
import kotlin.uuid.Uuid

@DocumentRouteAPI
internal fun Route.getDocumentSignedUrlRoute() {
    /**
     * Generate the signed URL for a document download.
     * @OpenAPITag Document - Operate
     */
    get("v1/document/url/{document_id?}/{group_id?}") {
        val documentId: Uuid? = call.request.queryParameters["document_id"].toUuidOrNull()
        val groupId: Uuid? = call.request.queryParameters["group_id"]?.toUuidOrNull()
        (documentId ?: groupId) ?: run {
            call.respond(status = HttpStatusCode.BadRequest, message = "Either document_id or group_id must be provided.")
            return@get
        }

        call.scope.get<DocumentAuditService> { parametersOf(call.getContext()) }
            .audit(operation = "generate signed URL", documentId = documentId)

        val basePath = "${NetworkUtils.getServerUrl()}/${AppSettings.storage.downloadsBasePath}"
        val secureUrl: String = SecureUrl.generate(
            basePath = basePath,
            data = "document_id=${documentId ?: ""}&group_id=${groupId ?: ""}",
        )

        call.respond(status = HttpStatusCode.OK, message = secureUrl)
    }
}
