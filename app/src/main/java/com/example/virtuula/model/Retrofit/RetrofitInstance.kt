package com.example.virtuula.model.Retrofit

import com.example.virtuula.response.NullOnEmptyConverterFactory
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class RetrofitInstance {
    companion object{
        //val BASE_URL="http://192.168.1.37:8080"
        val BASE_URL="http://ec2-54-84-248-159.compute-1.amazonaws.com:8080"
        //val BASE_URL="http://192.168.1.145:8080"
        val BASE_URL2 = "https://jsonplaceholder.typicode.com"
        val gson = GsonBuilder().setLenient().create()
        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(NullOnEmptyConverterFactory()).addConverterFactory(GsonConverterFactory.create(gson)).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        }
        fun getRetrofitInstance2(): Retrofit {
            return Retrofit.Builder().baseUrl(BASE_URL2).addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())).build()
        }


    }
}