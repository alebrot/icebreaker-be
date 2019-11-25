package com.icebreaker.be.controller.admin

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


const val COUNT_USERS_BY_AVAILABLE_POINTS = "/dashboard/user/points"

interface DashboardController {

    @GetMapping(COUNT_USERS_BY_AVAILABLE_POINTS)
    fun countUsersByAvailablePoints(): CountUsersByAvailablePointsResponse
}

@RestController
class DashboardControllerDefault : DashboardController {

    @PreAuthorize("hasAuthority('ADMIN')")
    override fun countUsersByAvailablePoints(): CountUsersByAvailablePointsResponse {
        return CountUsersByAvailablePointsResponse(arrayListOf(UserByAvailablePoint(100, 10)))
    }

}