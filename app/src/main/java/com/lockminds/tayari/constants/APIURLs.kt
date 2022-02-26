package com.lockminds.tayari.constants


class APIURLs{
    companion object {
        @JvmField val BUSINESS_MEDIA_URL = "https://app.tayari.co.tz/storage/"
        @JvmField val BASE_URL = "https://api.tayari.co.tz/api/"
        @JvmField val PAYMENT_SAVE = "savePayment"
        @JvmField val PAYMENT_CHECK = "paymentCheck"
        @JvmField val TIGO_PUSH_BILL = "https://accessgwtest.tigo.co.tz:8443/TAYARIPAYMENTS2DM-PushBillpay";
        @JvmField val TIGO_TOKEN = "https://accessgwtest.tigo.co.tz:8443/TAYARIPAYMENTS2DM-GetToken";
        @JvmField val PAY_USERNAME = "TAYARIPAYMENTS"
        @JvmField val PAY_PASSWORD = "k1BePdk"
        @JvmField val PAY_BILLER = "25565744455"
    }
}