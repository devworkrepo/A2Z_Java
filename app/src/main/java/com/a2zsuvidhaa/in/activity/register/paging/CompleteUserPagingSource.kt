package com.a2zsuvidhaa.`in`.activity.register.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.a2zsuvidhaa.`in`.data.repository.RegistrationRepository
import com.a2zsuvidhaa.`in`.data.response.RegisterCompleteUser
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData

class CompleteUserPagingSource(
    private val reportRepository: RegistrationRepository,
    private val queryParam: FieldMapData
) : PagingSource<Int, RegisterCompleteUser>() {
    override fun getRefreshKey(state: PagingState<Int, RegisterCompleteUser>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RegisterCompleteUser> {
        return try {
            queryParam["page"] = (params.key ?: FIRST_PAGE_INDEX).toString()

            val response = reportRepository.fetchCompletedUserList(data = queryParam)

            if (response.status == 1) {
                var nextPageNumber = if(response.nextPage == 0) null else response.nextPage
                val prevPageNumber = if(response.prevPage == 0) null else response.prevPage


                if(response.listData == null){
                    nextPageNumber = null
                }

                if(response.listData!!.isEmpty()){
                    nextPageNumber = null
                }
                if(response.listData.size < 30){
                    nextPageNumber = null
                }

                if(response.nextPage == 30){
                    nextPageNumber = null
                }

                LoadResult.Page(
                    data = response.listData,
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