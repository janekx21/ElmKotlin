package com.me.backend

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.language.StringValue
import graphql.schema.*
import org.springframework.beans.factory.BeanFactoryAware
import reactor.core.publisher.Mono
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf

/**
 * Schema generator hook that adds additional scalar types.
 */
class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {
    // Register additional GraphQL scalar types.
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        UUID::class -> graphqlUUIDType
        Unit::class -> unitType
        else -> null
    }

    // Register Reactor Mono monad type.
    override fun willResolveMonad(type: KType): KType = when (type.classifier) {
        Mono::class -> type.arguments.first().type ?: type
        Set::class -> List::class.createType(type.arguments)
        else -> type
    }

    // Exclude the Spring bean factory interface
    override fun isValidSuperclass(kClass: KClass<*>): Boolean {
        return when {
            kClass.isSubclassOf(BeanFactoryAware::class) -> false
            else -> super.isValidSuperclass(kClass)
        }
    }
}

internal val graphqlUUIDType = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("A type representing a formatted java.util.UUID")
    .coercing(UUIDCoercing)
    .build()

internal val unitType = GraphQLScalarType.newScalar()
    .name("Unit")
    .description("The type with only one value: the Unit object. This type corresponds to the void type in Java.")
    .coercing(UnitCoercing)
    .build()

private object UnitCoercing : Coercing<Unit, String> {
    override fun parseLiteral(input: Any?) {}
    override fun parseValue(input: Any?) {}
    override fun serialize(dataFetcherResult: Any?): String = ""
}

private object UUIDCoercing : Coercing<UUID, String> {
    override fun parseValue(input: Any): UUID = runCatching {
        UUID.fromString(serialize(input))
    }.getOrElse {
        throw CoercingParseValueException("Expected valid UUID but was $input")
    }

    override fun parseLiteral(input: Any): UUID {
        val uuidString = (input as? StringValue)?.value
        return runCatching {
            UUID.fromString(uuidString)
        }.getOrElse {
            throw CoercingParseLiteralException("Expected valid UUID literal but was $uuidString")
        }
    }

    override fun serialize(dataFetcherResult: Any): String = runCatching {
        dataFetcherResult.toString()
    }.getOrElse {
        throw CoercingSerializeException("Data fetcher result $dataFetcherResult cannot be serialized to a String")
    }
}
