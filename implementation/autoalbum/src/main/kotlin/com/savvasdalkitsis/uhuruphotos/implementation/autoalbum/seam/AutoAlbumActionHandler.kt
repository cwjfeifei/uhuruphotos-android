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
package com.savvasdalkitsis.uhuruphotos.implementation.autoalbum.seam

import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageActionHandler
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageMutation
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumDetails
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.Title
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.autoalbum.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.autoalbum.view.state.AutoAlbumFeedDisplay
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AutoAlbumActionHandler @Inject constructor(
    autoAlbumsUseCase: AutoAlbumsUseCase,
    remoteMediaUseCase: RemoteMediaUseCase,
    dateDisplayer: DateDisplayer,
) : ActionHandler<AlbumPageState, AlbumPageEffect, AlbumPageAction, AlbumPageMutation>
by AlbumPageActionHandler(
    albumRefresher = { autoAlbumsUseCase.refreshAutoAlbum(it) },
    initialFeedDisplay = { AutoAlbumFeedDisplay },
    feedDisplayPersistence = { _, _ -> },
    albumDetailsEmptyCheck = { albumId ->
        autoAlbumsUseCase.getAutoAlbum(albumId).items.isEmpty()
    },
    albumDetailsFlow = { albumId, _ ->
        autoAlbumsUseCase.observeAutoAlbum(albumId)
            .map { (photoEntries, people) ->
                AlbumDetails(
                    title = Title.Text(photoEntries.firstOrNull()?.title ?: ""),
                    people = with(remoteMediaUseCase) {
                        people.map { person ->
                            person.toPerson { it.toRemoteUrl() }
                        }
                    },
                    albums = photoEntries.groupBy { entry ->
                        dateDisplayer.dateString(entry.timestamp)
                    }.entries.map { (date, photos) ->
                        Album(
                            id = date,
                            displayTitle = date,
                            location = null,
                            photos = photos.map {
                                MediaItem(
                                    id = it.photoId.toString(),
                                    thumbnailUri = with(remoteMediaUseCase) {
                                        it.photoId.toThumbnailUrlFromIdNullable()
                                    },
                                    fullResUri = with(remoteMediaUseCase) {
                                        it.photoId.toFullSizeUrlFromIdNullable(it.video ?: false)
                                    },
                                    displayDayDate = date,
                                    isFavourite = it.isFavorite ?: false,
                                    isVideo = it.video ?: false,
                                )
                            }
                        )
                    }
                )
            }
    },
    mediaSequenceDataSource = { AutoAlbum(it) }
)
