package ru.g0rd1.peoplesfinder.apiservice

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.g0rd1.peoplesfinder.apiservice.model.*
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

    @GET("groups.getMembers")
    fun getGroupMembers(
        @Query("group_id") groupId: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int = 1000,
        @Query("sort") sort: String? = null,
        @Query("fields") fields: String = USER_DEFAULT_FIELDS,
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

    @GET("photos.getProfile")
    fun getProfilePhotos(
        @Query("owner_id") ownerId: Int,
        @Query("extended") extended: Int = 0,
        @Query("offset") offset: Int = 0,
        @Query("count") count: Int = 1000,
        @Query("photo_sizes") photoSizes: Int = 1,
        @Query("rev") rev: Int = 1,
        @Query(ACCESS_TOKEN_QUERY) accessToken: String = getAccessToken(),
        @Query(VERSION_QUERY) version: String = API_VERSION,
    ): Single<ApiVkResponse<ApiPhoto>>

    @GET("execute")
    fun execute(
        @Query("code") code: String,
        @Query(ACCESS_TOKEN_QUERY) accessToken: String = getAccessToken(),
        @Query(VERSION_QUERY) version: String = API_VERSION,
    ): Single<ApiVkResponse<ApiUser>>

    @GET("groups.search")
    fun searchGroups(
        @Query("q") searchText: String,
        @Query("count") count: Int = 1000,
        @Query(ACCESS_TOKEN_QUERY) accessToken: String = getAccessToken(),
        @Query(VERSION_QUERY) version: String = API_VERSION,
    ): Single<ApiVkResponse<ApiGroup>>

    companion object {
        const val API_VERSION = "5.89"
        private const val ACCESS_TOKEN_QUERY = "access_token"
        private const val VERSION_QUERY = "v"
        private const val USER_DEFAULT_FIELDS =
            "bdate, country, city, sex, has_photo, relation, photo_100, photo_max_orig"
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