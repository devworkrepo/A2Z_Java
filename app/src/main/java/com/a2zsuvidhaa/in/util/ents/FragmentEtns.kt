package com.a2zsuvidhaa.`in`.util.ents

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.a2zsuvidhaa.`in`.activity.MainActivity
import com.a2zsuvidhaa.`in`.util.apis.SingleMutableLiveData
import com.a2zsuvidhaa.`in`.util.ui.Resource


fun Fragment.removeCurrent(){
    activity?.supportFragmentManager?.beginTransaction()
            ?.remove(this)?.commit()

}


inline  fun <T : Resource<T>> Fragment.fragObserver(mLiveData: LiveData<T>, crossinline observer : (T)->Unit){
    mLiveData.observe(viewLifecycleOwner,{
        observer(it)
    })
}

inline fun Fragment.backPressHandler(crossinline handler : (OnBackPressedCallback)->Unit){
    requireActivity().onBackPressedDispatcher.addCallback(owner = viewLifecycleOwner) {
        handler(this)
    }
}

fun Fragment.gotoMainActivity(){

    activity?.let {
        val intent =  Intent(it, MainActivity::class.java)
        it.startActivity(intent)
        activity?.finish()
    }


}

/*fun <T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)*/

fun <T> Fragment.getNavigationResultAsLiveData(key: String = "result"): SingleMutableLiveData<T> {
    val data = findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)
    return if(data == null) SingleMutableLiveData() else SingleMutableLiveData(data)
}

fun <T> Fragment.getNavigationResult(key: String = "result",onResult : (T?)->Unit) {
    val data =  findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)
    onResult(data)

}





fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}
