package com.qltech.whatsweb.Repository

import com.qltech.common.helper.CacheDelegate
import com.qltech.common.utils.XLog
import com.qltech.messagesaver.model.Message
import com.qltech.messagesaver.model.MessageList

interface IMessageCacheRepository {
    fun getMessageList(): List<Message>
    fun setMessageList(messageList: List<Message>)
}

class MessageCacheRepository : IMessageCacheRepository {

    companion object {
        private val TAG = MessageCacheRepository::class.java.simpleName
    }

    private var cache: MessageList by CacheDelegate(MessageList::class.java)

    override fun getMessageList(): List<Message> {
        XLog.v(TAG, "[getStatusList] size: ${cache.list.size}, $this")
        return cache.list
    }

    override fun setMessageList(messageList: List<Message>) {
        XLog.v(TAG, "[setStatusList] size: ${messageList.size}, $this")
        cache = MessageList(messageList)
    }

}
