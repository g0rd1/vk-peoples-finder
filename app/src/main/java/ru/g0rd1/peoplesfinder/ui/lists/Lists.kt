package ru.g0rd1.peoplesfinder.ui.lists

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType

@Composable
@Preview(
    showBackground = true,
    heightDp = 320,
    widthDp = 180,
)
fun ListsView(
    @PreviewParameter(PreviewListsViewData::class) listsViewData: State<ListsViewData>,
    onFavoriteTabClick: () -> Unit = { },
    onBlockedTabClick: () -> Unit = { },
) {
    when (val listsViewDataValue = listsViewData.value) {
        is ListsViewData.Data -> ListsViewData(listsViewDataValue, onFavoriteTabClick, onBlockedTabClick)
        ListsViewData.Loading -> ListsViewLoading()
    }
}

@Composable
private fun ListsViewData(
    @PreviewParameter(PreviewListsViewData::class) listsViewData: ListsViewData.Data,
    onFavoriteTabClick: () -> Unit,
    onBlockedTabClick: () -> Unit,
) {
    Column {
        TabRow(
            selectedTabIndex = listsViewData.userType.id - 1,
        ) {
            Tab(selected = listsViewData.userType == UserType.FAVORITE, onClick = onFavoriteTabClick) {
                Text(text = UserType.FAVORITE.name)
            }
            Tab(selected = listsViewData.userType == UserType.BLOCKED, onClick = onBlockedTabClick) {
                Text(text = UserType.BLOCKED.name)
            }
        }
        LazyColumn {
            items(listsViewData.users) { user: User ->
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = if (user.photo100 != null) rememberImagePainter(user.photo100) else painterResource(R.drawable.ic_person),
                        contentDescription = "User photo thumbnail",
                        modifier = Modifier.size(100.dp),
                    )
                    Text(text = "${user.firstName} ${user.lastName}")
                    Image(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Delete user from this group",
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun ListsViewLoading() {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}