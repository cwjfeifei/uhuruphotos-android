package com.savvasdalkitsis.uhuruphotos.account.usecase

import com.savvasdalkitsis.uhuruphotos.db.albums.AlbumsQueries
import com.savvasdalkitsis.uhuruphotos.db.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.db.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.db.user.UserQueries
import com.savvasdalkitsis.uhuruphotos.image.cache.ImageCacheController
import com.savvasdalkitsis.uhuruphotos.worker.WorkScheduler
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import org.junit.Test

class AccountUseCaseTest {

    private val userQueries = mockk<UserQueries>(relaxed = true)
    private val albumsQueries = mockk<AlbumsQueries>(relaxed = true)
    private val searchQueries = mockk<SearchQueries>(relaxed = true)
    private val tokenQueries = mockk<TokenQueries>(relaxed = true)
    private val imageCacheController = mockk<ImageCacheController>(relaxed = true)
    private val videoCache = mockk<Cache>(relaxed = true)
    private val workScheduler = mockk<WorkScheduler>(relaxed = true)

    private val underTest = AccountUseCase(
        userQueries,
        albumsQueries,
        searchQueries,
        tokenQueries,
        imageCacheController,
        videoCache,
        workScheduler,
    )

    @Test
    fun `cancels all scheduled work when logging out`() = runBlocking {
        underTest.logOut()

        verify { workScheduler.cancelAllScheduledWork() }
    }

    @Test
    fun `clears albums when logging out`() = runBlocking {
        underTest.logOut()

        verify { albumsQueries.clearAlbums() }
    }

    @Test
    fun `clears search results when logging out`() = runBlocking {
        underTest.logOut()

        verify { searchQueries.clearSearchResults() }
    }

    @Test
    fun `deletes user when logging out`() = runBlocking {
        underTest.logOut()

        verify { userQueries.deleteUser() }
    }

    @Test
    fun `removes all tokens when logging out`() = runBlocking {
        underTest.logOut()

        verify { tokenQueries.removeAllTokens() }
    }

    @Test
    fun `clears image cache when logging out`() = runBlocking {
        underTest.logOut()

        verify { imageCacheController.clear() }
    }

    @Test
    fun `clears video cache when logging out`() = runBlocking {
        underTest.logOut()

        verify { videoCache.evictAll() }
    }
}