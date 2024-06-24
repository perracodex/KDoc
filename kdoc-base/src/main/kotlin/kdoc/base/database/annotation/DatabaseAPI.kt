/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.base.database.annotation

/**
 * Annotation for controlled access to the Database API.
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "Only to be used within the Database API.")
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseAPI
