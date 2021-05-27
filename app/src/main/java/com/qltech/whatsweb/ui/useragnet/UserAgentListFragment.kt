package com.qltech.whatsweb.ui.useragnet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.qltech.whatsweb.R
import com.qltech.whatsweb.ui.useragnet.adapter.UserAgentAdapter
import com.qltech.whatsweb.ui.useragnet.model.UserAgent
import com.qltech.whatsweb.util.UserAgentConstants
import kotlinx.android.synthetic.main.fragment_useragent_list.*

class UserAgentListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_useragent_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            initRecyclerView()
        }
    }

    private fun initRecyclerView() {
        if (context == null)
            return

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = UserAgentAdapter(getUserAgentList(), context)
    }

    private fun getUserAgentList(): List<UserAgent> {
        val list = ArrayList<UserAgent>()
        list.add(
            UserAgent(
                0,
                UserAgentConstants.OS.MAC, UserAgentConstants.Brower.SAFARI,
                UserAgentConstants.UserAngent.MAC_SAFARI,
                false
            )
        )
        list.add(
            UserAgent(
                1,
                UserAgentConstants.OS.MAC, UserAgentConstants.Brower.CHROME,
                UserAgentConstants.UserAngent.MAC_CHROME,
                false
            )
        )
        list.add(
            UserAgent(
                2,
                UserAgentConstants.OS.MAC, UserAgentConstants.Brower.OPERA,
                UserAgentConstants.UserAngent.MAC_OPERA,
                false
            )
        )
        list.add(
            UserAgent(
                3,
                UserAgentConstants.OS.WINDOW, UserAgentConstants.Brower.SAFARI,
                UserAgentConstants.UserAngent.WINDOW_SAFARI,
                false
            )
        )
        list.add(
            UserAgent(
                4,
                UserAgentConstants.OS.WINDOW, UserAgentConstants.Brower.CHROME,
                UserAgentConstants.UserAngent.WINDOW_CHROME,
                false
            )
        )
        list.add(
            UserAgent(
                5,
                UserAgentConstants.OS.WINDOW, UserAgentConstants.Brower.OPERA,
                UserAgentConstants.UserAngent.WINDOW_OPERA,
                false
            )
        )
        return list
    }

}