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
package com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam

import com.savvasdalkitsis.uhuruphotos.api.navigation.Navigator
import com.savvasdalkitsis.uhuruphotos.api.seam.EffectHandler
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.api.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.api.useralbum.navigation.UserAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsEffect.ErrorLoadingAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsEffect.NavigateToUserAlbum
import javax.inject.Inject

class UserAlbumsEffectHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
): EffectHandler<UserAlbumsEffect> {

    override suspend fun handleEffect(effect: UserAlbumsEffect) {
        when (effect) {
            ErrorLoadingAlbums -> toaster.show(string.error_loading_user_albums)
            NavigateBack -> navigator.navigateBack()
            is NavigateToUserAlbum ->
                navigator.navigateTo(UserAlbumNavigationTarget.name(effect.album.id))
        }
    }
}