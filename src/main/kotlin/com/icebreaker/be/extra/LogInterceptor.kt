package com.icebreaker.be.extra

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class LoggingRequestInterceptor : ClientHttpRequestInterceptor {

    @Throws(IOException::class)
    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        traceRequest(request, body)
        val response = execution.execute(request, body)
        traceResponse(response)
        return response
    }

    @Throws(IOException::class)
    private fun traceRequest(request: HttpRequest, body: ByteArray) {
        log.info("===========================request begin================================================")
        log.info("URI         : {}", request.uri)
        log.info("Method      : {}", request.method)
        log.info("Headers     : {}", request.headers)
        log.info("Request body: {}", String(body, charset("UTF-8")))
        log.info("==========================request end================================================")
    }

    @Throws(IOException::class)
    private fun traceResponse(response: ClientHttpResponse) {
        val inputStringBuilder = StringBuilder()
        val bufferedReader = BufferedReader(InputStreamReader(response.body, charset("UTF-8")))
        var line: String? = bufferedReader.readLine()
        while (line != null) {
            inputStringBuilder.append(line)
            inputStringBuilder.append('\n')
            line = bufferedReader.readLine()
        }
        log.info("============================response begin==========================================")
        log.info("Status code  : {}", response.statusCode)
        log.info("Status text  : {}", response.statusText)
        log.info("Headers      : {}", response.headers)
        log.info("Response body: {}", inputStringBuilder.toString())
        log.info("=======================response end=================================================")
    }

    companion object {
        internal val log: Logger = LoggerFactory.getLogger(LoggingRequestInterceptor::class.java)
    }

}