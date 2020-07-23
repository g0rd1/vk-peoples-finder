package ru.g0rd1.peoplesfinder.util

// @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
// fun synchronizedWithNotify(lock: Object, block: () -> Unit) {
//     synchronizedWithConditionAndNotify(lock, { true }, block)
// }
//
// @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
// fun synchronizedWithConditionAndNotify(lock: Object, condition: () -> Boolean, block: () -> Unit) {
//     synchronized(lock) {
//         while (!condition()) {
//             lock.wait(1000)
//         }
//         block()
//         lock.notifyAll()
//     }
// }