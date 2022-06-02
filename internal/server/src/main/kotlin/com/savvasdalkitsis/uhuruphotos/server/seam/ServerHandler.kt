/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.server.seam

import com.savvasdalkitsis.uhuruphotos.api.http.isHttpUrl
import com.savvasdalkitsis.uhuruphotos.api.http.isValidUrlOrDomain
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Offline
import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.AttemptChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.ChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.CheckPersistedServer
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.DismissUnsecuredServerDialog
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.Login
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.RequestServerUrlChange
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.SendLogsClick
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.TogglePasswordVisibility
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.UrlTyped
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.UserPasswordChangedTo
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.UsernameChangedTo
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerEffect.Close
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerEffect.ErrorLoggingIn
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerEffect.SendFeedback
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerMutation.AskForServerDetails
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerMutation.AskForUserCredentials
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerMutation.ChangePasswordTo
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerMutation.ChangeUsernameTo
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerMutation.HideUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerMutation.PerformingBackgroundJob
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerMutation.SetPasswordVisibility
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerMutation.ShowUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerMutation.ShowUrlValidation
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState.UserCredentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class ServerHandler @Inject constructor(
    private val serverUseCase: ServerUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
) : ActionHandler<ServerState, ServerEffect, ServerAction, ServerMutation> {

    override fun handleAction(
        state: ServerState,
        action: ServerAction,
        effect: suspend (ServerEffect) -> Unit,
    ): Flow<ServerMutation> = when (action) {
        CheckPersistedServer -> flow {
            when (serverUseCase.getServerUrl()) {
                null -> emit(AskForServerDetails(null, isValid = false))
                else -> when (authenticationUseCase.authenticationStatus()) {
                    is Offline, is Authenticated -> effect(Close)
                    is Unauthenticated -> {
                        when (state) {
                            is UserCredentials -> emit(AskForUserCredentials(state.username, state.password))
                            else -> emit(AskForUserCredentials("", ""))
                        }
                    }
                }
            }
        }
        is RequestServerUrlChange -> flow {
            val prefilledUrl = serverUseCase.getServerUrl()
            emit(AskForServerDetails(prefilledUrl, prefilledUrl?.isValidUrlOrDomain == true))
        }
        is UrlTyped -> flow {
            val prefilledUrl = serverUseCase.getServerUrl()
            emit(ShowUrlValidation(prefilledUrl, action.url.isValidUrlOrDomain))
        }
        is ChangeServerUrlTo -> flow {
            emit(HideUnsecureServerConfirmation)
            if (action.url.isValidUrlOrDomain) {
                serverUseCase.setServerUrl(action.url)
                effect(Close)
            }
        }
        is AttemptChangeServerUrlTo -> flow {
            if (action.url.isValidUrlOrDomain) {
                if (action.url.isHttpUrl) {
                    emit(ShowUnsecureServerConfirmation)
                } else {
                    serverUseCase.setServerUrl(action.url)
                    effect(Close)
                }
            }
        }
        DismissUnsecuredServerDialog -> flowOf(HideUnsecureServerConfirmation)
        Login -> flow {
            if ((state as? UserCredentials)?.allowLogin == false) {
                return@flow
            }
            emit(PerformingBackgroundJob)
            val credentials = state as UserCredentials
            try {
                authenticationUseCase.login(credentials.username, credentials.password)
                effect(Close)
            } catch (e: Exception) {
                log(e)
                effect(ErrorLoggingIn(e))
                emit(AskForUserCredentials(credentials.username, credentials.password))
            }
        }
        is UsernameChangedTo -> flowOf(ChangeUsernameTo(action.username.lowercase()))
        is UserPasswordChangedTo -> flowOf(ChangePasswordTo(action.password))
        SendLogsClick -> flow {
            effect(SendFeedback)
        }
        TogglePasswordVisibility -> flowOf(SetPasswordVisibility(!(state as UserCredentials).passwordVisible))
    }

}
