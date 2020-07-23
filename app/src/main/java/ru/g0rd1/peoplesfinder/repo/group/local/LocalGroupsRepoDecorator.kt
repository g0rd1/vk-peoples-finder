package ru.g0rd1.peoplesfinder.repo.group.local

abstract class LocalGroupsRepoDecorator(protected val localGroupsRepo: LocalGroupsRepo) :
    LocalGroupsRepo