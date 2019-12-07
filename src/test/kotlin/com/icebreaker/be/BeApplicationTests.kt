package com.icebreaker.be

import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@Ignore
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Config::class])
class BeApplicationTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun contextLoads() {
    }

    fun authenticate(email: String, password: String) {
        val token = getToken(email, password)
        setBearer(token)
    }

    private fun setBearer(token: GetTokenResponse) {
        val arrayList = ArrayList<ClientHttpRequestInterceptor>()
        arrayList.add(ClientHttpRequestInterceptor { request, body, execution ->
            request.headers.add("Authorization", "Bearer ${token.accessToken}")
            execution.execute(request, body)
        })
        testRestTemplate.restTemplate.interceptors = arrayList
    }

    fun authenticate() {
        authenticate("email1@email.com", "password")
    }

    fun authenticateSocial(accessToken: String) {
        val token = getToken(accessToken)
        setBearer(token)
    }

    private fun getToken(accessToken: String): GetTokenResponse {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.set("Access-Control-Allow-Headers", "x-requested-with, authorization")

        val map = LinkedMultiValueMap<String, String>()
        map.add("grant_type", "social")
        map.add("access_token", accessToken)
        map.add("network", "facebook")
//        map.add("client_id", "2314645505268736")
//        map.add("client_secret", "7015abedf212a0063a03757725107f71")

        val request = HttpEntity<MultiValueMap<String, String>>(map, headers)
        val restTemplateWithBasic = testRestTemplate.withBasicAuth("spring-security-oauth2-read-write-client", "spring-security-oauth2-read-write-client-password1234")
        val result = restTemplateWithBasic.postForEntity("/oauth/token", request, GetTokenResponse::class.java)
        Assert.assertNotNull(result)
        Assert.assertEquals(result.statusCode, HttpStatus.OK)
        Assert.assertNotNull(result.body)
        return result.body!!
    }


    private fun getToken(email: String, password: String): GetTokenResponse {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.set("Access-Control-Allow-Headers", "x-requested-with, authorization")

        val map = LinkedMultiValueMap<String, String>()
        map.add("grant_type", "password")
        map.add("username", email)
        map.add("password", password)

        val request = HttpEntity<MultiValueMap<String, String>>(map, headers)

        val restTemplateWithBasic = testRestTemplate.withBasicAuth("spring-security-oauth2-read-write-client", "spring-security-oauth2-read-write-client-password1234")
        val result = restTemplateWithBasic.postForEntity("/oauth/token", request, GetTokenResponse::class.java)
        Assert.assertNotNull(result)
        Assert.assertEquals(result.statusCode, HttpStatus.OK)
        Assert.assertNotNull(result.body)
        return result.body!!
    }

    data class GetTokenResponse(
            @JsonProperty("access_token") val accessToken: String,
            @JsonProperty("refresh_token") val refreshToken: String,
            @JsonProperty("token_type") val tokenType: String,
            @JsonProperty("expires_in") val expiresIn: Int,
            @JsonProperty("scope") val scope: String)
}

@TestConfiguration
@ComponentScan("com.icebreaker.be")
class Config {
//    @Bean
//    fun mockedSimpMessageSendingOperations(): SimpMessageSendingOperations {
//        return mock(SimpMessageSendingOperations::class.java)
//    }
}