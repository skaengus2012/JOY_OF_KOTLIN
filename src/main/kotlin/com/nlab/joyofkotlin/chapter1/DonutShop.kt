package com.nlab.joyofkotlin.chapter1

/**
 * @author Doohyun
 */

fun buyDonut(creditCard: CreditCard, amount: Int = 1): Purchase {
    (0 until amount).map { Donut }

    return Purchase(
        donuts = (0 until amount).map { Donut() },
        payment = Payment(creditCard, amount * Donut.PRICE)
    )
}