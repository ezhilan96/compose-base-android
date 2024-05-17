package com.compose.base.domain.useCases.home

import com.compose.base.data.util.DataState
import com.compose.base.domain.entity.ListData
import com.compose.base.domain.repository.HomeRepository
import com.compose.base.domain.useCases.core.MapEntityUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetListUseCase @Inject constructor(private val repo: HomeRepository) {
    private val mapResponseToEntity = MapEntityUseCase()
    private val list: MutableList<ListData> = mutableListOf()
    private var pageCount = 0

    operator fun invoke(pagingEnabled: Boolean = false): Flow<DataState<List<ListData>>> {
        if (!pagingEnabled) {
            pageCount = 0
            list.clear()
        }
        return repo.getBookingList(skip = pageCount * 10, isPendingList = true).map { dataState ->
            when (dataState) {
                is DataState.InProgress -> dataState

                is DataState.Success -> {
                    if (dataState.data.data?.size == 10) {
                        pageCount++
                    }
                    val newList = dataState.data.data?.mapNotNull { mapResponseToEntity(it) }
                    if (!newList.isNullOrEmpty()) {
                        list.addAll(newList)
                    }
                    DataState.Success(list.distinctBy { "${it.id}${it.date}${it.time}" })
                }

                is DataState.Error -> {
                    if (pageCount > 0) {
                        invoke(pagingEnabled = false).filter { it !is DataState.InProgress }
                            .first()
                    } else {
                        dataState
                    }
                }
            }
        }
    }
}