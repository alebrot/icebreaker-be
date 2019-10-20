package com.icebreaker.be.controller.user.impl

import com.icebreaker.be.BeApplicationTests
import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.controller.user.*
import com.icebreaker.be.controller.user.dto.*
import com.icebreaker.be.db.repository.UserPositionRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.service.chat.impl.ChatServiceDefault
import com.icebreaker.be.service.credit.CreditServiceDefault
import com.icebreaker.be.service.user.impl.UserServiceDefault
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDate

internal class UserControllerDefaultTest : BeApplicationTests() {
    @Ignore
    @Test
    fun getUserById() {
//        authenticate()
        authenticateSocial("EAAg5KGcfXAABAFRa6DaDbchx5VkIq7bDhnJwTZAdHSpjIrGZCzG11leyLMQKPAbt5mj3r2hni0LLGYUa3OaBZC6BxsvTlZCm4sPwM108EUQQz393YksQ64Xn4ZCFINIZCl730d6Hviz9icgsBY6aMq54gPDZCL1HRIXjoBZC5aolo7e2fNmPgq3kbIZBt91YXkbJhUip6DWoRE491XvKjYrcemcgA73RDCjMQpgNjCJUZAigZDZD")
        Thread.sleep(100)
        authenticateSocial("EAAg5KGcfXAABAFRa6DaDbchx5VkIq7bDhnJwTZAdHSpjIrGZCzG11leyLMQKPAbt5mj3r2hni0LLGYUa3OaBZC6BxsvTlZCm4sPwM108EUQQz393YksQ64Xn4ZCFINIZCl730d6Hviz9icgsBY6aMq54gPDZCL1HRIXjoBZC5aolo7e2fNmPgq3kbIZBt91YXkbJhUip6DWoRE491XvKjYrcemcgA73RDCjMQpgNjCJUZAigZDZD")

        val userId = 1

        val response = testRestTemplate.getForEntity(GET_USER_BY_ID.replace("{userId}", userId.toString()), GetUserByIdResponse::class.java)
        Assert.assertNotNull(response)
        Assert.assertEquals(response.statusCode, HttpStatus.OK)
        Assert.assertNotNull(response.body)
        Assert.assertNotNull(response.body!!.user)
    }

    @Ignore
    @Test
    fun getUserMe() {
        authenticate("email2@email.com", "password")
        val response = testRestTemplate.getForEntity(GET_USER_ME, GetUserMeResponse::class.java)
        Assert.assertNotNull(response)
        Assert.assertEquals(response.statusCode, HttpStatus.OK)
        Assert.assertNotNull(response.body)
        Assert.assertNotNull(response.body!!.context)
    }

    @Ignore
    @Test
    fun getAdminMe() {
        authenticate()
        val response = testRestTemplate.getForEntity(GET_ADMIN_ME, GetAdminMeResponse::class.java)
        Assert.assertNotNull(response)
        Assert.assertEquals(response.statusCode, HttpStatus.OK)
        Assert.assertNotNull(response.body)
        Assert.assertNotNull(response.body!!.context)
    }

    @Ignore
    @Test
    fun createUser() {

        val email = "test1@email.com"
        val password = "password"
        val createUserRequest = CreateUserRequest(email, "new firstname", "new lastname", password, LocalDate.now().minusYears(20))

        val responseEntity = testRestTemplate.postForEntity(CREATE_USER, createUserRequest, CreateUserResponse::class.java)
        Assert.assertEquals(responseEntity.statusCode, HttpStatus.OK)
        Assert.assertNotNull(responseEntity)
        Assert.assertNotNull(responseEntity.body)
        Assert.assertNotNull(responseEntity.body!!.user.id)


        val deleteUserRequest = DeleteUserRequest("reason")
        authenticate(email, password)

        val exchange = testRestTemplate.exchange(DELETE_USER_ME, HttpMethod.DELETE, HttpEntity(deleteUserRequest), BaseResponse::class.java)
        assert(exchange.statusCode==HttpStatus.OK)

    }

    @Ignore
    @Test
    fun getUserMeUsers() {
        authenticate()
        val response = testRestTemplate.getForEntity("$GET_USER_ME_USERS?distance={distance}", GetUserMeUsersResponse::class.java, 100)
        Assert.assertNotNull(response)
        Assert.assertEquals(response.statusCode, HttpStatus.OK)
        Assert.assertNotNull(response.body)
        Assert.assertNotNull(response.body!!.users)
    }

    @Ignore
    @Test
    fun createUserPosition() {
        authenticate("email2@email.com", "password")
        val responseEntity = testRestTemplate.postForEntity(CREATE_USER_POSITION, CreateUserPositionRequest(BigDecimal(45.4748338), BigDecimal(9.1746082)), String::class.java)
        Assert.assertEquals(responseEntity.statusCode, HttpStatus.OK)
        Assert.assertNotNull(responseEntity)
        testRestTemplate.postForEntity(CREATE_USER_POSITION, CreateUserPositionRequest(BigDecimal(46.4748338), BigDecimal(10.1746082)), String::class.java)
    }


    @Autowired
    lateinit var userPositionRepository: UserPositionRepository

    @Autowired
    lateinit var userRepository: UserRepository


    @Autowired
    lateinit var chatServiceDefault: ChatServiceDefault

    //    @Test
//    fun fdfdyf() {
//        ssdsfsd()
////        findByUserId.u
//    }
//
//    @Transactional
//    fun ssdsfsd() {
////        val findUsersCloseToUser = userRepository.findUsersCloseToUser(1, 1000)
//
//
////        val findByUserId = userPositionRepository.findByUserId(1)
////        val user = findByUserId?.user
////        val authorities = user?.authorities
//        val findById = userRepository.findById(1).toKotlinNotOptionalOrFail()
//        val model = User.fromEntity(findById)
//
//        val chatsByUser = chatServiceImpl.getChatsByUser(model)
//    }
    @Autowired
    lateinit var creditServiceDefault: CreditServiceDefault

    @Autowired
    lateinit var userServiceDefault: UserServiceDefault

    @Ignore
    @Test
    fun purchaseAndroid() {
        val user = userServiceDefault.getUserById(1)
        val purchaseAndroid = creditServiceDefault.purchaseAndroid(user, "android.test.purchased", "xzczxczxc")
    }

    @Ignore
    @Test
    fun purchaseIos() {
        val receipt = "{\\n\\t\"signature\" = \"[exactly_1320_characters]\";\\n\\t\"purchase-info\" =\n" +
                "\"[exactly_868_characters]\";\\n\\t\"environment\" = \"Sandbox\";\\n\\t\"pod\" =\n" +
                "\"100\";\\n\\t\"signing-status\" = \"0\";\\n}"
        val user = userServiceDefault.getUserById(1)
        val purchaseIos = creditServiceDefault.purchaseIos(user, receipt)
    }

}