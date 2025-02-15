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
package com.savvasdalkitsis.uhuruphotos.api.albumpage.seam

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.Person

sealed class AlbumPageAction {
    object SwipeToRefresh : AlbumPageAction()
    object NavigateBack : AlbumPageAction()

    data class LoadAlbum(val albumId: Int) : AlbumPageAction()
    data class SelectedPhoto(
        val mediaItem: MediaItem,
        val center: Offset,
        val scale: Float,
    ) : AlbumPageAction()

    data class PersonSelected(val person: Person) : AlbumPageAction()
    data class ChangeFeedDisplay(val feedDisplay: FeedDisplay) : AlbumPageAction()
}
