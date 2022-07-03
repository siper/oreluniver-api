package service

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import javax.sql.DataSource

object DatabaseFactory {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val dbUrl = appConfig.property("db.jdbcUrl").getString()
    private val dbUser = appConfig.property("db.dbUser").getString()
    private val dbPassword = appConfig.property("db.dbPassword").getString()

    fun create(): DataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = dbUrl
            maximumPoolSize = 3
            username = dbUser
            password = dbPassword
            validate()
        }
        return HikariDataSource(config)
    }

    suspend inline fun <T> dbQuery(crossinline block: suspend () -> T): T = newSuspendedTransaction { block() }
}