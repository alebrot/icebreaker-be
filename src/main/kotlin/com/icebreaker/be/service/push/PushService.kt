package com.icebreaker.be.service.push

import com.icebreaker.be.db.entity.AkPushEntity
import com.icebreaker.be.db.repository.PushRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.service.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface PushService {
    fun subscribe(user: User, id: String, pushToken: String)
}

@Service
class NotificationServiceDefault(val userRepository: UserRepository,
                                 val pushRepository: PushRepository) : PushService {

    @Transactional
    override fun subscribe(user: User, id: String, pushToken: String) {

        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        var push = userEntity.push
        if (push != null) {
            push.userId = id
            push.pushToken = pushToken
            pushRepository.save(push)
        } else {
            push = AkPushEntity()
            push.userId = id
            push.pushToken = pushToken
            pushRepository.save(push)

            userEntity.push = push
            userRepository.save(userEntity)
        }

    }
}