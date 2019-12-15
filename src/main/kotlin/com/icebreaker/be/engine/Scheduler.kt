package com.icebreaker.be.engine

import com.icebreaker.be.facade.user.UserFacade
import com.icebreaker.be.service.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat

@Component
class Scheduler(val userFacade: UserFacade,
                val userService: UserService) {

    private val logger: Logger = LoggerFactory.getLogger(Scheduler::class.java)
    private val dateFormat = SimpleDateFormat("HH:mm:ss")


    @Scheduled(fixedRate = 30000)
    fun reportCurrentTime() {
        userFacade.updateUserLastSeenForFakeUsers()

        val users = userService.getRealUsersOnlineWithLimitedAmountOfCreditsAndNoChatsWithFakeUsers()
        if (users.isNotEmpty()) {
            logger.info("online users with limited amount of credits and now chats found, size: {}", users.size)
            userFacade.sendInvitationTo(users)
        }

        val realUsersOnlineAndNoChatsWithFakeUsers = userService.getRealUsersOnlineAndNoChatsWithFakeUsers()

        if (users.isNotEmpty()) {
            logger.info("online users with now chats found, size: {}", users.size)
            userFacade.sendInvitationTo(realUsersOnlineAndNoChatsWithFakeUsers)
        }
    }


}