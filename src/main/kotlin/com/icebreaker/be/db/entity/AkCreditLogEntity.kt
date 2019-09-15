package com.icebreaker.be.db.entity

import com.icebreaker.be.service.model.CreditOperation
import com.icebreaker.be.service.model.CreditType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "AK_CREDIT_LOG", schema = "kofify")
class AkCreditLogEntity {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0

    @get:Enumerated(EnumType.ORDINAL)
    @get:Basic
    @get:Column(name = "CREDIT_TYPE")
    var creditType: CreditType? = null

    @get:Enumerated(EnumType.ORDINAL)
    @get:Basic
    @get:Column(name = "CREDIT_OPERATION")
    var creditOperation: CreditOperation? = null

    @get:Basic
    @get:Column(name = "AMOUNT")
    var amount: Int? = null

    @get:Basic
    @get:Column(name = "DESCRIPTION")
    var description: String? = null

    @get:ManyToOne
    @get:JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    var user: AkUserEntity? = null

    @get:Basic
    @get:CreationTimestamp
    @get:Column(name = "CREATED_AT")
    var createdAt: Timestamp? = null

    @get:Basic
    @get:UpdateTimestamp
    @get:Column(name = "UPDATED_AT")
    var updatedAt: Timestamp? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AkCreditLogEntity?
        return id == that!!.id && creditType == that.creditType && that.user?.id == user?.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id, creditType, user?.id)
    }
}
