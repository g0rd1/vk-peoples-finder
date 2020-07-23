package ru.g0rd1.peoplesfinder.repo.user.local

abstract class LocalUsersRepoDecorator(protected val localUsersRepo: LocalUsersRepo) :
    LocalUsersRepo