package eu.kevin.demo.main

interface MainViewCallback {
    fun onBackPressed()
    fun onLinkAccountPressed()
    fun onMakeBankPaymentPressed()
    fun onMakeCardPaymentPressed()
}