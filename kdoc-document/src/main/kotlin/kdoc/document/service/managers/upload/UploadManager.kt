/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.document.service.managers.upload

import io.ktor.http.content.*
import kdoc.base.database.schema.document.types.DocumentType
import kdoc.base.env.SessionContext
import kdoc.base.env.Tracer
import kdoc.document.entity.DocumentEntity
import kdoc.document.entity.DocumentRequest
import kdoc.document.errors.DocumentError
import kdoc.document.repository.IDocumentRepository
import kdoc.document.service.managers.upload.annotation.UploadAPI
import java.util.*

/**
 * Handles the uploading and processing of document files into the storage.
 *
 * @see [MultipartFileHandler]
 */
internal class UploadManager(
    @Suppress("unused") private val sessionContext: SessionContext,
    private val documentRepository: IDocumentRepository
) {
    private val tracer = Tracer<UploadManager>()

    /**
     * Handles the creation of documents from multipart data.
     * If a group ID is provided, the uploaded files are associated with that group,
     * otherwise a new group ID is generated and associated with the files.
     *
     * If any file persistence fails, all saved in this operation are deleted.
     *
     * @param ownerId The ID of the owner of the document.
     * @param groupId Optional group ID to associate with the uploaded files.
     * @param type The [DocumentType] being uploaded.
     * @param uploadRoot The root path where uploaded files are stored.
     * @param cipher Whether the document should be ciphered.
     * @param multipart The multipart data containing the files and request.
     * @return A list of created DocumentEntity objects or null if the request is invalid.
     */
    @OptIn(UploadAPI::class)
    suspend fun upload(
        ownerId: UUID,
        groupId: UUID? = null,
        type: DocumentType,
        uploadRoot: String,
        cipher: Boolean,
        multipart: MultiPartData
    ): List<DocumentEntity> {

        // Receive the uploaded files.

        val persistedFiles: List<MultipartFileHandler.Response> = MultipartFileHandler(
            uploadsRoot = uploadRoot,
            cipher = cipher
        ).receive(ownerId = ownerId, groupId = groupId, type = type, multipart = multipart)

        if (persistedFiles.isEmpty()) {
            tracer.error("No files provided for upload.")
            throw DocumentError.NoDocumentProvided(ownerId = ownerId)
        }

        // Create the document references in the database.

        try {
            val output: MutableList<DocumentEntity> = mutableListOf()
            val targetGroupId: UUID = groupId ?: UUID.randomUUID()

            persistedFiles.forEach { fileEntry ->
                val documentRequest = DocumentRequest(
                    ownerId = ownerId,
                    groupId = targetGroupId,
                    type = type,
                    description = fileEntry.description,
                    originalName = fileEntry.originalFilename,
                    storageName = fileEntry.storageFilename,
                    location = fileEntry.location,
                    isCiphered = fileEntry.isCiphered,
                    size = fileEntry.size
                )

                val documentId: UUID = documentRepository.create(documentRequest = documentRequest)
                val createdDocument: DocumentEntity = documentRepository.findById(documentId = documentId)!!
                output.add(createdDocument)
            }

            return output

        } catch (e: Exception) {
            tracer.error("Error uploading document: $e")
            // If any file persistence fails, delete all saved files.
            persistedFiles.forEach { it.delete() }
            throw DocumentError.FailedToPersistUpload(ownerId = ownerId, cause = e)
        }
    }
}
