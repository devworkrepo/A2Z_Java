package com.a2zsuvidhaa.`in`.activity.register.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.a2zsuvidhaa.`in`.data.repository.RegistrationRepository
import com.a2zsuvidhaa.`in`.data.response.RegisterInCompleteUser
import com.a2zsuvidhaa.`in`.data.response.RegistrationRoleUser
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData

class InCompleteUserPagingSource(
    private val reportRepository: RegistrationRepository,
    private val queryParam: FieldMapData
) : PagingSource<Int, RegisterInCompleteUser>() {
    override fun getRefreshKey(state: PagingState<Int, RegisterInCompleteUser>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RegisterInCompleteUser> {
        return try {
            queryParam["page"] = (params.key ?: FIRST_PAGE_INDEX).toString()

            val response = reportRepository.fetchInCompletedUserList(data = queryParam)

            if (response.status == 1) {
                var nextPageNumber = if(response.nextPage == 0) null else response.nextPage
                val prevPageNumber = if(response.prevPage == 0) null else response.prevPage


                if(response.members == null){
                    nextPageNumber = null
                }

                if(response.members!!.isEmpty()){
                    nextPageNumber = null
                }
                if(response.members.size < 30){
                    nextPageNumber = null
                }

                if(response.nextPage == 30){
                    nextPageNumber = null
                }

                LoadResult.Page(
                    data = response.members,
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