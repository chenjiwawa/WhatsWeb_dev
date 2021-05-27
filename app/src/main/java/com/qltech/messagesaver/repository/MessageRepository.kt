package com.qltech.whatsweb.Repository

import android.os.Environment
import com.qltech.messagesaver.common.utils.FileUtils
import com.qltech.messagesaver.common.utils.WhatsAppUtils
import java.io.FileFilter

interface IMessageRepository {
    fun getWhatsAppPaths(): List<String>
    fun getLocalPath(): String
    fun getLocalModelPaths(): List<String>
    fun getFileFilter(): FileFilter
}

class MessageRepository : IMessageRepository {

    override fun getWhatsAppPaths(): List<String> {
        return listOf(
            getWhatsAppRootPath(),
            getWhatsAppBizRootPath(),
            getFMWhatsAppRootPath(),
            getGBWhatsAppRootPath(),
            getYOWhatsAppRootPath(),
            getYO2WhatsAppRootPath(),
            getCooCooWhatsAppRootPath(),
            getWhatsAppPlusRootPath(),
            getHeyWhatsAppRootPath(),
            getAeroWhatsAppRootPath()
        ).map {
            "$it/Media/.Statuses/"
        }
    }

    override fun getLocalPath(): String {
        return "${getSdCardPath()}/StatusKeeper/downloaded/statuses/"
    }

    override fun getLocalModelPaths(): List<String> {
        return listOf(getLocalPath())
    }

    override fun getFileFilter(): FileFilter {
        return FileFilter {
            if (it.isFile) {
                with(it.name) {
                    endsWith(FileUtils.DEPUTY_JPG) || endsWith(FileUtils.DEPUTY_GIF) || endsWith(FileUtils.DEPUTY_MP4)
                }
            } else {
                false
            }
        }
    }

    private fun getWhatsAppRootPath(): String? {
        return "${getSdCardPath()}/${WhatsAppUtils.WHATS_APP_ROOT_PATH}/"
    }

    private fun getFMWhatsAppRootPath(): String {
        return "${getSdCardPath()}/${WhatsAppUtils.FM_WHATS_APP_ROOT_PATH}"
    }

    private fun getGBWhatsAppRootPath(): String {
        return "${getSdCardPath()}/${WhatsAppUtils.GB_WHATS_APP_ROOT_PATH}"
    }

    private fun getYOWhatsAppRootPath(): String {
        return "${getSdCardPath()}/${WhatsAppUtils.YO_WHATS_APP_ROOT_PATH}"
    }

    private fun getYO2WhatsAppRootPath(): String {
        return "${getSdCardPath()}/${WhatsAppUtils.YO_2_WHATS_APP_ROOT_PATH}"
    }

    private fun getCooCooWhatsAppRootPath(): String {
        return "${getSdCardPath()}/${WhatsAppUtils.COO_COO_WHATS_APP_ROOT_PATH}"
    }

    private fun getWhatsAppPlusRootPath(): String {
        return "${getSdCardPath()}/${WhatsAppUtils.WHATS_APP_PLUS_ROOT_PATH}"
    }

    private fun getHeyWhatsAppRootPath(): String {
        return "${getSdCardPath()}/${WhatsAppUtils.HEY_WHATS_APP_ROOT_PATH}"
    }

    private fun getAeroWhatsAppRootPath(): String {
        return "${getSdCardPath()}/${WhatsAppUtils.AERO_WHATS_APP_ROOT_PATH}"
    }


    private fun getWhatsAppBizRootPath(): String? {
        return "${getSdCardPath()}/${WhatsAppUtils.WHATS_APP_BIZ_ROOT_PATH}/"
    }

    private fun getSdCardPath(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }
}