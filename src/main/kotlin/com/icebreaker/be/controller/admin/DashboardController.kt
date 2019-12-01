package com.icebreaker.be.controller.admin

import com.icebreaker.be.service.admin.DashboardService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


const val COUNT_USERS_BY_AVAILABLE_POINTS = "/dashboard/user/points"
const val COUNT_ONLINE_USERS_BY_DATE = "/dashboard/user/date"

interface DashboardController {
    @GetMapping(COUNT_USERS_BY_AVAILABLE_POINTS)
    fun countUsersByAvailablePoints(): CountUsersByAvailablePointsResponse

    @GetMapping(COUNT_ONLINE_USERS_BY_DATE)
    fun countOnlineUsersByDate(): CountOnlineUsersByDateResponse
}

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
class DashboardControllerDefault(val dashboardService: DashboardService) : DashboardController {

    override fun countUsersByAvailablePoints(): CountUsersByAvailablePointsResponse {
        val countUsersByAvailablePoints = dashboardService.countUsersByAvailablePoints()
        return CountUsersByAvailablePointsResponse(countUsersByAvailablePoints.map { UserByAvailablePointDto(it.userCount, it.points) })
    }

    override fun countOnlineUsersByDate(): CountOnlineUsersByDateResponse {
        val countOnlineUsersByDate = dashboardService.countOnlineUsersByDate()
        return CountOnlineUsersByDateResponse(countOnlineUsersByDate.map { UserByDateDto(it.userCount, it.date) })
    }

}