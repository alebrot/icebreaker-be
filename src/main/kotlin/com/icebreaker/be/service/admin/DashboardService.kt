package com.icebreaker.be.service.admin

import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.service.admin.model.UserByAvailablePoint
import com.icebreaker.be.service.admin.model.UserByDate
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.fromEntity
import org.springframework.stereotype.Service
import java.math.BigInteger


interface DashboardService {
    fun countUsersByAvailablePoints(): List<UserByAvailablePoint>
    fun countOnlineUsersByDate(): List<UserByDate>
    fun getNewUsers(): List<User>
}

const val defaultLimit = 6;
const val defaultOffset = 0;

@Service
class DashboardServiceDefault(val userRepository: UserRepository) : DashboardService {
    override fun countUsersByAvailablePoints(): List<UserByAvailablePoint> {
        return userRepository.countUsersByAvailablePoints(defaultLimit, defaultOffset).map { m: Map<String, Int> ->
            val count = m["COUNT"] as Int
            val credits = m["CREDITS"] as Int
            UserByAvailablePoint(count, credits)
        }
    }

    override fun countOnlineUsersByDate(): List<UserByDate> {
        return userRepository.countOnlineUsersByDate(defaultLimit, defaultOffset).map { m: Map<String, Any> ->
            val count = m["COUNT"] as BigInteger
            val date = m["DATE"] as java.sql.Date
            UserByDate(count.toInt(), date.toLocalDate())
        }
    }

    override fun getNewUsers(): List<User> {
        return userRepository.getNewUsers(defaultLimit, defaultOffset).map {
            User.fromEntity(it)
        }
    }


}