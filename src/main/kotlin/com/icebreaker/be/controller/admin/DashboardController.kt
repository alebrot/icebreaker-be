package com.icebreaker.be.controller.admin

import com.icebreaker.be.service.admin.DashboardService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


const val COUNT_USERS_BY_AVAILABLE_POINTS = "/dashboard/user/points"

interface DashboardController {
    @GetMapping(COUNT_USERS_BY_AVAILABLE_POINTS)
    fun countUsersByAvailablePoints(): CountUsersByAvailablePointsResponse
}

@RestController
class DashboardControllerDefault(val dashboardService: DashboardService) : DashboardController {

    @PreAuthorize("hasAuthority('ADMIN')")
    override fun countUsersByAvailablePoints(): CountUsersByAvailablePointsResponse {
        val countUsersByAvailablePoints = dashboardService.countUsersByAvailablePoints()
        return CountUsersByAvailablePointsResponse(countUsersByAvailablePoints.map { UserByAvailablePointDto(it.userCount, it.points) })
    }

}