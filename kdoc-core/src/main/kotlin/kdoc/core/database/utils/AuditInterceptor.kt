/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.core.database.utils

import kdoc.core.context.SessionContext
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.StatementInterceptor

/**
 * Intercepts SQL statements to perform auditing actions before execution.
 *
 * @param sessionContext The [SessionContext] details.
 */
internal class AuditInterceptor(private val sessionContext: SessionContext) : StatementInterceptor {
    override fun beforeExecution(
        transaction: Transaction,
        context: StatementContext
    ) {
    }
}
