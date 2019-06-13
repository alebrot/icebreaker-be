package com.icebreaker.be.user.impl

import com.icebreaker.be.auth.UserDetailsDefault
import com.icebreaker.be.db.entity.AkSocialEntity
import com.icebreaker.be.db.entity.AkUserEntity
import com.icebreaker.be.db.repository.AuthorityRepository
import com.icebreaker.be.db.repository.SocialRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.fromEntity
import com.icebreaker.be.user.UserService
import com.icebreaker.be.user.social.impl.SocialUser
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserServiceDefault(val userRepository: UserRepository,
                         val passwordEncoder: PasswordEncoder,
                         val authorityRepository: AuthorityRepository,
                         val socialRepository: SocialRepository) : UserService {

    @Transactional
    override fun createUser(email: String, password: String, firstName: String, lastName: String): User {
        val passwordHash = passwordEncoder.encode(password)
        val defaultAuthorityOpt = authorityRepository.findById(1)
        val defaultAuthority = if (defaultAuthorityOpt.isPresent) defaultAuthorityOpt.get() else throw IllegalArgumentException("defaultAuthority not found")

        val akUserEntity = AkUserEntity()
        akUserEntity.email = email
        akUserEntity.passwordHash = passwordHash
        akUserEntity.authorities = Arrays.asList(defaultAuthority)
        akUserEntity.firstName = firstName
        akUserEntity.lastName = lastName
        val saved = userRepository.save(akUserEntity)
        return User.fromEntity(saved)
    }

    @Transactional
    override fun createUserDetails(username: String?): UserDetails {
        if (username == null) {
            throw UsernameNotFoundException("userName is null")
        }

        val userEntity = userRepository.findByEmail(username) ?: throw UsernameNotFoundException("$username not found")
        val user = User.fromEntity(userEntity)

        val userDetails = UserDetailsDefault(user)
        AccountStatusUserDetailsChecker().check(userDetails)

        return userDetails
    }

    @Transactional
    override fun createUserDetails(socialUser: SocialUser): UserDetails {

        var userEntity: AkUserEntity? = null

        val socialEntity = socialRepository.findBySocialIdAndType(socialUser.id, socialUser.socialType)
        if (socialEntity == null) {//new social user

            userEntity = userRepository.findByEmail(socialUser.email)

            if (userEntity == null) {//user have never been registered
                //create record in user table

                val defaultAuthorityOpt = authorityRepository.findById(1)
                val defaultAuthority = if (defaultAuthorityOpt.isPresent) defaultAuthorityOpt.get() else throw IllegalArgumentException("defaultAuthority not found")

                val akUserEntity = AkUserEntity()
                akUserEntity.email = socialUser.email
                akUserEntity.authorities = Arrays.asList(defaultAuthority)
                akUserEntity.firstName = socialUser.firstName
                akUserEntity.lastName = socialUser.lastName
                akUserEntity.imgUrl = socialUser.imgUrl
                userEntity = userRepository.save(akUserEntity)

            }

            //create record in social table
            val socialEntityToCreate = AkSocialEntity()
            socialEntityToCreate.email = socialUser.email
            socialEntityToCreate.type = socialUser.socialType
            socialEntityToCreate.socialId = socialUser.id
            socialEntityToCreate.user = userEntity
            socialRepository.save(socialEntityToCreate)
        } else {
            //user already registered with social
            userEntity = socialEntity.user
        }

        if (userEntity == null) {
            throw IllegalStateException("userEntity is null")
        }

        val user = User.fromEntity(userEntity)

        return UserDetailsDefault(user)
    }
}

