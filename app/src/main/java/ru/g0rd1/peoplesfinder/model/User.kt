package ru.g0rd1.peoplesfinder.model

import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("id")
    val userId: Int,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("deactivated")
    val deactivated: String?,

    @SerializedName("is_closed")
    val isClosed: Boolean,

    @SerializedName("bdate")
    val birthDate: String?,

    @SerializedName("city")
    val city: City?,

    @SerializedName("sex")
    val sex: Int?,

    @SerializedName("photo_200")
    val photo: String?,

    @SerializedName("last_seen")
    val lastSeen: LastSeen?,

    @SerializedName("relation")
    val relation: Int?

) {
    data class LastSeen(
        val time: Int,
        val platform: Int
    ) {
        class Platform {
            companion object {
                const val MOBILE = 1
                const val IPHONE = 2
                const val IPAD = 3
                const val ANDROID = 4
                const val WINDOWS_PHONE = 5
                const val WINDOWS_10 = 6
                const val SITE = 7
            }
        }
    }

    data class City(
        val id: Int,
        val title: String
    )

    class Sex {
        companion object {
            const val NOT_SPECIFIED = 0
            const val FEMALE = 1
            const val MALE = 2
        }
    }

    class RelationType {
        companion object {
            const val NOT_SPECIFIED = 0
            const val SINGLE = 1
            const val IN_A_RELATIONSHIP = 2
            const val ENGAGED = 3
            const val MARRIED = 4
            const val IT_IS_COMPLICATED = 5
            const val ACTIVELY_SEARCHING = 6
            const val IN_LOVE = 7
            const val IN_A_CIVIL_UNION = 8
        }
    }

}