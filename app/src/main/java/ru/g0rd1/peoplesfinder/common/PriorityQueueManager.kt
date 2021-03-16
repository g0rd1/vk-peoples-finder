package ru.g0rd1.peoplesfinder.common

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.PriorityBlockingQueue

class PriorityQueueManager(capacity: Int) {

    private val priorityQueue = PriorityBlockingQueue<PriorityId>()

    private val jobQueue: BlockingQueue<PriorityId> = ArrayBlockingQueue(capacity)

    private val priorityIdToWaitQueue: MutableMap<PriorityId, BlockingQueue<Unit>> = hashMapOf()

    private val disposables = CompositeDisposable()

    private val jobDistribution: Completable = Completable.fromAction {
        val stub = PriorityId("", 0)
        jobQueue.put(stub)
        jobQueue.remove(stub)
        val priorityId = priorityQueue.take()
        priorityIdToWaitQueue[priorityId]?.put(Unit)
    }.repeat()


    init {
        jobDistribution
            .subscribeOnIo()
            .observeOnUI()
            .subscribe {}
            .addTo(disposables)
    }

    fun <T> getQueuedSingle(single: Single<T>, priority: Int): Single<T> {
        return Single.fromCallable { PriorityId(UUID.randomUUID().toString(), priority) }
            .flatMap { priorityId ->
                Completable.fromAction {
                    try {
                        priorityIdToWaitQueue[priorityId] = ArrayBlockingQueue(1)
                        priorityQueue.put(priorityId)
                        priorityIdToWaitQueue[priorityId]?.take()
                        priorityIdToWaitQueue.remove(priorityId)
                        jobQueue.put(priorityId)
                    } catch (t: Throwable) {
                        jobQueue.remove(priorityId)
                    }
                }
                    .andThen(single)
                    .doFinally {
                        jobQueue.remove(priorityId)
                    }
            }
    }

    fun getQueuedCompletable(completable: Completable, priority: Int): Completable {
        return Single.just(PriorityId(UUID.randomUUID().toString(), priority))
            .flatMapCompletable { priorityId ->
                Completable.fromAction {
                    try {
                        priorityIdToWaitQueue[priorityId] = ArrayBlockingQueue(1)
                        priorityQueue.put(priorityId)
                        priorityIdToWaitQueue[priorityId]!!.take()
                        priorityIdToWaitQueue.remove(priorityId)
                        jobQueue.put(priorityId)
                    } catch (t: Throwable) {
                        jobQueue.remove(priorityId)
                    }
                }.onErrorComplete()
                    .andThen(completable)
                    .doFinally {
                        jobQueue.remove(priorityId)
                    }
            }
    }

    fun clear() {
        disposables.clear()
    }

    private data class PriorityId(val id: String, val priority: Int) : Comparable<PriorityId> {
        override fun compareTo(other: PriorityId): Int {
            return this.priority - other.priority
        }

    }

}
