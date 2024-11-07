package com.goryajnoff.cryptoapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.goryajnoff.cryptoapp.api.ApiFactory
import com.goryajnoff.cryptoapp.database.AppDatabase
import com.goryajnoff.cryptoapp.pojo.CoinPriceInfo
import com.goryajnoff.cryptoapp.pojo.CoinPriceInfoRawData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class CoinViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    val priceList = db.coinPriceInfoDao().getPriceList()
    private val compositeDisposable = CompositeDisposable()
    init {
        loadData()
    }

    fun getDetailInfo(fsym:String): LiveData<CoinPriceInfo> {
        return db.coinPriceInfoDao().getPriceInfoAboutCoin(fsym)
    }

    private fun loadData(){
        val disposable = ApiFactory.apiService.getTopCoinsInfo(limit = 50)
            .map { it.data?.map { it.coinInfo?.name}?.joinToString(",").toString() }
            .flatMap { ApiFactory.apiService.getFullPriceList(fsyms = it)}
            .map { getCoinInfoFromRaw(it) }
            .delaySubscription(10, TimeUnit.SECONDS)
            .repeat()
            .retry()
            .subscribeOn(Schedulers.io())
            .subscribe({
                db.coinPriceInfoDao().insertPriceList(it)
                Log.d("TESTLOAD",it.toString())
            },{
                Log.d("TESTLOAD",it.message.toString())
            })
        compositeDisposable.add(disposable)
    }
    private fun getCoinInfoFromRaw(rawData:CoinPriceInfoRawData):List<CoinPriceInfo>{
        val result = ArrayList<CoinPriceInfo>()
        val jsonObject = rawData.coinPriceInfoJsonObject ?: return result
        val coinKeySet = jsonObject.keySet()
        for(coinKey in coinKeySet){
            val currency = jsonObject.getAsJsonObject(coinKey)
            val keySet = currency.keySet()
            for(key in keySet){
                val priceInfo = Gson().fromJson(currency.getAsJsonObject(key),CoinPriceInfo::class.java)
                result.add(priceInfo)
            }
        }
        return result
    }



    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}