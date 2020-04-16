package com.android.dogs.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.dogs.model.DogBreed
import com.android.dogs.model.DogDatabase
import com.android.dogs.model.DogsApiService
import com.android.dogs.util.NotificationHelper
import com.android.dogs.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class DogListViewModel(application: Application) : BaseViewModel(application) {
    private val dogsService = DogsApiService()
    private val disposable = CompositeDisposable()

    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1000 * 1000 *1000L

    val dogs = MutableLiveData<List<DogBreed>>()
    val isLoading = MutableLiveData<Boolean>()
    val isError = MutableLiveData<Boolean>()

    fun refresh(){
        checkCacheDuration()
        val updateTime = prefHelper.getUpdateTime()
        if(updateTime!= null && updateTime!=0L && System.nanoTime()-updateTime<refreshTime){
            fetchFromDataBase()
        }else {
            fetchFromRemote()
        }
    }

    private fun checkCacheDuration() {
        val cachePreference = prefHelper.getCacheDuration()
        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5*60
            refreshTime = cachePreferenceInt.times(1000*1000*1000L)
        }catch (e : NumberFormatException){
            e.printStackTrace()
        }
    }

    private fun fetchFromDataBase() {
        isLoading.value = true
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogRetrieved(dogs)
            Toast.makeText(getApplication(),"Fetched from DB",Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFromRemote(){
        isLoading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :  DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogLocally(dogList)
                        Toast.makeText(getApplication(),"Fetched from API",Toast.LENGTH_SHORT).show()
                        NotificationHelper(getApplication()).createNotification()
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        isError.value = true
                        e.printStackTrace()
                    }

                })
        )
    }
    private fun dogRetrieved(dogList: List<DogBreed>){
        dogs.value = dogList
        isLoading.value = false
        isError.value = false
    }
    private fun storeDogLocally(dogList: List<DogBreed>){
        //Because this class inherit CoroutineScope so we can access launch JOB
        //this will run any work inside its scope on separate thread
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            //for clearing previous info available
            dao.deleteAllDogs()
            val result = dao.insertAll(*dogList.toTypedArray())
            var i=0
            while (i<dogList.size){
                dogList[i].uuid = result[i].toInt()
                ++i
            }
            dogRetrieved(dogList)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun refreshByPassCache() {
        fetchFromRemote()
    }
}