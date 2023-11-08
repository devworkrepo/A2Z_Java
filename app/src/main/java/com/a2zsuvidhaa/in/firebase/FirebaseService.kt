package com.a2zsuvidhaa.`in`.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import javax.inject.Inject


class FirebaseService @Inject constructor(val firebaseFirestore: FirebaseFirestore) {



  inline fun getInAppUpdateData(crossinline updateInfo : (forceUpdate :Boolean,shouldUpdate : Boolean) ->Unit) {
      firebaseFirestore.collection(COLLECTION_IN_APP_UPDATE)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result?.let { querySnapshot ->

                            try {
                                val snapshot: DocumentSnapshot = querySnapshot.documents[0]
                                val forceUpdate:Boolean = snapshot.getBoolean("force_update")!!
                                val shouldUpdate:Boolean = snapshot.getBoolean("should_update")!!
                                    updateInfo(forceUpdate,shouldUpdate)
                            }catch (e : Exception){
                                updateInfo(true,true)
                            }

                        } ?: run{
                            updateInfo(true,true)
                        }
                    }
                    else updateInfo(true,true)
                }
    }


    inline fun getNormalUpdateData(crossinline updateInfo : (force :Boolean,enable : Boolean,delay : Long) ->Unit) {
        firebaseFirestore.collection(COLLECTION_NORMAL_UPDATE)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result?.let { querySnapshot ->
                            val snapshot: DocumentSnapshot = querySnapshot.documents[0]
                            try {
                                val enable:Boolean = snapshot.getBoolean("enable")!!
                                val force:Boolean = snapshot.getBoolean("force")!!
                                val delay:Long = snapshot.getLong("delay")!!
                                updateInfo(force,enable,delay)
                            }catch (e : Exception){
                                updateInfo(true,true,14400000L )
                            }

                        } ?: run{
                            updateInfo(true,true,14400000L)
                        }
                    }
                    else updateInfo(true,true,14400000L)
                }
    }


    companion object {
        const val COLLECTION_IN_APP_UPDATE = "in_app_update"
        const val COLLECTION_NORMAL_UPDATE = "normal_update"

    }

}