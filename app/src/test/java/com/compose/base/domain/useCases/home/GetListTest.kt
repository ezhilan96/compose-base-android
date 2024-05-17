package com.compose.base.domain.useCases.home

import com.compose.base.core.Constants
import com.compose.base.data.model.remote.response.BookingListResponse
import com.compose.base.data.model.remote.response.ListResponse
import com.compose.base.data.util.DataState
import com.compose.base.domain.entity.BookingType
import com.compose.base.domain.entity.b2bStatusDataList
import com.compose.base.domain.entity.b2cStatusDataList
import com.compose.base.domain.entity.etsStatusDataList
import com.compose.base.domain.entity.ondcStatusDataList
import com.compose.base.domain.repository.HomeRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

class GetListTest {

    private lateinit var getPendingBookingList: GetListUseCase
    private val mockHomeRepository = mockk<HomeRepository>(relaxed = true)

    @Before
    fun setUp() {
        getPendingBookingList = GetListUseCase(mockHomeRepository)
    }

    @Test
    fun `get pending list - paging disabled - success result`() {
        val fullPage = DataState.Success(ListResponse(data = List(10) {
            createBookingListResponse()
        }))
        val skipSlot = slot<Int>()
        coEvery {
            mockHomeRepository.getBookingList(capture(skipSlot))
        } returns flowOf(fullPage)
        runTest {
            val firstPageResult = getPendingBookingList(pagingEnabled = true).first()
            assertThat(skipSlot.captured).isEqualTo(0)
            assertThat(firstPageResult).isInstanceOf(DataState.Success::class.java)
            assertThat((firstPageResult as DataState.Success).data.size).isEqualTo(10)
        }
    }

    @Test
    fun `get pending list - paging disabled - empty result`() {
        val fullPage = DataState.Success(ListResponse(data = List(0) {
            createBookingListResponse()
        }))
        val skipSlot = slot<Int>()
        coEvery {
            mockHomeRepository.getBookingList(capture(skipSlot))
        } returns flowOf(fullPage)
        runTest {
            val firstPageResult = getPendingBookingList(pagingEnabled = true).first()
            assertThat(skipSlot.captured).isEqualTo(0)
            assertThat(firstPageResult).isInstanceOf(DataState.Success::class.java)
            assertThat((firstPageResult as DataState.Success).data.size).isEqualTo(0)
        }
    }

    @Test
    fun `get pending list - paging disabled - error result`() {
        val dataState = DataState.Error.Remote()
        val skipSlot = slot<Int>()
        coEvery {
            mockHomeRepository.getBookingList(capture(skipSlot))
        } returns flowOf(dataState)
        runTest {
            val firstPageResult = getPendingBookingList(pagingEnabled = true).first()
            assertThat(skipSlot.captured).isEqualTo(0)
            assertThat(firstPageResult).isInstanceOf(DataState.Error.Remote::class.java)
        }
    }

    @Test
    fun `get pending list - paging enable - result with a sequence of full, half and empty page`() {
        val fullPage = DataState.Success(ListResponse(data = List(10) {
            createBookingListResponse()
        }))
        val halfPage = DataState.Success(ListResponse(data = List(5) {
            createBookingListResponse()
        }))
        val emptyPage = DataState.Success(ListResponse(data = listOf<BookingListResponse>()))
        val skipSlot = slot<Int>()
        coEvery {
            mockHomeRepository.getBookingList(capture(skipSlot))
        } returns flowOf(fullPage) andThen flowOf(halfPage) andThen flowOf(emptyPage)
        runTest {
            val firstPageResult = getPendingBookingList(pagingEnabled = true).first()
            assertThat(skipSlot.captured).isEqualTo(0)
            assertThat(firstPageResult).isInstanceOf(DataState.Success::class.java)
            assertThat((firstPageResult as DataState.Success).data.size).isEqualTo(10)
            val secondPageResult = getPendingBookingList(pagingEnabled = true).first()
            assertThat(skipSlot.captured).isEqualTo(10)
            assertThat(secondPageResult).isInstanceOf(DataState.Success::class.java)
            assertThat((secondPageResult as DataState.Success).data.size).isEqualTo(15)
            val thirdResult = getPendingBookingList(pagingEnabled = true).first()
            assertThat(skipSlot.captured).isEqualTo(10)
            assertThat(thirdResult).isInstanceOf(DataState.Success::class.java)
            assertThat((thirdResult as DataState.Success).data.size).isEqualTo(15)
        }
    }

    @Test
    fun `get pending list - paging enable - result with a sequence of full, full and error page`() {
        val firstPage = DataState.Success(ListResponse(data = List(10) {
            createBookingListResponse()
        }))
        val secondPage = DataState.Success(ListResponse(data = List(10) {
            createBookingListResponse()
        }))
        val errorDataState = DataState.Error.Remote()
        val skips = mutableListOf<Int>()
        coEvery {
            mockHomeRepository.getBookingList(capture(skips))
        } returns flowOf(firstPage) andThen flowOf(secondPage) andThen flowOf(errorDataState) andThen flowOf(
            firstPage
        )
        runTest {
            val firstPageResult = getPendingBookingList(pagingEnabled = true).first()
            assertThat(skips[0]).isEqualTo(0)
            assertThat(firstPageResult).isInstanceOf(DataState.Success::class.java)
            assertThat((firstPageResult as DataState.Success).data.size).isEqualTo(10)
            val secondPageResult = getPendingBookingList(pagingEnabled = true).first()
            assertThat(skips[1]).isEqualTo(10)
            assertThat(secondPageResult).isInstanceOf(DataState.Success::class.java)
            assertThat((secondPageResult as DataState.Success).data.size).isEqualTo(20)
            val thirdResult = getPendingBookingList(pagingEnabled = true).first()
            assertThat(skips[2]).isEqualTo(20)
            assertThat(skips[3]).isEqualTo(0)
            assertThat(thirdResult).isInstanceOf(DataState.Success::class.java)
            assertThat((thirdResult as DataState.Success).data.size).isEqualTo(10)
        }
    }

    private fun createBookingListResponse(): BookingListResponse {
        val mockBookingList = mockk<BookingListResponse>(relaxed = true)
        val bookingType = pickRandomBookingType()
        every { mockBookingList.id } returns UUID.randomUUID().hashCode()
        every { mockBookingList.bookingType } returns bookingType
        every { mockBookingList.status } returns pickRandomStatus(BookingType.valueOf(bookingType))
        every { mockBookingList.travelDateAndTime } returns "2024-02-07T12:30:00.000Z"
        every { mockBookingList.tripType } returns pickRandomTripType()
        return mockBookingList
    }

    private fun pickRandomBookingType(): String {
        return BookingType.entries.map { it.toString() }.random()
    }

    private fun pickRandomTripType(): String {
        val tripTypes = listOf(
            Constants.JSON_OFFICE_PICKUP,
            Constants.JSON_HOME_PICKUP,
            Constants.JSON_BOOKRIDE,
            Constants.JSON_OUTSTATION,
            Constants.JSON_MULTICITY,
            Constants.JSON_PACKAGE,
            Constants.JSON_HOURLYRENTAL,
            Constants.JSON_LOCAL,
        )
        return tripTypes.random()
    }

    private fun pickRandomStatus(bookingType: BookingType): Int {
        val statusList = when (bookingType) {
            BookingType.customer -> b2cStatusDataList
            BookingType.taxida -> ondcStatusDataList
            BookingType.business -> b2bStatusDataList
            BookingType.ets -> etsStatusDataList
        }
        val randomStatus = statusList.filter { it.second != 0 }.random()
        return randomStatus.second
    }
}