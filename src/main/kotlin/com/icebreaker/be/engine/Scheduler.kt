package com.icebreaker.be.engine

import com.icebreaker.be.facade.user.UserFacade
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class Scheduler(val userFacade: UserFacade) {

    private val log: Logger = LoggerFactory.getLogger(Scheduler::class.java)
    private val dateFormat = SimpleDateFormat("HH:mm:ss")


    @Scheduled(fixedRate = 30000)
    fun reportCurrentTime() {
//        log.info("The time is now {}", dateFormat.format(Date()))
        userFacade.updateUserLastSeenForFakeUsers()
    }

}