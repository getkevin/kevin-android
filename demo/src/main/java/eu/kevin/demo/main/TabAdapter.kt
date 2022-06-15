package eu.kevin.demo.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import eu.kevin.demo.screens.accountlinking.AccountLinkingFragment
import eu.kevin.demo.screens.payment.PaymentFragment

internal class TabAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AccountLinkingFragment()
            1 -> PaymentFragment()
            else -> throw IllegalStateException("No fragment for position $position")
        }
    }
}