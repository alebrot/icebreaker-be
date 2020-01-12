package com.icebreaker.be.service.user.impl

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.icebreaker.be.CoreProperties
import com.icebreaker.be.ImageProperties
import com.icebreaker.be.auth.UserDetailsDefault
import com.icebreaker.be.controller.user.GET_IMAGE_PATH
import com.icebreaker.be.db.entity.*
import com.icebreaker.be.db.repository.*
import com.icebreaker.be.ext.getIntInRange
import com.icebreaker.be.ext.toBoolean
import com.icebreaker.be.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.service.model.*
import com.icebreaker.be.service.social.impl.SocialUser
import com.icebreaker.be.service.user.UserService
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.min

@Service
class UserServiceDefault(val userRepository: UserRepository,
                         val deletedUserRepository: DeletedUserRepository,
                         val passwordEncoder: PasswordEncoder,
                         val authorityRepository: AuthorityRepository,
                         val socialRepository: SocialRepository,
                         val positionRepository: UserPositionRepository,
                         val userImageRepository: UserImageRepository,
                         val imageProperties: ImageProperties,
                         val coreProperties: CoreProperties) : UserService {

    private val fakeEmailDomain = "@email.com"
    private val creditsLimit = 5
    private val onlineSinceInMinutes = 1L

    private val cache: Cache<String, Set<Int>> = Caffeine.newBuilder()
            .expireAfterWrite(onlineIntervalInMinutes.toLong(), TimeUnit.MINUTES)
            .build()

    private val cacheKey = "cacheKey"

    override fun getRealUsersOnlineCount(): Int {
        val now = LocalDateTime.now().minusMinutes(onlineSinceInMinutes)
        return userRepository.countAllByEmailNotContainingAndLastSeenAfter(fakeEmailDomain, Timestamp.valueOf(now))
    }

    @Transactional
    override fun getRealUsersOnline(): List<User> {
        val now = LocalDateTime.now().minusMinutes(onlineSinceInMinutes)
        val allByEmailNotContainingAndLastSeenAfter = userRepository.getAllByEmailNotContainingAndLastSeenAfter(fakeEmailDomain, Timestamp.valueOf(now))
        val predicate = { akUserEntity: AkUserEntity -> akUserEntity.credits < creditsLimit }
        return allByEmailNotContainingAndLastSeenAfter
                .filter(predicate)
                .map { User.fromEntity(it) }
    }


    @Transactional
    override fun getRealUsersOnlineWithLimitedAmountOfCreditsAndNoChatsWithFakeUsers(): List<User> {
        val now = LocalDateTime.now().minusMinutes(onlineSinceInMinutes)
        val realUsers = userRepository.getAllByEmailNotContainingAndLastSeenAfterAndCreditsLessThan(fakeEmailDomain, Timestamp.valueOf(now), creditsLimit)
        return filterUsers(realUsers).map { User.fromEntity(it) }
    }

    @Transactional
    override fun getRealUsersOnlineAndNoChatsWithFakeUsers(): List<User> {
        val now = LocalDateTime.now().minusMinutes(onlineSinceInMinutes)
        val realUsers = userRepository.getAllByEmailNotContainingAndLastSeenAfter(fakeEmailDomain, Timestamp.valueOf(now))
        return filterUsers(realUsers).map { User.fromEntity(it) }
    }

    private fun filterUsers(realUsers: List<AkUserEntity>): List<AkUserEntity> {

        val fakeIds: Set<Int> = cache.get(cacheKey) { t ->
            if (t == cacheKey) {
                userRepository.findAllByEmailContaining(fakeEmailDomain, Int.MAX_VALUE, 4).map { it.id }.toSet()
            } else {
                HashSet()
            }
        } ?: HashSet()

        return realUsers
                .filter {
                    val chats = it.chats
                    val allUsersFromUserChats = chats
                            .flatMap { akChatEntity -> akChatEntity.users }
                            .map { akUserEntity -> akUserEntity.id }
                            .toSet()
                    fakeIds.intersect(allUsersFromUserChats).isEmpty()
                }
    }


    @Transactional
    override fun updateUser(user: User): User {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        userEntity.bio = user.bio
        userEntity.gender = user.gender
        userEntity.lastSeen = Timestamp.valueOf(user.lastSeen)
        val saved = userRepository.save(userEntity)
        return User.fromEntity(saved)
    }

    @Transactional
    override fun getUserById(userId: Int): User {
        val userEntity = userRepository.findById(userId).toKotlinNotOptionalOrFail()
        return User.fromEntity(userEntity)
    }

    override fun getUserByEmailOrFail(email: String): User {
        val userEntity = userRepository.findByEmail(email)
                ?: throw IllegalArgumentException("user with email $email not found")
        return User.fromEntity(userEntity)
    }

    override fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)?.let {
            val fromEntity = User.fromEntity(it)
            fromEntity
        }
    }

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

    override fun getImages(user: User): List<UserImage> {
        val images = userImageRepository.findByUserIdOrderByPosition(user.id)
        return images.mapNotNull {
            val position = it.position
            val imageName = it.imageName
            if (imageName != null) {
                val url = ServletUriComponentsBuilder.fromHttpUrl(imageProperties.host)
                        .path(GET_IMAGE_PATH)
                        .path(imageName)
                        .toUriString()
                UserImage(position, url)
            } else {
                null
            }
        }
    }

    override fun getImageNameByPosition(user: User, position: Int): String? {
        return userImageRepository.findByUserIdAndPosition(user.id, position)?.imageName
    }

    @Transactional
    override fun deleteImageByPosition(user: User, position: Int) {
        return userImageRepository.deleteByUserIdAndPosition(user.id, position)
    }

    override fun getUserProfileImageName(user: User): String? {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        return userEntity.imgUrl
    }


    override fun updateUserProfilePhoto(user: User, imageName: String?) {
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
    override fun getUsersCloseToUser(user: User, distanceInMeters: Int, age: IntRange, gender: Gender?, online: Boolean?, limit: Int, offset: Int): List<UserWithDistance> {
        val distance = min(distanceInMeters, coreProperties.maxDistance)

        val interval = when {
            online == null -> {
                null
            }
            online -> {
                onlineIntervalInMinutes
            }
            else -> {
                onlineIntervalInMinutes
            }
        }

        val findUsersCloseToUser = if (coreProperties.fake) {
            userRepository.findUsersCloseToUserWithFakeUsers(user.id, distance, age.first, age.last, gender?.ordinal, interval, limit, offset)
        } else {
            userRepository.findUsersCloseToUser(user.id, distance, age.first, age.last, gender?.ordinal, interval, limit, offset)
        }
        return findUsersCloseToUser.map(mapper)
    }

    @Transactional
    override fun getUsersCloseToUserPosition(user: User, distanceInMeters: Int, latitude: BigDecimal, longitude: BigDecimal, age: IntRange, gender: Gender?, online: Boolean?, limit: Int, offset: Int): List<UserWithDistance> {

        val interval = when {
            online == null -> {
                null
            }
            online -> {
                onlineIntervalInMinutes
            }
            else -> {
                onlineIntervalInMinutes
            }
        }

        val findUsersCloseToUser = if (coreProperties.fake) {
            userRepository.findUsersCloseToUserPositionWithFakeUsers(user.id, distanceInMeters, latitude.toDouble(), longitude.toDouble(), age.first, age.last, gender?.ordinal, interval, limit, offset)
        } else {
            userRepository.findUsersCloseToUserPosition(user.id, distanceInMeters, latitude.toDouble(), longitude.toDouble(), age.first, age.last, gender?.ordinal, interval, limit, offset)
        }
        return findUsersCloseToUser.map(mapper)
    }

    @Transactional
    override fun getFakeUsersByGender(gender: Gender, limit: Int, offset: Int): List<User> {
        return userRepository.findAllByEmailContainingAndGender(fakeEmailDomain, gender.ordinal, limit, offset).map {
            User.fromEntity(it)
        }
    }

    @Transactional
    override fun getFakeUsers(limit: Int, offset: Int): List<User> {
        return userRepository.findAllByEmailContaining(fakeEmailDomain, limit, offset).map {
            User.fromEntity(it)
        }
    }

    @Transactional
    override fun getFakeUsers(distanceInMeters: Int, limit: Int, offset: Int): List<UserWithDistance> {
        return userRepository.findAllByEmailContaining(fakeEmailDomain, limit, offset).map {
            val distance = Random().getIntInRange(1, distanceInMeters)
            UserWithDistance(distance, User.fromEntity(it))
        }.shuffled()
    }

    override fun isFakeUser(user: User): Boolean {
        return user.email.contains(fakeEmailDomain)
    }

    @Transactional
    override fun getDistanceBetweenUsers(user1: User, user2: User): Int? {
        return userRepository.findDistanceBetweenUsers(user1.id, user2.id)
    }

    @Transactional
    override fun createUser(email: String, password: String, firstName: String, lastName: String?, birthday: LocalDate): User {
        val passwordHash = passwordEncoder.encode(password)
        val defaultAuthorityOpt = authorityRepository.findById(1)
        val defaultAuthority = if (defaultAuthorityOpt.isPresent) defaultAuthorityOpt.get() else throw IllegalArgumentException("defaultAuthority not found")

        val akUserEntity = AkUserEntity()
        akUserEntity.email = email
        akUserEntity.passwordHash = passwordHash
        akUserEntity.authorities = listOf(defaultAuthority)
        akUserEntity.firstName = firstName
        akUserEntity.lastName = lastName
        akUserEntity.birthday = java.sql.Date.valueOf(birthday)
        val saved = userRepository.save(akUserEntity)
        return User.fromEntity(saved)
    }

    @Transactional
    override fun deleteUser(user: User, reason: String?) {

        val akUserEntity = userRepository.findByEmail(user.email)
                ?: throw IllegalArgumentException("User with ${user.email} not found")
        val akDeletedUserEntity = AkDeletedUserEntity()
        akDeletedUserEntity.firstName = akUserEntity.firstName
        akDeletedUserEntity.lastName = akUserEntity.lastName
        akDeletedUserEntity.email = akUserEntity.email
        akDeletedUserEntity.bio = akUserEntity.bio
        akDeletedUserEntity.birthday = akUserEntity.birthday
        akDeletedUserEntity.userCreatedAt = akUserEntity.createdAt
        akDeletedUserEntity.userUpdatedAt = akUserEntity.updatedAt
        akDeletedUserEntity.reason = reason
        akDeletedUserEntity.credits = akUserEntity.credits
        akDeletedUserEntity.creditsUpdatedAt = akUserEntity.creditsUpdatedAt
        akDeletedUserEntity.gender = akUserEntity.gender
        akDeletedUserEntity.invitedBy = akUserEntity.invitedBy

        deletedUserRepository.save(akDeletedUserEntity)
        userRepository.delete(akUserEntity)
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
                akUserEntity.birthday = java.sql.Date.valueOf(socialUser.birthDay)
                akUserEntity.gender = socialUser.gender

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
        val imgUrl: String? = findUsersCloseToUser["IMG_URL"] as? String
        val birthday: java.sql.Date = findUsersCloseToUser["BIRTHDAY"] as java.sql.Date
        val authorities: List<Authority> = ArrayList()
        val accountExpired: Int = findUsersCloseToUser["ACCOUNT_EXPIRED"] as Int
        val accountLocked: Int = findUsersCloseToUser["ACCOUNT_LOCKED"] as Int
        val credentialsExpired: Int = findUsersCloseToUser["CREDENTIALS_EXPIRED"] as Int
        val enabled: Int = findUsersCloseToUser["ENABLED"] as Int
        val genderInt: BigInteger? = findUsersCloseToUser["GENDER"] as? BigInteger?

        val lastSeen: Timestamp = findUsersCloseToUser["LAST_SEEN"] as Timestamp

        val createdAt: Timestamp = findUsersCloseToUser["CREATED_AT"] as Timestamp

//        val credits: Int = findUsersCloseToUser["CREDITS"] as Int
//
//        val creditsUpdatedAt: Timestamp = findUsersCloseToUser["CREDITS_UPDATED_AT"] as Timestamp
//
//        val admobCount: Int = findUsersCloseToUser["ADMOB_COUNT"] as Int
//
//        val admobUpdatedAt: Timestamp = findUsersCloseToUser["ADMOB_UPDATED_AT"] as Timestamp

        val gender: Gender? = if (genderInt != null) Gender.values()[genderInt.toInt()] else null

        val bio: String? = findUsersCloseToUser["BIO"] as? String

        val distance: Int = (findUsersCloseToUser["DISTANCE"] as Double).toInt()
        val invitedBy: Int? = findUsersCloseToUser["INVITED_BY"] as? Int

        val user = User(id, email, passwordHash, firstName, lastName, imgUrl, authorities, accountExpired.toByte().toBoolean(), accountLocked.toByte().toBoolean(), credentialsExpired.toByte().toBoolean(), birthday.toLocalDate(), bio, gender, enabled.toByte().toBoolean(), lastSeen.toLocalDateTime(), createdAt.toLocalDateTime(), null, invitedBy)

        UserWithDistance(distance, user)
    }
}

