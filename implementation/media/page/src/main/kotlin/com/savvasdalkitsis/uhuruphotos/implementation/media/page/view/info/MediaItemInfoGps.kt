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
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.api.icons.R.drawable
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.state.SingleMediaItemState

@Composable
internal fun MediaItemInfoGps(
    mediaItem: SingleMediaItemState,
    action: (com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction) -> Unit
) {
    mediaItem.gps?.let { gps ->
        Box(
            modifier = Modifier.clickable { action(com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.ClickedOnGps(gps)) },
        ) {
            MediaItemInfoIconEntry(
                icon = drawable.ic_location_pin,
            ) {
                Text("${gps.lat.round(2)}:${gps.lon.round(2)}")
            }
        }
    }
}

private fun Double.round(decimals: Int = 2): String = "%.${decimals}f".format(this)