package com.qltech.messagesaver.usecase

import androidx.core.net.toUri
import com.qltech.common.mergeListFlow
import com.qltech.common.utils.XLog
import com.qltech.messagesaver.common.utils.FileUtils
import com.qltech.messagesaver.model.Message
import com.qltech.messagesaver.model.enums.MessageEnum
import com.qltech.whatsweb.Repository.IFileRepository
import com.qltech.whatsweb.Repository.IMessageCacheRepository
import com.qltech.whatsweb.Repository.IMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.File
import kotlin.system.measureTimeMillis

interface IMessageUseCase {
    fun getMessageFlow(): Flow<List<Message>>
    suspend fun getMessage(): List<Message>
    fun getCacheMessage(): List<Message>

    suspend fun downloadMessage(message: Message): Boolean
    suspend fun removeMessage(messageList: List<Message>)
    suspend fun removeMessage(message: Message): Boolean
}

open class MessageUseCase(
    protected val messageRepository: IMessageRepository,
    protected val messageCacheRepository: IMessageCacheRepository,
    protected val fileRepository: IFileRepository,
) : IMessageUseCase {

    companion object {
        private val TAG = MessageUseCase::class.java.simpleName
    }

    override fun getMessageFlow(): Flow<List<Message>> {
        val filter = messageRepository.getFileFilter()

        return messageRepository.getWhatsAppPaths()
            .mergeListFlow { path ->
                XLog.v(TAG, "[getStatusFlow] path: $path")
                fileRepository.getFileListFlow(path, filter)
                    .catch {
                        emit(emptyList())
                    }
            }
            .map {
                it.toMessageList(false)
            }
    }

    override suspend fun getMessage(): List<Message> {
        val filter = messageRepository.getFileFilter()

        return messageRepository.getWhatsAppPaths()
            .flatMap { path ->
                XLog.v(TAG, "[getStatus] path: $path")
                fileRepository.getFileList(path, filter)
            }
            .toMessageList(false)
    }

    override fun getCacheMessage(): List<Message> {
        return messageCacheRepository.getMessageList()
    }

    override suspend fun downloadMessage(message: Message): Boolean {
        val isSuccess: Boolean
        var spendTime: Long = 0
        var errorMessage: String? = null

        try {
            spendTime = measureTimeMillis {
                val newPath = messageRepository.getLocalPath() + message.name
                isSuccess = fileRepository.copyFile(message.path, newPath)
            }
        } catch (e: Exception) {
            errorMessage = e.message
            throw e
        } finally {
        }
        return isSuccess
    }

    override suspend fun removeMessage(messageList: List<Message>) {
        messageList.forEach {
            removeMessage(it)
        }
    }

    override suspend fun removeMessage(message: Message): Boolean {
        return fileRepository.removeFile(message.path)
    }

    protected fun List<File>.toMessageList(isSaved: Boolean): List<Message> {
        return try {
            map { file ->
                Message(
                    file.path,
                    file.name,
                    file.toUri().toString(),
                    if (FileUtils.isMp4(file)) MessageEnum.VIDEO else MessageEnum.IMAGE,
                    file.lastModified(),
                    isSaved
                )
            }.sortedByDescending {
                it.lastModify
            }.also {
                messageCacheRepository.setMessageList(it)
            }
        } catch (e: ConcurrentModificationException) {
            emptyList()
        }
    }
}
