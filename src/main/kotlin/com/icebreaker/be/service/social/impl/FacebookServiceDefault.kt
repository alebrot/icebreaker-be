package com.icebreaker.be.service.social.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.icebreaker.be.service.auth.social.SocialType
import com.icebreaker.be.service.model.Gender
import com.icebreaker.be.service.social.SocialService
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Service("facebookSocialService")
class FacebookServiceDefault : SocialService {
    override fun getUser(token: String): SocialUser {
        val restTemplate = RestTemplate()
        val url = "https://graph.facebook.com/v3.3/me?fields=id,first_name,birthday,gender,last_name,email,picture.width(250).height(250)&access_token=$token"
        val response = restTemplate.getForEntity(URI(url), String::class.java)
        val mapper = ObjectMapper()
        val root = mapper.readTree(response.body)
        val id = root.path("id").textValue() ?: throw IllegalArgumentException("no id")
//        val name = root.path("name")?.textValue()
        val firstName = root.path("first_name")?.textValue() ?: throw IllegalArgumentException("no first_name")
        val lastName = root.path("last_name")?.textValue() ?: throw IllegalArgumentException("no last_name")
        val email = root.path("email")?.textValue() ?: throw IllegalArgumentException("no email")
        val imageUrl = root.get("picture")?.get("data")?.path("url")?.textValue()

        val birthdayString = root.path("birthday")?.textValue()
                ?: throw IllegalArgumentException("no birthday")//"05/17/1989"
        val birthDay = LocalDate.parse(birthdayString, DateTimeFormatter.ofPattern("MM/dd/yyyy"))


        val gender: Gender? = when (root.path("gender")?.textValue()) {
            "male" -> {
                Gender.MALE
            }
            "female" -> {
                Gender.FEMALE
            }
            else -> {
                null
            }
        }



        return SocialUser(SocialType.FACEBOOK, id, email, firstName, lastName, imageUrl, birthDay, gender)
    }
}

data class SocialUser(val socialType: SocialType, val id: String, val email: String, val firstName: String, val lastName: String, val imgUrl: String?, val birthDay: LocalDate, val gender: Gender?)