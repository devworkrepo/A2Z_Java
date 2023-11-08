package com.a2zsuvidhaa.`in`.activity.register

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.activity.register.paging.RegistrationUserPagingSource
import com.a2zsuvidhaa.`in`.data.repository.RegistrationRepository
import com.a2zsuvidhaa.`in`.data.response.RegistrationRoleUser
import com.a2zsuvidhaa.`in`.util.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RegistrationRoleMapViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository,
    private val appPreference: AppPreference
) : BaseViewModel() {

    var isFetchDistributor = false

    var userType: UserType? = null
    var userMapperType: UserType? = null
    lateinit var createUserType : UserType
    val userIds = arrayListOf<String>()

    var titleObs = MutableLiveData<String>()
    var inputMode =""
    var inputText =""
    var fosId = 1

    init {
        setUserMapperType()

        setTitle()
    }

    fun fetchUsers(): Flow<PagingData<RegistrationRoleUser>> {

        var userId = if(userIds.isEmpty()) "" else userIds.last()
        if(isFetchDistributor) {
            userId = appPreference.id.toString()
        }

        return Pager(
            config = PagingConfig(
                pageSize = 30,
            ),
            pagingSourceFactory = {
                RegistrationUserPagingSource(
                    registrationRepository,
                    setUrl(),
                    hashMapOf(
                        "user_id" to userId,
                        "searchType" to inputMode,
                        "searchInput" to inputText,
                        "distType" to if(isFetchDistributor) "FOS_DIS" else ""
                    )
                )
            }
        ).flow
    }

    private fun initialUrl() =
        when (userMapperType) {
            UserType.RM -> AppConstants.BASE_URL + "sales/asm-list"
            UserType.ASM -> AppConstants.BASE_URL + "sales/fos-list"
            UserType.FOS -> AppConstants.BASE_URL + "sales/md-list"
            else -> ""
        }


    private fun setUrl() =
        if (userMapperType == userType) {
            initialUrl()
        } else when (userType) {
            UserType.ASM -> AppConstants.BASE_URL + "sales/fos"
            UserType.FOS -> AppConstants.BASE_URL + "sales/md"
            UserType.MD -> AppConstants.BASE_URL + "sales/dist"
            else -> ""
        }

    fun setUserType(isReverse: Boolean = false) {
        if (isReverse) {
            userType = when (userType) {
                UserType.Distributor -> UserType.MD
                UserType.MD -> {
                    fosId = 1
                    UserType.FOS

                }
                UserType.FOS -> UserType.ASM
                UserType.ASM -> UserType.RM
                else -> null
            }
        } else {
            userType = when (userType) {
                UserType.RM -> UserType.ASM
                UserType.ASM -> UserType.FOS
                UserType.FOS -> UserType.MD
                UserType.MD -> UserType.Distributor
                else -> null
            }
        }
        setTitle()
    }

    private fun setTitle() {
        val title = when (userType) {
            UserType.RM -> "ASM List"
            UserType.ASM -> "FOS List"
            UserType.FOS -> "MD List"
            UserType.MD -> "Distributor List"
            else -> ""
        }
        titleObs.value = title
    }


    private fun setUserMapperType() {
        userMapperType = when (appPreference.rollId) {
            22 -> UserType.RM
            23 -> UserType.ASM
            24 -> UserType.FOS
            else -> null
        }
        userType = userMapperType
    }

    fun setupCreateRole(createRoleId: Int) {
        when (createRoleId) {
            3 -> createUserType = UserType.MD
            4 -> createUserType = UserType.Distributor
            5 -> createUserType = UserType.Retailer
        }
    }

    enum class UserType {
        RM, ASM, FOS, MD, Distributor,Retailer
    }

}
