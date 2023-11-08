package com.a2zsuvidhaa.`in`.fragment.report

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.a2zsuvidhaa.`in`.data.model.report.LedgerReport
import com.a2zsuvidhaa.`in`.data.repository.ReportRepository
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData

class LedgerPagingSource(
    private val reportRepository: ReportRepository,
    private val queryParam: FieldMapData
) : PagingSource<Int, LedgerReport>() {
    override fun getRefreshKey(state: PagingState<Int, LedgerReport>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LedgerReport> {
        return try {
            queryParam["page"] = (params.key ?: FIRST_PAGE_INDEX).toString()
            val response = reportRepository.ledgerReport(data = queryParam)

            if (response.status == 1) {
                var nextPageNumber = if(response.nextPage == 0) null else response.nextPage
                val prevPageNumber = if(response.prevPage == 0) null else response.prevPage


                if(response.reports == null){
                    nextPageNumber = null
                }

                if(response.reports!!.isEmpty()){
                    nextPageNumber = null
                }
                if(response.reports!!.size < 30){
                    nextPageNumber = null
                }

                if(response.nextPage == 30){
                    nextPageNumber = null
                }

                LoadResult.Page(
                    data = response.reports!!,
                    prevKey = prevPageNumber,
                    nextKey = nextPageNumber
                )
            } else {
                LoadResult.Error(Exception("Something went wrong! please contact with admin!"))
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    companion object {
        const val FIRST_PAGE_INDEX = 1
    }

}