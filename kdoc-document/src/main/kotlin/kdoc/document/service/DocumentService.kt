/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.document.service

import kdoc.base.env.SessionContext
import kdoc.base.env.Tracer
import kdoc.base.persistence.pagination.Page
import kdoc.base.persistence.pagination.Pageable
import kdoc.base.persistence.utils.toUUIDOrNull
import kdoc.base.security.utils.SecureUrl
import kdoc.base.settings.AppSettings
import kdoc.base.utils.NetworkUtils
import kdoc.document.entity.DocumentEntity
import kdoc.document.entity.DocumentFilterSet
import kdoc.document.entity.DocumentRequest
import kdoc.document.repository.IDocumentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

/**
 * Document service, where all the documents business logic should be defined.
 */
internal class DocumentService(
    @Suppress("unused") private val sessionContext: SessionContext,
    private val documentRepository: IDocumentRepository
) {
    private val tracer = Tracer<DocumentService>()

    /**
     * Retrieves a document entity by its ID.
     *
     * @param documentId The ID of the document to be retrieved.
     * @return The resolved [DocumentEntity] if found, null otherwise.
     */
    suspend fun findById(documentId: UUID): DocumentEntity? = withContext(Dispatchers.IO) {
        return@withContext documentRepository.findById(documentId = documentId)
    }

    /**
     * Retrieves a document entity by its owner ID.
     *
     * @param ownerId The owner ID of the document to be retrieved.
     * @param pageable The pagination options to be applied, or null for a single all-in-one page.
     * @return List of [DocumentEntity] entries.
     */
    suspend fun findByOwnerId(ownerId: UUID, pageable: Pageable?): Page<DocumentEntity> = withContext(Dispatchers.IO) {
        return@withContext documentRepository.findByOwnerId(ownerId = ownerId, pageable = pageable)
    }

    /**
     * Retrieves all document entities by group ID.
     *
     * @param groupId The group the documents belongs to.
     * @param pageable The pagination options to be applied, or null for a single all-in-one page.
     * @return List of [DocumentEntity] entries.
     */
    suspend fun findByGroupId(groupId: UUID, pageable: Pageable? = null): Page<DocumentEntity> = withContext(Dispatchers.IO) {
        return@withContext documentRepository.findByGroupId(groupId = groupId, pageable = pageable)
    }

    /**
     * Retrieves all document entities.
     *
     * @param pageable The pagination options to be applied, or null for a single all-in-one page.
     * @return List of [DocumentEntity] entries.
     */
    suspend fun findAll(pageable: Pageable? = null): Page<DocumentEntity> = withContext(Dispatchers.IO) {
        return@withContext documentRepository.findAll(pageable = pageable)
    }

    /**
     * Retrieves all document entities matching the provided [filterSet].
     *
     * @param filterSet The [DocumentFilterSet] to be applied.
     * @param pageable The pagination options to be applied, or null for a single all-in-one page.
     * @return List of [DocumentEntity] entries.
     */
    suspend fun search(filterSet: DocumentFilterSet, pageable: Pageable? = null): Page<DocumentEntity> = withContext(Dispatchers.IO) {
        return@withContext documentRepository.search(filterSet = filterSet, pageable = pageable)
    }

    /**
     * Creates a new document.
     *
     * @param documentRequest The document to be created.
     * @return The ID of the created document.
     */
    suspend fun create(documentRequest: DocumentRequest): DocumentEntity = withContext(Dispatchers.IO) {
        tracer.debug("Creating a new document.")
        val documentId: UUID = documentRepository.create(documentRequest = documentRequest)
        return@withContext findById(documentId = documentId)!!
    }

    /**
     * Updates a document's details.
     *
     * @param documentId The ID of the document to be updated.
     * @param documentRequest The new details for the document.
     * @return The number of updated records.
     */
    @Suppress("unused")
    suspend fun update(
        documentId: UUID,
        documentRequest: DocumentRequest
    ): DocumentEntity? = withContext(Dispatchers.IO) {
        tracer.debug("Updating document with ID: $documentId.")
        val updatedCount: Int = documentRepository.update(documentId = documentId, documentRequest = documentRequest)
        return@withContext if (updatedCount > 0) findById(documentId = documentId) else null
    }

    /**
     * Deletes a document using the provided ID.
     *
     * @param documentId The ID of the document to be deleted.
     * @return The number of delete records.
     */
    suspend fun delete(documentId: UUID): Int = withContext(Dispatchers.IO) {
        tracer.debug("Deleting document with ID: $documentId.")
        return@withContext documentRepository.delete(documentId = documentId)
    }

    /**
     * Deletes all documents by group ID.
     *
     * @param groupId The group ID to be used for deletion.
     * @return The number of deleted records.
     */
    suspend fun deleteByGroup(groupId: UUID): Int = withContext(Dispatchers.IO) {
        tracer.debug("Deleting all documents by group ID: $groupId.")
        return@withContext documentRepository.deleteByGroup(groupId = groupId)
    }

    /**
     * Deletes all documents.
     *
     * @return The number of deleted records.
     */
    suspend fun deleteAll(): Int = withContext(Dispatchers.IO) {
        tracer.debug("Deleting all documents.")
        return@withContext documentRepository.deleteAll()
    }

    /**
     * Retrieves the total count of documents.
     *
     * @return The total count of existing records.
     */
    suspend fun count(): Int = withContext(Dispatchers.IO) {
        return@withContext documentRepository.count()
    }

    /**
     * Retrieves a list of [DocumentEntity] references based on the provided token and signature.
     * If the signature is invalid or expired, the method will return null.
     *
     * @param token The token to verify.
     * @param signature The signature to verify.
     * @return The [DocumentEntity] if the verification is successful, null otherwise.
     */
    suspend fun findBySignature(token: String, signature: String): List<DocumentEntity>? {
        val basePath = "${NetworkUtils.getServerUrl()}/${AppSettings.storage.downloadsBasePath}"
        val decodedToken: String? = SecureUrl.verify(
            basePath = basePath,
            token = token,
            signature = signature
        )

        if (decodedToken == null) {
            tracer.warning("Invalid or expired token: $token")
            return null
        }

        val params: Map<String, String> = decodedToken.split("&").associate {
            val (key, value) = it.split("=")
            key.lowercase() to value.trim()
        }

        val documentId: UUID? = params["document_id"]?.toUUIDOrNull()
        val groupId: UUID? = params["group_id"]?.toUUIDOrNull()
        if (documentId == null && groupId == null) {
            tracer.error("No document ID or group ID provided.")
            throw IllegalArgumentException("No document ID or group ID provided.")
        }

        return withContext(Dispatchers.IO) {
            return@withContext search(
                filterSet = DocumentFilterSet(
                    id = documentId,
                    groupId = groupId
                )
            ).content
        }
    }

    companion object {
        /** The file path's system-dependent name-separator character. */
        val PATH_SEPARATOR: String = File.separator
    }
}
