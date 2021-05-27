package com.qltech.messagesaver.ui.message

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.qltech.common.args.ArgsCreator
import com.qltech.common.args.putArgs
import com.qltech.common.extensions.subscribe
import com.qltech.common.extensions.visible
import com.qltech.common.utils.IntentUtils
import com.qltech.whatsweb.BuildConfig
import com.qltech.whatsweb.R
import com.qltech.messagesaver.arguments.MessageArguments
import com.qltech.messagesaver.arguments.MessageDetailArguments
import com.qltech.messagesaver.common.utils.WhatsAppUtils
import com.qltech.messagesaver.common.view.DeletePopupWindow
import com.qltech.firebase.analytics.FirebaseAnalyticHelper
import com.qltech.firebase.analytics.FirebaseEvent
import com.qltech.firebase.remoteconfig.AdRemoteConfig
import com.qltech.messagesaver.model.Message
import com.qltech.messagesaver.model.enums.HomeEnum
import com.qltech.messagesaver.model.enums.OperateEnum
import com.qltech.messagesaver.model.enums.MessageSourceEnum
import com.qltech.messagesaver.ui.adapter.MessageAdapter
import com.qltech.messagesaver.ui.message.detail.MessageDetailActivity
import com.qltech.messagesaver.viewmodel.IMainViewModel
import com.qltech.messagesaver.viewmodel.IMessageViewModel
import com.qltech.messagesaver.viewmodel.MainViewModel
import com.qltech.messagesaver.viewmodel.MessageViewModel
import com.qltech.ui.BaseFragment
import com.qltech.ui.helper.LoadingAdditional
import com.qltech.ui.helper.SnackBarAdditional
import com.qltech.ui.model.SnackMessage
import com.qltech.ui.view.adapter.AdapterData
import com.qltech.ui.view.adapter.MarginItemDecoration
import com.qltech.ui.view.adapter.RecyclerAdapterBase
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.layout_need_permission.*
import kotlinx.android.synthetic.main.layout_no_message.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class MessageFragment : BaseFragment(R.layout.fragment_message), LoadingAdditional, SnackBarAdditional {

    private val arguments: MessageArguments by ArgsCreator()

    private val viewModel: IMessageViewModel by viewModel<MessageViewModel> { parametersOf(arguments) }
    private val mainViewModel: IMainViewModel by sharedViewModel<MainViewModel>()

    private val adapter: MessageAdapter = MessageAdapter()

    private var deleteItem: MenuItem? = null
    private var multiDownloadItem: MenuItem? = null

    override fun onDestroy() {
        adapter.getAdList().forEach {
            it.destroy()
        }
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        empty_view_stub.layoutResource = when (arguments.messageEnum) {
            MessageSourceEnum.WHATS_APP -> R.layout.layout_no_message
            MessageSourceEnum.LOCAL -> R.layout.layout_no_downloaded
        }

        recycler_view.layoutManager = GridLayoutManager(context, 2)
        recycler_view.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.margin_grid).toInt()
            )
        )
        recycler_view.adapter = adapter

        adapter.onItemClickListener =
            object : RecyclerAdapterBase.OnItemClickListener<AdapterData.Data<HomeEnum>> {
                override fun onClick(view: View, data: AdapterData.Data<HomeEnum>) {
                    when (data.getDataType()) {
                        HomeEnum.STATUS,
                        HomeEnum.LOCAL_STATUS -> {
                            viewModel.onMessageClick(data.getData())
                        }
                    }
                }
            }
        adapter.onMessageClickListener = object : MessageAdapter.OnMessageClickListener {

            override fun onDownloadBtnClick(message: Message) {
                XXPermissions.with(this@MessageFragment)
                    .permission(Permission.Group.STORAGE)
                    .request { _, all ->
                        if (all) {
                            downloadMessage(message)
                        } else {
                            showSnackBar(
                                SnackMessage(
                                    SnackMessage.Type.ERROR,
                                    getString(R.string.need_permission_to_work)
                                )
                            )
                        }
                    }
            }

            override fun onShareBtnClick(message: Message) {
                sendMessage(message)
            }

            override fun onSelected(message: Message, isSelected: Boolean) {
                if (isSelected) {
                    if (message.isSaved) {
                        viewModel.setOperateMode(OperateEnum.EDIT)
                    } else {
                        viewModel.setOperateMode(OperateEnum.MULTI_DOWNLOAD)
                    }
                }
                viewModel.onMessageClick(message)
            }
        }
        scroll_view.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            down_to_top.visible = scrollY != 0
        }

        down_to_top.setOnClickListener {
            scroll_view.smoothScrollTo(0, 0)
        }
    }

    private fun initData() {
        bindLoading(viewModel.loadingLiveData)
        bindSnackBar(viewModel.snackBarMessageLiveData)
        bindLoading(mainViewModel.loadingLiveData)
        bindSnackBar(mainViewModel.snackBarMessageLiveData)

        subscribe(mainViewModel.storagePermissionGranted) { isGranted ->
            if (isGranted) {
                need_permission_root?.visible = false
            } else {
                if (null != permission_view_stub) {
                    permission_view_stub.inflate()
                    initRequestStoragePermissionView()
                } else {
                    need_permission_root?.visible = true
                }
            }
        }
        subscribe(viewModel.operateEnum) {
            activity?.invalidateOptionsMenu()
            mainViewModel.setOperateMode(it)
        }
        subscribe(viewModel.messageList) {
            if (it.size != adapter.itemCount) {
                scroll_view.scrollTo(0, 0)
            }
            adapter.setList(it)
            empty_layout?.visible = it.isEmpty()

            if (it.isEmpty() && null != empty_view_stub) {
                empty_view_stub.inflate()
            }
        }
        subscribe(viewModel.toMessageDetailPage) {
            toDetailPage(it)
        }
        subscribe(viewModel.selectedNum) {
            mainViewModel.setSelectedNum(it)
        }
        viewModel.setAdMobEnable(AdRemoteConfig.getAdSwitch() && AdRemoteConfig.getAdStatusGridEnable())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_message, menu)
        deleteItem = menu.findItem(R.id.menu_delete)
        multiDownloadItem = menu.findItem(R.id.menu_multi_download)
        when {
            OperateEnum.EDIT == viewModel.operateEnum.value -> {
                deleteItem!!.isVisible = true
                multiDownloadItem!!.isVisible = false
            }
            OperateEnum.MULTI_DOWNLOAD == viewModel.operateEnum.value -> {
                deleteItem!!.isVisible = false
                multiDownloadItem!!.isVisible = true
            }
            else -> {
                deleteItem!!.isVisible = false
                multiDownloadItem!!.isVisible = false
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                deleteMessage()
                true
            }
            R.id.menu_multi_download -> {
                permissionHelper.runOnPermissionGranted(
                    this@MessageFragment,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) {
                    if (it) {
                        downloadMessageList()
                    } else {
                        showSnackBar(
                            SnackMessage(
                                SnackMessage.Type.ERROR,
                                getString(R.string.need_permission_to_work)
                            )
                        )
                    }
                }
                true
            }
            else -> false
        }
    }

    override fun onResume() {
        super.onResume()

        val isPermissionGranted =
            XXPermissions.isGranted(activity, Permission.Group.STORAGE)

        mainViewModel.setStoragePermission(isPermissionGranted)
        if (isPermissionGranted) {
            viewModel.monitorMessageFileChange()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isResumed) return

        if (!isVisibleToUser) {
            viewModel.setOperateMode(OperateEnum.NONE)
        }
    }

    private fun initRequestStoragePermissionView() {
        button_permission.setOnClickListener {
            tryToGetStatus()
        }
    }

    private fun tryToGetStatus() {
        XXPermissions.with(this@MessageFragment)
            .permission(Permission.Group.STORAGE)
            .request { _, all ->
                mainViewModel.onReceivePermissionResult(all)
                if (all) {
                    viewModel.monitorMessageFileChange()
                }
            }
    }

    private fun downloadMessage(message: Message) {
        viewModel.downloadMessage(message)
    }

    private fun downloadMessageList() {
        viewModel.downloadMessageList()
    }

    private fun sendMessage(message: Message) {
        val context = context ?: return

        IntentUtils.shareImage(
            context,
            File(message.path),
            "${BuildConfig.APPLICATION_ID}.provider",
            WhatsAppUtils.WHATS_APP_PKG_NAME
        )
    }

    private fun toDetailPage(bean: Message) {
        val context = context ?: return

        if (arguments.messageEnum == MessageSourceEnum.WHATS_APP) {
            FirebaseAnalyticHelper.logEvent(FirebaseEvent.VIEW_DETAILS_LEFT)
        } else {
            FirebaseAnalyticHelper.logEvent(FirebaseEvent.VIEW_DETAILS_RIGHT)
        }

        val intent = Intent(context, MessageDetailActivity::class.java)
        val args = MessageDetailArguments(arguments.messageEnum, bean)
        intent.putArgs(args)
        startActivity(intent)
    }

    private fun deleteMessage() {
        XXPermissions.with(this@MessageFragment)
            .permission(Permission.Group.STORAGE)
            .request { _, all ->
                if (all) {
                    showDeletePopupWindow()
                } else {
                    showSnackBar(
                        SnackMessage(
                            SnackMessage.Type.ERROR,
                            getString(R.string.need_permission_to_work)
                        )
                    )
                }
            }
    }

    private fun showDeletePopupWindow() {
        activity?.let {
            val popupWindow = DeletePopupWindow(it)
            popupWindow.setOnDetermineListener(object : DeletePopupWindow.OnDetermineListener {
                override fun onDetermine() {
                    popupWindow.dismiss()
                    FirebaseAnalyticHelper.logEvent(FirebaseEvent.BATCH_DELETION)
                    viewModel.onDeleteClick()
                }
            })
            popupWindow.showAtLocation(view, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        }
    }

    override fun onBackPressed(): Boolean {
        return if (OperateEnum.EDIT == viewModel.operateEnum.value || OperateEnum.MULTI_DOWNLOAD == viewModel.operateEnum.value) {
            viewModel.setOperateMode(OperateEnum.NONE)
            true
        } else {
            super.onBackPressed()
        }
    }
}
