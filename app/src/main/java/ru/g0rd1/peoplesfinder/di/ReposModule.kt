package ru.g0rd1.peoplesfinder.di

import dagger.Binds
import dagger.Module
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
import javax.inject.Singleton

@Module
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
    abstract fun accessRepo(accessRepo: SharedPrefVKAccessRepo): VKAccessRepo

    @Binds
    @Singleton
    abstract fun filtersRepo(filtersRepo: SharedPrefFiltersRepo): FiltersRepo

    @Binds
    @Singleton
    abstract fun cityRepo(cityRepo: HttpCityRepo): CityRepo

    @Binds
    @Singleton
    abstract fun countryRepo(countryRepo: HttpCountryRepo): CountryRepo

}