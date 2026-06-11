package io.github.anshu7vyas.stocked.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.github.anshu7vyas.stocked.ui.home.HomeFragment
import io.github.anshu7vyas.stocked.ui.shopping.ShoppingListFragment
import io.github.anshu7vyas.stocked.ui.timeline.TimelineFragment

/** Replaced by Compose paging in Phase 3; kept on the deprecated API until then. */
@Suppress("DEPRECATION")
class HomePagerAdapter(
    fragmentManager: FragmentManager,
    private val tabCount: Int,
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> HomeFragment()
        1 -> ShoppingListFragment()
        2 -> TimelineFragment()
        else -> throw IllegalArgumentException("Unknown tab position $position")
    }

    override fun getCount(): Int = tabCount
}
