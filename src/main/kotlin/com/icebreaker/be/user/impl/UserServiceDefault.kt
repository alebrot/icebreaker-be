package com.icebreaker.be.user.impl

import com.icebreaker.be.auth.UserDetailsDefault
import com.icebreaker.be.controller.user.GET_IMAGE_PATH
import com.icebreaker.be.db.entity.AkSocialEntity
import com.icebreaker.be.db.entity.AkUserEntity
import com.icebreaker.be.db.entity.AkUserImageEntity
import com.icebreaker.be.db.entity.AkUserPositionEntity
import com.icebreaker.be.db.repository.*
import com.icebreaker.be.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.service.model.Authority
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.UserWithDistance
import com.icebreaker.be.service.model.fromEntity
import com.icebreaker.be.user.UserService
import com.icebreaker.be.user.social.impl.SocialUser
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@Service
class UserServiceDefault(val userRepository: UserRepository,
                         val passwordEncoder: PasswordEncoder,
                         val authorityRepository: AuthorityRepository,
                         val socialRepository: SocialRepository,
                         val positionRepository: UserPositionRepository,
                         val userImageRepository: UserImageRepository) : UserService {
    @Transactional
    override fun updateImageForUser(user: User, position: Int, imageName: String) {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val images = userEntity.images
        val foundImage: AkUserImageEntity? = images.firstOrNull { it.position == position }
        if (foundImage != null) {
            foundImage.imageName = imageName
            userImageRepository.save(foundImage)
        } else {
            val akUserImageEntity = AkUserImageEntity()
            akUserImageEntity.imageName = imageName
            akUserImageEntity.position = position
            akUserImageEntity.user = userEntity
            userImageRepository.save(akUserImageEntity)
        }
    }

    override fun getImages(user: User): List<String> {
        val images = userImageRepository.findByUserIdOrderByPosition(user.id)
        return images.mapNotNull { it.imageName }.map {
            ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(GET_IMAGE_PATH)
                    .path(it)
                    .toUriString()
        }
    }

    override fun updateUserProfilePhoto(user: User, imageName: String) {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        userEntity.imgUrl = imageName
        user.imgUrl = imageName
        userRepository.save(userEntity)
    }

    @Transactional
    override fun updateUserPosition(user: User, latitude: BigDecimal, longitude: BigDecimal) {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        var position = userEntity.position

        if (position == null) {
            position = AkUserPositionEntity()
            position.latitude = latitude
            position.longitude = longitude
            positionRepository.save(position)
            userEntity.position = position
            userRepository.save(userEntity)
        } else {
            position.latitude = latitude
            position.longitude = longitude
            positionRepository.save(position)
        }
    }

    @Transactional
    override fun getUsersCloseToUser(user: User, distanceInMeters: Int): List<UserWithDistance> {
//        val userEntity = userRepository.findById(user.id).orElseThrow { IllegalArgumentException("defaultAuthority not found") }
//        val position = userEntity.position ?: IllegalStateException("user {$user.id} position is null")
        val findUsersCloseToUser = userRepository.findUsersCloseToUser(user.id, distanceInMeters)
        return findUsersCloseToUser.map(mapper)
    }

    @Transactional
    override fun getUsersCloseToUserPosition(user: User, distanceInMeters: Int, latitude: BigDecimal, longitude: BigDecimal): List<UserWithDistance> {
        val findUsersCloseToUser = userRepository.findUsersCloseToUserPosition(user.id, distanceInMeters, latitude.toDouble(), longitude.toDouble())
        return findUsersCloseToUser.map(mapper)
    }


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
    override fun createUserDetails(socialUser: SocialUser): Pair<UserDetails, Boolean> {
        var created = false


        var userEntity: AkUserEntity?

        val socialEntity = socialRepository.findBySocialIdAndType(socialUser.id, socialUser.socialType)
        if (socialEntity == null) {//new social user

            userEntity = userRepository.findByEmail(socialUser.email)

            if (userEntity == null) {//user have never been registered
                //create record in user table
                val defaultAuthorityOpt = authorityRepository.findById(1)
                val defaultAuthority = if (defaultAuthorityOpt.isPresent) defaultAuthorityOpt.get() else throw IllegalArgumentException("defaultAuthority not found")

                val akUserEntity = AkUserEntity()
                akUserEntity.email = socialUser.email
                akUserEntity.authorities = listOf(defaultAuthority)
                akUserEntity.firstName = socialUser.firstName
                akUserEntity.lastName = socialUser.lastName
                akUserEntity.imgUrl = socialUser.imgUrl
                userEntity = userRepository.save(akUserEntity)

            }
            created = true
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

        return Pair(UserDetailsDefault(user), created)
    }

    val mapper = { findUsersCloseToUser: Map<String, Any> ->
        val id: Int = findUsersCloseToUser["ID"] as Int
        val email: String = findUsersCloseToUser["EMAIL"] as String
        val passwordHash: String? = findUsersCloseToUser["PASSWORD_HASH"] as? String
        val firstName: String = findUsersCloseToUser["FIRST_NAME"] as String
        val lastName: String = findUsersCloseToUser["LAST_NAME"] as String
        val imgUrl: String? = findUsersCloseToUser["IMG_URL"] as?String
        val authorities: List<Authority> = ArrayList()
        val accountExpired: Boolean = findUsersCloseToUser["ACCOUNT_EXPIRED"] as Boolean
        val accountLocked: Boolean = findUsersCloseToUser["ACCOUNT_LOCKED"] as Boolean
        val credentialsExpired = findUsersCloseToUser["CREDENTIALS_EXPIRED"] as Boolean
        val enabled: Boolean = findUsersCloseToUser["ENABLED"] as Boolean

        val distance: Int = (findUsersCloseToUser["DISTANCE"] as Double).toInt()

        val user = User(id, email, passwordHash, firstName, lastName, imgUrl, authorities, accountExpired, accountLocked, credentialsExpired, enabled)

        UserWithDistance(distance, user)
    }
}

