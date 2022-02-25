package com.example.testapp.ui.viemodels

import android.os.Parcelable
import androidx.lifecycle.*
import androidx.paging.*
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.data.Repository
import com.example.testapp.data.models.MenuItem
import com.example.testapp.paging.GetListPagingSource
import com.example.testapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val listItemsMutable = MutableLiveData<List<MenuItem>>(emptyList())

    private val showProgressBarMutable = MutableLiveData(false)
    val progressBarVisible: LiveData<Boolean>
        get() = showProgressBarMutable

    private val serverConnectErrorEvent = SingleLiveEvent<String>()
    val serverConnectError: LiveData<String>
        get() = serverConnectErrorEvent

    fun flowGetListBreweries(queryKey: String): Flow<PagingData<MenuItem>> {
        return Pager(
            PagingConfig(pageSize = 20)
        ) {
            GetListPagingSource(queryKey, ::getList)
        }.flow
            .cachedIn(viewModelScope)
    }

    private suspend fun getList(queryKey: String, page: Int): List<MenuItem> {
        return withContext(Dispatchers.IO) {
            try {
                showProgressBar()
                repository.getItems(queryKey, page).also { receivedList ->
                    hideProgressBar()
                    addNewListToLiveData(receivedList)
                }
            } catch (t: Throwable) {
                hideProgressBar()
                serverConnectErrorEvent.postValue(t.message)
                return@withContext emptyList()
            }
        }
    }

    private fun addNewListToLiveData(newList: List<MenuItem>) {
        val sumList = listItemsMutable.value?.plus(newList)
        if (sumList != null) {
            listItemsMutable.postValue(sumList!!)
        }
    }

    private fun showProgressBar() {
        showProgressBarMutable.postValue(true)
    }

    private fun hideProgressBar() {
        showProgressBarMutable.postValue(false)
    }

}





