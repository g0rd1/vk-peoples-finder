package ru.g0rd1.peoplesfinder.common

import javax.inject.Inject

class PriorityQueueManagerFactory @Inject constructor() {

    fun create(queueCapacity: Int): PriorityQueueManager = PriorityQueueManager(queueCapacity)

}