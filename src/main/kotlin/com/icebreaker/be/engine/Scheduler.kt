package com.icebreaker.be.engine

import com.icebreaker.be.facade.user.UserFacade
import com.icebreaker.be.service.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

const val offlineUsersDelay = 1000 * 60 * 60 * 6 //6 hours
const val onlineUsersDelay = 10000 //10 sec

@Component
class Scheduler(val userFacade: UserFacade,
                val userService: UserService) {

    private val logger: Logger = LoggerFactory.getLogger(Scheduler::class.java)

    @Scheduled(fixedRate = 20000, fixedDelayString = "\${random.long($onlineUsersDelay)}")
    fun slowScheduler() {

        val realUsersOnlineAndNoChatsWithFakeUsers = userService.getRealUsersOnlineToSendInvitations()
        if (realUsersOnlineAndNoChatsWithFakeUsers.isNotEmpty()) {
            logger.info("online users to send invitation, size: {}", realUsersOnlineAndNoChatsWithFakeUsers.size)
            userFacade.sendInvitationTo(realUsersOnlineAndNoChatsWithFakeUsers)
        }

        val users = userService.getRealUsersOnlineToSendMoreInvitations()
        if (users.isNotEmpty()) {
            logger.info("online users to send more invitations, size: {}", users.size)
            userFacade.sendInvitationTo(users)
        }

    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 12, fixedDelayString = "\${random.long($offlineUsersDelay)}")//once every 12 hours
    fun offlineUsers() {
        val users = userService.getRealUsersOfflineToSendInvitations()
        if (users.isNotEmpty()) {
            logger.info("offline users to send invitation, size: {}", users.size)
            userFacade.sendInvitationTo(users)
        }
    }

    @Scheduled(fixedRate = 5000)
    fun updateLastSeen() {
        userFacade.updateUserLastSeenForFakeUsers()
    }

}