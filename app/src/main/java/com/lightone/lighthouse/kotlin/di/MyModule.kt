package com.lightone.lighthouse.kotlin.di


import androidx.room.Room
import com.lightone.lighthouse.kotlin.Database.UserScrapDatabase
import com.lightone.lighthouse.kotlin.Database.UserSearchDatabase
import com.lightone.lighthouse.kotlin.config.MyApplication.Companion.client
import com.lightone.lighthouse.kotlin.config.MyConstant.Companion.BASE_URL
import com.lightone.lighthouse.kotlin.src.detail.model.GetChartDataModel
import com.lightone.lighthouse.kotlin.src.detail.service.GetDetailChartAPI
import com.lightone.lighthouse.kotlin.src.detail.service.GetDetailChartDataImpl
import com.lightone.lighthouse.kotlin.src.home.adapter.DaysAdapter
import com.lightone.lighthouse.kotlin.src.home.adapter.SectorAdapter
import com.lightone.lighthouse.kotlin.src.home.model.GetReportsDataModel
import com.lightone.lighthouse.kotlin.src.home.service.ReportAPI
import com.lightone.lighthouse.kotlin.src.home.service.ReportDataImpl
import com.lightone.lighthouse.kotlin.src.scrap.adapter.ScrapeAdapter
import com.lightone.lighthouse.kotlin.src.search.adapter.RecentsAdapter
import com.lightone.lighthouse.kotlin.src.search.adapter.TagAdapter
import com.lightone.lighthouse.kotlin.src.search.model.SearchDataModel
import com.lightone.lighthouse.kotlin.src.search.service.SearchAPI
import com.lightone.lighthouse.kotlin.src.search.service.SearchDataImpl
import com.lightone.lighthouse.kotlin.src.suggest.adapter.SuggestAdapter
import com.lightone.lighthouse.kotlin.src.suggest.model.SuggestDataModel
import com.lightone.lighthouse.kotlin.src.suggest.service.SuggestAPI
import com.lightone.lighthouse.kotlin.src.suggest.service.SuggestDataImpl
import com.lightone.lighthouse.kotlin.src.suggest_detail.adapter.SuggestSectorAdapter
import com.lightone.lighthouse.kotlin.src.suggest_detail.model.GetSuggestDetailDataModel
import com.lightone.lighthouse.kotlin.src.suggest_detail.service.GetSuggestDetailAPI
import com.lightone.lighthouse.kotlin.src.suggest_detail.service.GetSuggestDetailDataImpl
import com.lightone.lighthouse.kotlin.viewmodel.*
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * MyModule.kt
 */

var retrofitPart = module {
    single<SearchAPI> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchAPI::class.java)
    }
    single<GetDetailChartAPI> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetDetailChartAPI::class.java)
    }
    single<ReportAPI> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReportAPI::class.java)
    }
    single<SuggestAPI> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SuggestAPI::class.java)
    }
    single<GetSuggestDetailAPI> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetSuggestDetailAPI::class.java)
    }
}

var roomPart = module {
    single {
        Room.databaseBuilder(
            get(),
            UserScrapDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
    single { get<UserScrapDatabase>().userscrapDao() }

    single {
        Room.databaseBuilder(
            get(),
            UserSearchDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
    single { get<UserSearchDatabase>().usersearchDao() }

}

var adapterPart = module {
    factory {
        DaysAdapter()
    }
    factory {
        SectorAdapter()
    }
    factory {
        RecentsAdapter()
    }
    factory {
        SuggestAdapter()
    }
    factory {
        SuggestSectorAdapter()
    }
    factory {
        ScrapeAdapter()
    }
    factory {
        TagAdapter()
    }
}

var modelPart = module {
    factory<SearchDataModel> {
        SearchDataImpl(get())
    }
    factory<GetChartDataModel> {
        GetDetailChartDataImpl(get())
    }
    factory<GetReportsDataModel> {
        ReportDataImpl(get())
    }
    factory<SuggestDataModel> {
        SuggestDataImpl(get())
    }
    factory<GetSuggestDetailDataModel> {
        GetSuggestDetailDataImpl(get())
    }
}

var viewModelPart = module {
    viewModel { MainViewModel() }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { SuggestViewModel(get() ) }
    viewModel { SuggestDetailViewModel(get(), get()) }
    viewModel { ScraplViewModel(get()) }
}

var myDiModule = listOf(retrofitPart, roomPart, adapterPart, modelPart, viewModelPart)