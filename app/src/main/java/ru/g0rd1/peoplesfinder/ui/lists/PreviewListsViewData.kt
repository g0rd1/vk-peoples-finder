package ru.g0rd1.peoplesfinder.ui.lists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ru.g0rd1.peoplesfinder.common.enums.Relation
import ru.g0rd1.peoplesfinder.common.enums.Sex
import ru.g0rd1.peoplesfinder.model.City
import ru.g0rd1.peoplesfinder.model.Country
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import java.time.LocalDate

class PreviewListsViewData : PreviewParameterProvider<ListsViewData> {

    override val values = listOf<ListsViewData>(
        ListsViewData.Data(
            userType = UserType.FAVORITE,
            users = listOf(
                User(
                    id = 0,
                    firstName = "Иван",
                    lastName = "Иванов",
                    deactivated = null,
                    isClosed = false,
                    birthday = LocalDate.now().minusYears(25),
                    city = City(id = 0, title = "Курган", false),
                    country = Country(id = 0, title = "Россия"),
                    sex = Sex.MALE,
                    relation = Relation.SINGLE,
                    hasPhoto = true,
                    photo100 = "https://chudo-udo.info/media/k2/items/cache/f7c6b266b48b52b888acd15686984fae_XL.jpg",
                    photoMax = "https://chudo-udo.info/media/k2/items/cache/f7c6b266b48b52b888acd15686984fae_XL.jpg"
                ),
                User(
                    id = 1,
                    firstName = "Пётр",
                    lastName = "Петров",
                    deactivated = null,
                    isClosed = false,
                    birthday = LocalDate.now().minusYears(20),
                    city = City(id = 1, title = "Петропавловск", false),
                    country = Country(id = 1, title = "Казахстан"),
                    sex = Sex.MALE,
                    relation = Relation.ENGAGED,
                    hasPhoto = true,
                    photo100 = "https://memepedia.ru/wp-content/uploads/2018/08/mi0-768x490.jpg",
                    photoMax = "https://memepedia.ru/wp-content/uploads/2018/08/mi0-768x490.jpg"
                )
            )
        )
    ).asSequence()

    override val count: Int = values.count()
}
