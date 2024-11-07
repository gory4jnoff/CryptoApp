package com.goryajnoff.cryptoapp.api

import com.goryajnoff.cryptoapp.pojo.CoinInfoListOfData
import com.goryajnoff.cryptoapp.pojo.CoinPriceInfoRawData
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top/totalvolfull")
    fun getTopCoinsInfo(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = "ca08d8fcee6d170c24bf9d7de84fd9ac48f42bc525e6b7dfec666cade8c146cf",
        @Query(QUERY_PARAM_LIMIT) limit: Int = 10,
        @Query(QUERY_PARAM_TO_SYMBOL) tsym: String = CONCURRENCY
    ): Single<CoinInfoListOfData>
    @GET("pricemultifull")
    fun getFullPriceList(
        @Query(QUERY_PARAM_API_KEY) apiKey:String = "ca08d8fcee6d170c24bf9d7de84fd9ac48f42bc525e6b7dfec666cade8c146cf",
        @Query(QUERY_PARAM_FROM_SYMBOLS) fsyms:String,
        @Query(QUERY_PARAM_TO_SYMBOLS) tsyms:String = CONCURRENCY
    ):Single<CoinPriceInfoRawData>

    companion object {
        private const val QUERY_PARAM_API_KEY = "api_key"
        private const val QUERY_PARAM_LIMIT = "limit"
        private const val QUERY_PARAM_TO_SYMBOL = "tsym"
        private const val QUERY_PARAM_FROM_SYMBOLS = "fsyms"
        private const val QUERY_PARAM_TO_SYMBOLS = "tsyms"
        private const val CONCURRENCY = "USD"
    }
}