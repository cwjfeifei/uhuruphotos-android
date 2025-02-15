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
package com.savvasdalkitsis.uhuruphotos.implementation.media.local.repository

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.service.LocalMediaService
import javax.inject.Inject

class MediaStoreVersionRepository @Inject constructor(
    private val localMediaService: LocalMediaService,
    flowSharedPreferences: FlowSharedPreferences,
) {
    private val mediaStoreSavedVersion = flowSharedPreferences.getString("media_store_version", "")

    var currentMediaStoreVersion
        get() = mediaStoreSavedVersion.get()
        set(value) { mediaStoreSavedVersion.set(value) }

    val latestMediaStoreVersion get() = localMediaService.mediaStoreCurrentVersion
}