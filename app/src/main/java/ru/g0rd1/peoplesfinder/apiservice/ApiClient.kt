package ru.g0rd1.peoplesfinder.apiservice

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.g0rd1.peoplesfinder.apiservice.response.GetGroupMembersResponse
import ru.g0rd1.peoplesfinder.apiservice.response.GetGroupsResponse
import ru.g0rd1.peoplesfinder.apiservice.response.GetUserResponse

interface ApiClient {

    @GET("groups.get")
    fun getGroups(
        @Query("user_id") userId: String,
        @Query("extended") extended: Int = 1,
        @Query("fields") fields: String = "members_count",
        @Query("offset") offset: Int = 0,
        @Query("count") count: Int,
        @Query(ACCESS_TOKEN_QUERY) accessToken: String,
        @Query(VERSION_QUERY) version: String = API_VERSION
    ): Single<GetGroupsResponse>

    @GET("execute")
    fun getGroupMembers(
        @Query("code") code: String,
        @Query(ACCESS_TOKEN_QUERY) accessToken: String,
        @Query(VERSION_QUERY) version: String = API_VERSION
    ): Single<GetGroupMembersResponse>

    @GET("users.get")
    fun getUser(
        @Query("fields") fields: String = USER_DEFAULT_FIELDS
    ): Single<GetUserResponse>

    companion object {
        private const val API_VERSION = "5.21"
        private const val ACCESS_TOKEN_QUERY = "access_token"
        private const val VERSION_QUERY = "v"
        private const val USER_DEFAULT_FIELDS =
            "bdate, city, sex, has_photo, photo_200, last_seen, relation"

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