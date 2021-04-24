package ru.g0rd1.peoplesfinder.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.g0rd1.peoplesfinder.repo.access.SharedPrefVKAccessRepo
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import ru.g0rd1.peoplesfinder.repo.filters.FiltersRepo
import ru.g0rd1.peoplesfinder.repo.filters.SharedPrefFiltersRepo
import ru.g0rd1.peoplesfinder.repo.group.local.DBLocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.HttpVkGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.DBLocalUsersRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.repo.vk.HttpVkRepo
import ru.g0rd1.peoplesfinder.repo.vk.VkRepo
import ru.g0rd1.peoplesfinder.repo.vk.city.CityRepo
import ru.g0rd1.peoplesfinder.repo.vk.city.HttpCityRepo
import ru.g0rd1.peoplesfinder.repo.vk.country.CountryRepo
import ru.g0rd1.peoplesfinder.repo.vk.country.HttpCountryRepo
import ru.g0rd1.peoplesfinder.repo.vk.photo.HttpPhotoRepo
import ru.g0rd1.peoplesfinder.repo.vk.photo.PhotoRepo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReposModule {

    @Binds
    @Singleton
    abstract fun vkRepo(vkRepo: HttpVkRepo): VkRepo

    @Binds
    @Singleton
    abstract fun groupsVkRepo(groupsVkRepo: HttpVkGroupsRepo): VkGroupsRepo

    @Binds
    @Singleton
    abstract fun localGroupsRepo(localGroupsRepo: DBLocalGroupsRepo): LocalGroupsRepo

    @Binds
    @Singleton
    abstract fun localUsersRepo(localUsersRepo: DBLocalUsersRepo): LocalUsersRepo

    @Binds
    @Singleton
    abstract fun cityRepo(cityRepo: HttpCityRepo): CityRepo

    @Binds
    @Singleton
    abstract fun countryRepo(countryRepo: HttpCountryRepo): CountryRepo

    @Binds
    @Singleton
    abstract fun photoRepo(photoRepo: HttpPhotoRepo): PhotoRepo

    companion object {

        @Provides
        @Singleton
        fun filtersRepo(@ApplicationContext context: Context): FiltersRepo = SharedPrefFiltersRepo(context)

        @Provides
        @Singleton
        fun accessRepo(@ApplicationContext context: Context): VKAccessRepo = SharedPrefVKAccessRepo(context)

    }

}