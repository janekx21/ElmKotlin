package com.me.backend.model

import java.util.*

/**
 * @param name string containing the name of the entity
 */
class EntityNotFoundException(id: UUID, name: String) : Exception("The $name with id $id could not be found")

class OptionalInputUndefinedException : Exception("The optional input should be defined but was undefined")
