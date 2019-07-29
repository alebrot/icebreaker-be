package com.icebreaker.be.facade.credit

import com.icebreaker.be.exception.CreditsNotAvailableException
import com.icebreaker.be.service.chat.ChatService
import com.icebreaker.be.service.credit.CreditService
import com.icebreaker.be.service.model.User
import org.springframework.stereotype.Service

interface CreditFacade {
    fun handleCreditsForNewChatCreation(user: User, userIds: List<Int>)
}

@Service
class CreditFacadeDefault(val creditService: CreditService, val chatService: ChatService) : CreditFacade {

    val creditsToCreateChatRequired: Int = 1

    fun assertAvailableCredits(credits: Int, user: User) {
        val rewardCredits = creditService.getAvailableCredits(user).credits
        if (rewardCredits > 0 && rewardCredits - credits >= 0) {

        } else {
            val rewardedCredits = creditService.rewardCredits(user)
            if (rewardedCredits.credits > 0 && rewardedCredits.credits - credits >= 0) {

            } else {
                throw CreditsNotAvailableException("No credits available", credits)
            }
        }
    }

    override fun handleCreditsForNewChatCreation(user: User, userIds: List<Int>) {
        val newChat = chatService.isNewChat(user, userIds)
        if (newChat) {
            assertAvailableCredits(creditsToCreateChatRequired, user)
            creditService.removeCredits(creditsToCreateChatRequired, user)
        }
    }
}