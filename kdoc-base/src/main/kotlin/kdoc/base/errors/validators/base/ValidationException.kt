/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.base.errors.validators.base

import kdoc.base.errors.validators.EmailValidator
import kdoc.base.errors.validators.PhoneValidator

/**
 * Custom exception class to represent validation errors.
 * This exception should be thrown when validation of a value fails.
 *
 * @property code A unique code identifying the type of validation error.
 * @param message The detail message describing the validation failure.
 * @param cause Optional underlying reason for this [ValidationException].
 *
 * @see IValidator
 * @see EmailValidator
 * @see PhoneValidator
 */
public class ValidationException(
    public val code: String,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
