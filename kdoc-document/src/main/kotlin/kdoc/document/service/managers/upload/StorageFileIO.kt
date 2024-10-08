/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.document.service.managers.upload

import kdoc.core.security.utils.SecureIO
import kdoc.core.utils.CountingInputStream
import kdoc.document.service.DocumentService.Companion.PATH_SEPARATOR
import kdoc.document.service.managers.upload.annotation.UploadAPI
import java.io.File
import java.io.InputStream

/**
 * Handles the actual persistence of files to the storage.
 */
@UploadAPI
internal object StorageFileIO {

    /**
     * Data class to encapsulate the details of a persisted file.
     *
     * @property location The path location of the document file.
     * @property file The document reference in the storage.
     * @property fileSize The size of the document in bytes.
     */
    data class FileDetails(
        val location: String,
        val file: File,
        var fileSize: Long
    )

    fun save(
        uploadsRoot: String,
        path: String,
        storageFilename: String,
        cipher: Boolean,
        inputStream: InputStream
    ): FileDetails {
        val location = File("$uploadsRoot$PATH_SEPARATOR$path")
        location.mkdirs()  // Create directories if they don't exist.

        val documentFile = File(location, storageFilename)
        val countingInputStream = CountingInputStream(wrappedInputStream = inputStream)

        documentFile.outputStream().buffered().use { outputStream ->
            if (cipher) {
                SecureIO.cipher(input = countingInputStream, output = outputStream)
            } else {
                countingInputStream.copyTo(out = outputStream)
            }
        }

        return FileDetails(
            location = location.path,
            file = documentFile,
            fileSize = countingInputStream.totalBytesRead
        )
    }
}