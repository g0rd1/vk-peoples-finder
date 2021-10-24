package ru.g0rd1.peoplesfinder.ui.userDetail.fsm

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import io.reactivex.Observable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import ru.g0rd1.peoplesfinder.repo.user.history.UserHistoryRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.ui.userDetail.model.UserResult
import ru.g0rd1.peoplesfinder.util.observeOnUI
import javax.inject.Inject

object UserDetailMultiple {

    data class State(
        val userResults: List<UserResult>,
        val currentUser: User?,
        val loading: Boolean,
    )

    sealed class Wish {
        object ToPreviousUser : Wish()
        object ToNextUsers : Wish()
        object LoadUsers : Wish()
    }

    sealed class Effect {
        object LoadingStarted : Effect()
        data class UsersLoaded(val users: List<UserResult>) : Effect()
        object ToNextUser : Effect()
        object ToPreviousUser : Effect()
    }

}

class UserDetailMultipleFeature @Inject constructor(
    actor: UserDetailMultipleActor,
    reducer: UserDetailMultipleReducer,
) : ActorReducerFeature<UserDetailMultiple.Wish, UserDetailMultiple.Effect, UserDetailMultiple.State, Nothing>(
    initialState = UserDetailMultiple.State(
        userResults = listOf(),
        currentUser = null,
        loading = true
    ),
    actor = actor,
    reducer = reducer,
) {

    init {
        this.accept(UserDetailMultiple.Wish.LoadUsers)
    }

    fun toNext() {
        this.accept(UserDetailMultiple.Wish.ToNextUsers)
    }

    fun toPrevious() {
        this.accept(UserDetailMultiple.Wish.ToPreviousUser)
    }

}

class UserDetailMultipleActor @Inject constructor(
    private val userRepo: LocalUsersRepo,
    private val userHistoryRepo: UserHistoryRepo,
) : Actor<UserDetailMultiple.State, UserDetailMultiple.Wish, UserDetailMultiple.Effect> {

    override fun invoke(
        state: UserDetailMultiple.State,
        action: UserDetailMultiple.Wish,
    ): Observable<out UserDetailMultiple.Effect> {

        return when (action) {
            UserDetailMultiple.Wish.LoadUsers -> {
                if (state.currentUser == null) {
                    getLoadUserEffectObservable(state.currentUser?.id)
                } else {
                    Observable.empty()
                }
            }
            UserDetailMultiple.Wish.ToNextUsers -> {
                val needLoadCondition =
                    state.userResults.takeLast(LOAD_GAP).map { it.user.id }.contains(state.currentUser?.id)
                if (needLoadCondition) {
                    Observable.just<UserDetailMultiple.Effect>(UserDetailMultiple.Effect.ToNextUser).concatWith(
                        if (state.loading) {
                            Observable.empty()
                        } else {
                            getLoadUserEffectObservable(state.currentUser?.id)
                        }
                    )
                } else {
                    Observable.just(UserDetailMultiple.Effect.ToNextUser)
                }
            }
            UserDetailMultiple.Wish.ToPreviousUser -> {
                val needLoadCondition =
                    state.userResults.take(LOAD_GAP).map { it.user.id }.contains(state.currentUser?.id)
                if (needLoadCondition) {
                    Observable.just<UserDetailMultiple.Effect>(UserDetailMultiple.Effect.ToPreviousUser).concatWith(
                        if (state.loading) {
                            Observable.empty()
                        } else {
                            getLoadUserEffectObservable(state.currentUser?.id)
                        }
                    )
                } else {
                    Observable.just(UserDetailMultiple.Effect.ToPreviousUser)
                }
            }
        }.observeOnUI()
    }

    private fun getLoadUserEffectObservable(userId: Int?): Observable<UserDetailMultiple.Effect> {
        return getLoadUsersSingle(userId)
            .map<UserDetailMultiple.Effect> { UserDetailMultiple.Effect.UsersLoaded(it) }
            .toObservable()
            .startWith(UserDetailMultiple.Effect.LoadingStarted)
            .retry()
    }

    private fun getLoadUsersSingle(lastUserId: Int?): Single<List<UserResult>> {
        val currentIdSingle = if (lastUserId != null) {
            userHistoryRepo.getHistoryId(lastUserId)
        } else {
            userHistoryRepo.getLastId()
        }.toSingle(0)
        return currentIdSingle.flatMap { currentUserHistoryId ->
            getCurrentUserAndPreviousInHistoryUsers(currentUserHistoryId)
                .flatMap { currentAndPreviousHistoryUsers ->
                    getNextInHistoryUsers(currentUserHistoryId, currentAndPreviousHistoryUsers)
                        .flatMap { nextHistoryUsers ->
                            getHistoryAndFromSearchUsers(currentAndPreviousHistoryUsers, nextHistoryUsers)
                        }
                }
        }
    }

    private fun getCurrentUserAndPreviousInHistoryUsers(currentUserHistoryId: Int) =
        userHistoryRepo.getUserAndPreviousUsers(currentUserHistoryId, LOAD_SIZE / 2)
            .map<List<UserResult>> {
                it.map { (historyId, user) ->
                    UserResult.FromHistory(user, historyId)
                }
            }

    private fun getNextInHistoryUsers(
        currentUserHistoryId: Int,
        currentAndPreviousHistoryUsers: List<UserResult>,
    ) = userHistoryRepo.getNextUsers(
        historyId = currentUserHistoryId,
        count = LOAD_SIZE - currentAndPreviousHistoryUsers.size
    )
        .map<List<UserResult>> {
            it.map { (historyId, user) ->
                UserResult.FromHistory(user, historyId)
            }
        }

    private fun getHistoryAndFromSearchUsers(
        currentAndPreviousHistoryUsers: List<UserResult>,
        nextHistoryUsers: List<UserResult>,
    ): Single<List<UserResult>> {
        val historyUsersCount = currentAndPreviousHistoryUsers.size + nextHistoryUsers.size
        return userRepo.getUsersWithSameGroupsCountWithFilters(
            count = LOAD_SIZE - historyUsersCount,
            notInUserTypes = listOf(UserType.BLOCKED)
        )
            .map { usersFromSearchResult ->
                val usersFromSearch = usersFromSearchResult.map {
                    UserResult.FromSearch(it.key)
                }
                currentAndPreviousHistoryUsers.plus(nextHistoryUsers).plus(usersFromSearch)
            }
    }

    companion object {
        private const val LOAD_GAP = 10
        private const val LOAD_SIZE = LOAD_GAP * 4
    }

}

