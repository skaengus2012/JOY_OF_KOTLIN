package com.nlab.joyofkotlin.chapter1

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Doohyun
 */
class DonutShopKtTest {

    @Test fun `buy donut when selected 5 times donut`() {
        val creditCard = CreditCard()
        val purchase = buyDonut(creditCard, amount = 5)

        assertEquals(Donut.PRICE * 5, purchase.payment.amount)
        assertEquals(creditCard, purchase.payment.creditCard)
    }

}