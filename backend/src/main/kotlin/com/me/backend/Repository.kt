package com.me.backend

import com.me.backend.model.EntityNotFoundException
import java.util.*

interface Entity {
    val id: UUID
}

class Repository<T : Entity>(private val name: String) {
    private val data = mutableMapOf<UUID, T>()
    val values get() = data.values.toList()
    fun add(value: T): T = value.also { data[it.id] = it }
    fun addAll(values: List<T>) = values.forEach { add(it) }
    fun replace(value: T): T =
        data.replace(value.id, value) ?: throw EntityNotFoundException(value.id, name)

    fun remove(id: UUID): T = data.remove(id) ?: throw EntityNotFoundException(id, name)
    fun removeAll(predicate: (T) -> Boolean) = data.values.filter(predicate).forEach { remove(it.id) }
    fun find(id: UUID): T = data[id] ?: throw EntityNotFoundException(id, name)
}

inline fun <reified T : Entity> createRepository() = Repository<T>(T::class.simpleName!!)

fun createID(): UUID = UUID.randomUUID()
val zeroID = UUID(0, 0)
