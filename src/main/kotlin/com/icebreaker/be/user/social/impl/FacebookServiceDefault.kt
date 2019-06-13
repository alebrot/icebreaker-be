package com.icebreaker.be.user.social.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.icebreaker.be.user.social.SocialService
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service("facebookSocialService")
class FacebookServiceDefault : SocialService {
    override fun getUser(token: String): SocialUser {
        val restTemplate = RestTemplate()
        val url = "https://graph.facebook.com/v3.3/me?fields=id,first_name,last_name,email,picture.width(250).height(250)&access_token=$token"
        val response = restTemplate.getForEntity(url, String::class.java)
        val mapper = ObjectMapper()
        val root = mapper.readTree(response.body)
        val id = root.path("id").textValue() ?: throw IllegalArgumentException("no id")
//        val name = root.path("name")?.textValue()
        val firstName = root.path("first_name")?.textValue() ?: throw IllegalArgumentException("no first_name")
        val lastName = root.path("last_name")?.textValue() ?: throw IllegalArgumentException("no last_name")
        val email = root.path("email")?.textValue() ?: throw IllegalArgumentException("no email")
        val imageUrl = root.get("picture")?.get("data")?.path("url")?.textValue()
        return SocialUser(id, email, firstName, lastName, imageUrl)
    }
}

data class SocialUser(val id: String, val email: String, val firstName: String, val lastName: String, val imgUrl: String?)