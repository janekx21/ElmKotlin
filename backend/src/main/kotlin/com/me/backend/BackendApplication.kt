package com.me.backend

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.operations.Subscription
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.Duration
import kotlin.random.Random

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}

@SpringBootApplication
class BackendApplication {
    @Bean
    fun hooks() = CustomSchemaGeneratorHooks()

    private val logger: Logger = LoggerFactory.getLogger(BackendApplication::class.java)

    init {
        logger.info("Hosting playground at http://localhost:8080/playground")
    }
}

@Component
@Suppress("unused")
class AppQuery(val repo: AppRepository) : Query {
    @GraphQLDescription("Returns example string")
    fun exampleQuery(): String = "Example"
}

@Component
@Suppress("unused")
class AppMutation(val repo: AppRepository) : Mutation {
    @GraphQLDescription("Example mutation that returns its input")
    fun exampleMutation(foo: String): String = foo
}

@Component
@Suppress("unused")
class AppSubscription(val repo: AppRepository) : Subscription {
    @GraphQLDescription("Returns a random number every second")
    fun counter(limit: Int? = null): Flux<Int> = Flux.interval(Duration.ofSeconds(1)).map {
        Random.nextInt()
    }
}
