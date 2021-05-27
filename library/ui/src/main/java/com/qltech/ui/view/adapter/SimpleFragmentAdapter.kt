package com.qltech.ui.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout

class SimpleFragmentAdapter<T : ITabPage>(fm: FragmentManager, private var pageList: List<T>) :
    FragmentStatePagerAdapter(fm) {

    fun setPageList(pageList: List<T>) {
        this.pageList = pageList
        notifyDataSetChanged()
    }

    fun putData(page: T) {
        if (!pageList.contains(page)) return

        pageList = pageList.toMutableList().apply {
            set(indexOf(page), page)
        }
    }

    fun getTabPage(position: Int): T = pageList[position]

    override fun getItem(position: Int): Fragment {
        return getTabPage(position).getFragment()
    }

    override fun getCount(): Int {
        return pageList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return pageList[position].tabTitle
    }

    fun getPageList():List<T> {
        return pageList
    }

    fun indexOf(t: T): Int {
        return pageList.indexOf(t)
    }

    override fun getItemPosition(item: Any): Int {
        return POSITION_NONE
    }

    //更新所有TabLayout title的值
    fun setAllTabLayoutTitle(tab:TabLayout,titles: List<String>) {
        for (i in pageList.indices) {
            tab.getTabAt(i)!!.text = titles[i]
        }
    }

}

interface ITabPage {
    val tabTitle: String

    fun getFragment(): Fragment
}