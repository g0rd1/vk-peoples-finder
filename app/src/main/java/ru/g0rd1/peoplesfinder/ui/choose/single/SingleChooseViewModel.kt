package ru.g0rd1.peoplesfinder.ui.choose.single

import androidx.annotation.CallSuper
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import io.reactivex.internal.schedulers.NewThreadScheduler
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.base.ItemClickListener
import java.util.concurrent.TimeUnit

abstract class SingleChooseViewModel<T> : BaseViewModel(), ItemClickListener<SingleChooseItemViewData<T>> {

    val searchText = ObservableField<String>("")

    val loaderVisible = ObservableBoolean()

    val items = ObservableField<List<SingleChooseItemViewData<*>>>(listOf())

    val errorText = ObservableField("")

    val errorVisible = ObservableBoolean()

    abstract val cancelChoiceVisible: Boolean

    abstract val title: String

    abstract val searchTextHint: String

    private val searchTextSubject: Subject<String> = BehaviorSubject.createDefault("")

    @CallSuper
    override fun onStart() {
        searchText.addOnPropertyChangedCallback(onSearchTextChanged)
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        searchText.removeOnPropertyChangedCallback(onSearchTextChanged)
    }

    protected fun observeSearchText(): io.reactivex.Observable<String> {
        return searchTextSubject.debounce(SEARCH_THROTTLE, TimeUnit.MILLISECONDS, NewThreadScheduler())
    }

    protected fun setError(errorText: String) {
        this.errorText.set(errorText)
        errorVisible.set(true)
    }

    protected fun clearError() {
        errorText.set("")
        errorVisible.set(false)
    }

    abstract fun cancelChoice()

    abstract fun close()

    private val onSearchTextChanged = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            searchTextSubject.onNext(searchText.get() ?: "")
        }
    }

    companion object {
        private const val SEARCH_THROTTLE = 1000L
    }

}