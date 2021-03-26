package sk.bsmk.experiments

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class ProgrammingExcusesFetcher(private val client: HttpClient) {

    suspend fun fetchExcuse(): String {
        val response: HttpResponse = client.get("http://programmingexcuses.com/")
        return response.readText()
    }

}