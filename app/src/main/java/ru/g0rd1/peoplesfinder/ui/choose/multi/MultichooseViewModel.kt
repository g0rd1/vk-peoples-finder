package ru.g0rd1.peoplesfinder.ui.choose.multi

import androidx.annotation.CallSuper
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import java.util.concurrent.TimeUnit

abstract class MultichooseViewModel<T> : BaseViewModel() {

    val searchText = ObservableField<String>()

    val loaderVisible = ObservableBoolean()

    val dropChoiceVisible = ObservableBoolean()

    val items = ObservableField<List<MultichooseItemViewModel<*>>>(listOf())

    private val searchTextSubject: Subject<String> = PublishSubject.create()

    val errorText = ObservableField("")

    val errorVisible = ObservableBoolean()

    val onItemClick: (Int) -> Unit = ::onItemClick

    @CallSuper
    override fun onStart() {
        searchText.addOnPropertyChangedCallback(onSearchTextChanged)
        items.addOnPropertyChangedCallback(onItemsChanged)
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        searchText.removeOnPropertyChangedCallback(onSearchTextChanged)
        items.removeOnPropertyChangedCallback(onItemsChanged)
    }

    protected fun observeSearchText(): io.reactivex.Observable<String> {
        return searchTextSubject.throttleLatest(SEARCH_THROTTLE, TimeUnit.MILLISECONDS)
    }

    protected fun setError(errorText: String) {
        this.errorText.set(errorText)
        errorVisible.set(true)
    }

    protected fun clearError() {
        errorText.set("")
        errorVisible.set(false)
    }

    abstract fun onItemClick(position: Int)

    abstract fun dropChoice()

    abstract fun confirmChoice()

    private val onSearchTextChanged = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            searchTextSubject.onNext(searchText.get() ?: "")
        }
    }

    private val onItemsChanged = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            dropChoiceVisible.set(items.get()?.any { it.choosed } ?: false)
        }
    }

    companion object {
        private const val SEARCH_THROTTLE = 1000L
    }

}