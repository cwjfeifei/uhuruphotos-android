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
package com.savvasdalkitsis.uhuruphotos.implementation.feedpage.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.api.feed.view.FeedDisplayActionButton
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.api.icons.R.drawable
import com.savvasdalkitsis.uhuruphotos.api.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageAction.AskForSelectedPhotosTrashing
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageAction.DownloadSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageAction.ShareSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.view.state.FeedPageState

@Composable
internal fun RowScope.FeedPageActionBar(
    state: FeedPageState,
    action: (FeedPageAction) -> Unit
) {
    AnimatedVisibility(visible = state.shouldShowShareIcon) {
        ActionIcon(
            onClick = { action(ShareSelectedPhotos) },
            icon = drawable.ic_share
        )
    }
    AnimatedVisibility(visible = state.hasSelection) {
        ActionIcon(
            onClick = { action(AskForSelectedPhotosTrashing) },
            icon = drawable.ic_delete
        )
    }
    AnimatedVisibility(visible = state.hasSelection) {
        ActionIcon(
            onClick = { action(DownloadSelectedPhotos) },
            icon = drawable.ic_cloud_download
        )
    }
    AnimatedVisibility(visible = !state.hasSelection) {
        FeedDisplayActionButton(
            onChange = { action(ChangeDisplay(it as FeedDisplays)) },
            currentFeedDisplay = state.feedState.feedDisplay
        )
    }
}