package com.icebreaker.be.facade.credit

import com.icebreaker.be.exception.CreditsNotAvailableException
import com.icebreaker.be.service.chat.ChatService
import com.icebreaker.be.service.chat.model.Chat
import com.icebreaker.be.service.credit.CreditService
import com.icebreaker.be.service.model.Store
import com.icebreaker.be.service.model.User
import org.springframework.stereotype.Service

interface CreditFacade {
    fun handleCreditsForNewChatCreation(user: User, userIds: List<Int>, store: Store)
    fun handleCreditsForDiscoveringChatRequest(user: User, chat: Chat, store: Store): Chat
}

@Service
class CreditFacadeDefault(val creditService: CreditService, val chatService: ChatService) : CreditFacade {

    val creditsToCreateChatRequired: Int = 1
    val creditsToDiscoverChatRequest: Int = 1

    fun assertAvailableCredits(credits: Int, user: User, store: Store) {
        val rewardCredits = creditService.getAvailableCredits(user).credits
        if (rewardCredits > 0 && rewardCredits - credits >= 0) {
        } else {
            val products = creditService.getProducts(store)
            throw CreditsNotAvailableException(products, "No credits available", credits)
        }
    }

    override fun handleCreditsForNewChatCreation(user: User, userIds: List<Int>, store: Store) {
        val newChat = chatService.isNewChat(user, userIds)
        if (newChat) {
            assertAvailableCredits(creditsToCreateChatRequired, user, store)
            creditService.removeCreditsForChatCreation(creditsToCreateChatRequired, user, store)
        }
    }

    override fun handleCreditsForDiscoveringChatRequest(user: User, chat: Chat, store: Store): Chat {
        val newChat: Chat
        if (chat.enabled == false) {
            assertAvailableCredits(creditsToDiscoverChatRequest, user, store)
            newChat = chatService.enableChat(chat, user)
            creditService.removeCreditsForChatDiscovery(creditsToDiscoverChatRequest, user,store)
        } else {
            newChat = chat
        }

        return newChat
    }
}