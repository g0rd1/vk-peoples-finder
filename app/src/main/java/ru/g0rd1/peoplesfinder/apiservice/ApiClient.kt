package ru.g0rd1.peoplesfinder.apiservice

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.g0rd1.peoplesfinder.apiservice.model.ApiCity
import ru.g0rd1.peoplesfinder.apiservice.model.ApiCountry
import ru.g0rd1.peoplesfinder.apiservice.model.ApiGroup
import ru.g0rd1.peoplesfinder.apiservice.model.ApiUser
import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkResponse

interface ApiClient {

    fun getAccessToken(): String

    @GET("groups.get")
    fun getGroups(
        @Query("user_id") userId: Int,
        @Query("extended") extended: Int = 1,
        @Query("fields") fields: String = "members_count",
        @Query("offset") offset: Int = 0,
        @Query("count") count: Int,
        @Query(ACCESS_TOKEN_QUERY) accessToken: String = getAccessToken(),
        @Query(VERSION_QUERY) version: String = API_VERSION
    ): Single<ApiVkResponse<ApiGroup>>

    @GET("execute")
    fun getGroupMembers(
        @Query("code") code: String,
        @Query(ACCESS_TOKEN_QUERY) accessToken: String = getAccessToken(),
        @Query(VERSION_QUERY) version: String = API_VERSION
    ): Single<ApiVkResponse<ApiUser>>

    @GET("database.getCountries")
    fun getCountries(
        @Query("need_all") needAll: Int = NEED_ALL_COUNTRIES_DEFAULT,
        @Query("count") count: Int = 1000,
        @Query(ACCESS_TOKEN_QUERY) accessToken: String = getAccessToken(),
        @Query(VERSION_QUERY) version: String = API_VERSION
    ): Single<ApiVkResponse<ApiCountry>>

    @GET("database.getCities")
    fun getCities(
        @Query("need_all") needAll: Int = NEED_ALL_CITIES_DEFAULT,
        @Query("count") count: Int,
        @Query("country_id") countryId: Int? = null,
        @Query("q") query: String? = null,
        @Query(ACCESS_TOKEN_QUERY) accessToken: String = getAccessToken(),
        @Query(VERSION_QUERY) version: String = API_VERSION
    ): Single<ApiVkResponse<ApiCity>>

    companion object {
        private const val API_VERSION = "5.21"
        private const val ACCESS_TOKEN_QUERY = "access_token"
        private const val VERSION_QUERY = "v"
        private const val USER_DEFAULT_FIELDS =
            "bdate, city, sex, has_photo, photo_200, last_seen, relation"
        private const val NEED_ALL_COUNTRIES_DEFAULT = 1
        private const val NEED_ALL_CITIES_DEFAULT = 1

        fun getGroupMembersCode(
            groupId: String,
            offset: Int = 0,
            step: Int,
            stepsCount: Int
        ): String {
            return """var a, i, step, stepsCount, offset, groupId, b=[];
                     |i=0;
                     |step = $step;
                     |stepsCount = $stepsCount;
                     |offset = $offset;
                     |groupId = $groupId;
                     |while (i<stepsCount) {
                     |a=API.groups.getMembers({"group_id": groupId, "offset": i*step+offset, "count": step, "fields": "$USER_DEFAULT_FIELDS"});
                     |b.push(a);
                     |i=i+1;
                     |}
                     |return b;""".trimMargin()
        }
    }
}