class UserDetailMultipleReducer @Inject constructor() : Reducer<UserDetailMultiple.State, UserDetailMultiple.Effect> {
    override fun invoke(state: UserDetailMultiple.State, effect: UserDetailMultiple.Effect): UserDetailMultiple.State {
        return when (effect) {
            UserDetailMultiple.Effect.LoadingStarted -> state.copy(loading = true)
            UserDetailMultiple.Effect.ToNextUser -> {
                val currentUser = state.currentUser
                val users = state.userResults.map { it.user }
                val newCurrentUser = if (currentUser != null && users.map { it.id }.contains(currentUser.id)) {
                    if (currentUser.id == users.last().id) {
                        currentUser
                    } else {
                        val currentIndex = users.indexOfFirst { it.id == currentUser.id }
                        users[currentIndex + 1]
                    }
                } else {
                    currentUser
                }
                state.copy(currentUser = newCurrentUser)
            }
            UserDetailMultiple.Effect.ToPreviousUser -> {
                val currentUser = state.currentUser
                val users = state.userResults.map { it.user }
                val newCurrentUser = if (currentUser != null && users.map { it.id }.contains(currentUser.id)) {
                    if (currentUser.id == users.first().id) {
                        currentUser
                    } else {
                        val currentIndex = users.indexOfFirst { it.id == currentUser.id }
                        users[currentIndex - 1]
                    }
                } else {
                    currentUser
                }
                state.copy(currentUser = newCurrentUser)
            }
            is UserDetailMultiple.Effect.UsersLoaded -> {
                val newUsers = effect.users.sorted()
                val newHistoryUsers = newUsers.filterIsInstance<UserResult.FromHistory>()
                val currentUser = state.currentUser ?: (newHistoryUsers.lastOrNull() ?: newUsers.firstOrNull())?.user
                state.copy(
                    loading = false,
                    userResults = newUsers,
                    currentUser = currentUser
                )
            }
        }
    }

}


