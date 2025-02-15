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
package com.savvasdalkitsis.uhuruphotos.implementation.hidden.seam

import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageActionHandler
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageMutation
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumDetails
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.Title
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.biometrics.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource.HiddenMedia
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.usecase.HiddenPhotosUseCase
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

internal class HiddenPhotosAlbumPageActionHandler @Inject constructor(
    mediaUseCase: MediaUseCase,
    hiddenPhotosUseCase: HiddenPhotosUseCase,
    settingsUseCase: SettingsUseCase,
    biometricsUseCase: BiometricsUseCase
): ActionHandler<AlbumPageState, AlbumPageEffect, AlbumPageAction, AlbumPageMutation> by AlbumPageActionHandler(
    albumRefresher = { mediaUseCase.refreshFavouriteMedia() },
    initialFeedDisplay = { hiddenPhotosUseCase.getHiddenPhotosFeedDisplay() },
    feedDisplayPersistence = { _, feedDisplay ->
        hiddenPhotosUseCase.setHiddenPhotosFeedDisplay(feedDisplay)
    },
    albumDetailsEmptyCheck = {
        mediaUseCase.getHiddenMedia().getOrDefault(emptyList()).isEmpty()
    },
    albumDetailsFlow = { _, effect ->
        settingsUseCase.observeBiometricsRequiredForHiddenPhotosAccess()
            .flatMapLatest { biometricsRequired ->
                val proceed = when {
                    biometricsRequired -> biometricsUseCase.authenticate(
                        string.authenticate,
                        string.authenticate_for_access_to_hidden,
                        string.authenticate_for_access_to_hidden_description,
                        true,
                    )
                    else -> Result.success(Unit)
                }
                if (proceed.isFailure) {
                    flow {
                        effect(NavigateBack)
                    }
                } else {
                    mediaUseCase.observeHiddenMedia()
                        .mapNotNull { it.getOrNull() }
                        .map { photoEntries ->
                            AlbumDetails(
                                title = Title.Resource(string.hidden_photos),
                                albums = listOf(
                                    Album(
                                        id = "hidden",
                                        displayTitle = "",
                                        location = null,
                                        photos = photoEntries,
                                    )
                                ),
                            )
                        }
                }
            }

    },
    mediaSequenceDataSource = { HiddenMedia },
)