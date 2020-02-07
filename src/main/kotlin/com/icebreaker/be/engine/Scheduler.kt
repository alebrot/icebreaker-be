package com.icebreaker.be.engine

import com.icebreaker.be.ext.getIntInRange
import com.icebreaker.be.facade.user.UserFacade
import com.icebreaker.be.service.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

const val offlineUsersDelay: Long = 1000 * 60 * 60 * 6 //6 hours
const val onlineUsersDelay = 30000L //30 sec

@Component
class Scheduler(val userFacade: UserFacade,
                val userService: UserService) {

    private val logger: Logger = LoggerFactory.getLogger(Scheduler::class.java)

    @Scheduled(fixedDelay = onlineUsersDelay)
    fun onlineScheduler() {
        Thread.sleep(Random().getIntInRange(0, onlineUsersDelay.toInt()).toLong())
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

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    fun offlineUsers() {
        val l: Int = (offlineUsersDelay / 8).toInt()
        Thread.sleep(Random().getIntInRange(0, l).toLong())
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