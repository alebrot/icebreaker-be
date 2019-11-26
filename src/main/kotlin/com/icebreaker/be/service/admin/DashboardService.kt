package com.icebreaker.be.service.admin

import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.service.admin.model.UserByAvailablePoint
import org.springframework.stereotype.Service


interface DashboardService {
    fun countUsersByAvailablePoints(): List<UserByAvailablePoint>
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

}