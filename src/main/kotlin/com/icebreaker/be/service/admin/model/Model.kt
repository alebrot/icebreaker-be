package com.icebreaker.be.service.admin.model

import java.time.LocalDate

data class UserByAvailablePoint(val userCount: Int, val points: Int)
data class UserByDate(val userCount: Int, val date: LocalDate)
