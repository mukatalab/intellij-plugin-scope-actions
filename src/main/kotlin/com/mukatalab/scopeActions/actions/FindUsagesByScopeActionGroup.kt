package com.mukatalab.scopeActions.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.mukatalab.jumpy.actions.FindUsagesByScopeAction
import com.mukatalab.scopeActions.getScopeActionTypeName
import com.mukatalab.scopeActions.getUserDefinedSearchScopes
import com.mukatalab.scopeActions.services.ScopeActionsProjectConfigService

class FindUsagesByScopeActionGroup(private var children: Array<AnAction> = arrayOf()) : ActionGroup() {
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return children
    }

    fun loadChildren(project: Project) {
        val pluginId = PluginId.findId("com.mukatalab.scopeActions") ?: return
        val userDefinedScopes = getUserDefinedSearchScopes(project, DataContext.EMPTY_CONTEXT)

        val scopeActionsService = ScopeActionsProjectConfigService.getInstance(project)
        val actionManagerService: ActionManager = ActionManager.getInstance()

        val scopeActions: MutableList<AnAction> = mutableListOf()
        for (scope in userDefinedScopes) {
            if (scopeActionsService.state.getEnabled(
                    scope.displayName,
                    getScopeActionTypeName(FindUsagesByScopeAction::class)
                )
            ) {
                val scopeAction = FindUsagesByScopeAction(
                    scope,
                    "Find Usages by Scope: ${scope.displayName}"
                )
                scopeActions.add(scopeAction)
                actionManagerService.registerAction(
                    scopeActionsService.state.getActionId(
                        scope.displayName,
                        getScopeActionTypeName(FindUsagesByScopeAction::class)
                    ),
                    scopeAction,
                    pluginId
                )
            }
        }

        children = scopeActions.toTypedArray()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.PSI_ELEMENT) != null
    }
}