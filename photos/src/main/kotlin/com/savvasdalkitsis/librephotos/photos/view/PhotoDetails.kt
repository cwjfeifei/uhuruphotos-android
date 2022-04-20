package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.radusalagean.infobarcompose.InfoBar
import com.radusalagean.infobarcompose.InfoBarMessage
import com.savvasdalkitsis.librephotos.image.view.Image
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.ui.view.zoom.ZoomableState
import com.savvasdalkitsis.librephotos.ui.view.zoom.zoomable
import com.savvasdalkitsis.librephotos.video.view.Video

@Composable
fun PhotoDetails(
    zoomableState: ZoomableState,
    action: (PhotoAction) -> Unit,
    state: PhotoState,
    contentPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .zoomable(
                zoomableState = zoomableState,
                onTap = { action(PhotoAction.ToggleUI) },
                onSwipeAway = { action(PhotoAction.NavigateBack) },
                onSwipeUp = { action(PhotoAction.ShowInfo) },
            )
    ) {
        when {
            state.isVideo -> Video(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                videoUrl = state.fullResUrl,
                videoThumbnailUrl = state.lowResUrl,
                play = true,
            )
            else -> Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                lowResUrl = state.lowResUrl,
                fullResUrl = state.fullResUrl,
                onFullResImageLoaded = { action(PhotoAction.FullImageLoaded) },
                contentScale = ContentScale.Fit,
                contentDescription = "photo",
            )
        }
        Column {
            Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
            InfoBar(offeredMessage = state.errorMessage?.let { InfoBarMessage(it) }) {
                action(PhotoAction.DismissErrorMessage)
            }
        }
        if (state.showPhotoDeletionConfirmationDialog) {
            DeletePermissionDialog(
                photoCount = 1,
                onDismiss = { action(PhotoAction.DismissPhotoDeletionDialog) }
            ) { action(PhotoAction.DeletePhoto) }
        }
    }
}