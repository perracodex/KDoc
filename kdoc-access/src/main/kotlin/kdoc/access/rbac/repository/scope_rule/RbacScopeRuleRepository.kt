/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.access.rbac.repository.scope_rule

import kdoc.access.rbac.entity.field_rule.RbacFieldRuleRequest
import kdoc.access.rbac.entity.scope_rule.RbacScopeRuleRequest
import kdoc.access.rbac.repository.field_rule.IRbacFieldRuleRepository
import kdoc.base.database.schema.admin.rbac.RbacScopeRuleTable
import kdoc.base.utils.DateTimeUtils
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Implementation of [IRbacScopeRuleRepository].
 * Responsible for managing [RbacScopeRuleRequest] data.
 *
 * @see RbacScopeRuleRequest
 */
class RbacScopeRuleRepository(
    private val fieldRuleRepository: IRbacFieldRuleRepository
) : IRbacScopeRuleRepository {

    override fun replace(roleId: UUID, scopeRuleRequests: List<RbacScopeRuleRequest>?): Int {
        return transaction {
            RbacScopeRuleTable.deleteWhere {
                RbacScopeRuleTable.roleId eq roleId
            }

            var newRowCount = 0

            if (!scopeRuleRequests.isNullOrEmpty()) {
                val scopeRules: List<ResultRow> = RbacScopeRuleTable.batchInsert(
                    data = scopeRuleRequests
                ) { scopeRule ->
                    this.mapRuleRequest(roleId = roleId, scopeRuleRequest = scopeRule)
                }

                newRowCount = scopeRules.size

                // If the update was successful, recreate the field rules.
                if (newRowCount > 0) {
                    scopeRules.forEach { scopeRule ->

                        // Find the field rules for the scope rule using the scope name.
                        val fieldRuleRequest: List<RbacFieldRuleRequest>? = scopeRuleRequests.firstOrNull {
                            it.scope == scopeRule[RbacScopeRuleTable.scope]
                        }?.fieldRules

                        // If the field rules are not empty, update the field rules.
                        val newScopeRuleId: UUID = scopeRule[RbacScopeRuleTable.id]
                        fieldRuleRepository.replace(
                            scopeRuleId = newScopeRuleId,
                            requestList = fieldRuleRequest
                        )
                    }
                }
            }

            newRowCount
        }
    }

    /**
     * Populates an SQL [BatchInsertStatement] with data from an [RbacScopeRuleRequest] instance,
     * so that it can be used to update or create a database record.
     */
    private fun BatchInsertStatement.mapRuleRequest(roleId: UUID, scopeRuleRequest: RbacScopeRuleRequest) {
        this[RbacScopeRuleTable.roleId] = roleId
        this[RbacScopeRuleTable.scope] = scopeRuleRequest.scope
        this[RbacScopeRuleTable.accessLevel] = scopeRuleRequest.accessLevel
        this[RbacScopeRuleTable.updatedAt] = DateTimeUtils.currentUTCDateTime()
    }
}
