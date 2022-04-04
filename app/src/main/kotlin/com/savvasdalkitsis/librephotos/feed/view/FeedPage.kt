package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.librephotos.account.view.AccountOverviewPopUp
import com.savvasdalkitsis.librephotos.extensions.blurIf
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction.*
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.home.view.HomeScaffold
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider

@ExperimentalAnimationApi
@Composable
fun FeedPage(
    controllersProvider: ControllersProvider,
    state: FeedPageState,
    action: (FeedAction) -> Unit,
) {
    HomeScaffold(
        modifier = Modifier
            .blurIf(state.showAccountOverview),
        controllersProvider.navController!!,
        userBadgeState = state.userBadgeState,
        userBadgePressed = { action(UserBadgePressed) },
    ) { contentPadding ->
        Feed(contentPadding, state.feedState)
        AccountOverviewPopUp(
            visible = state.showAccountOverview,
            userBadgeState = state.userBadgeState,
            onDismiss = { action(DismissAccountOverview) },
            onLogoutClicked = { action(LogOut) },
        )
    }
}