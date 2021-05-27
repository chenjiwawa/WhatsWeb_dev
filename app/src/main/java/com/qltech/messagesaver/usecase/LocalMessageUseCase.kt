package com.qltech.messagesaver.usecase

import com.qltech.common.mergeListFlow
import com.qltech.common.utils.XLog
import com.qltech.messagesaver.model.Message
import com.qltech.whatsweb.Repository.IFileRepository
import com.qltech.whatsweb.Repository.IMessageCacheRepository
import com.qltech.whatsweb.Repository.IMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.File

class LocalMessageUseCase(
    messageRepository: IMessageRepository,
    messageCacheRepository: IMessageCacheRepository,
    fileRepository: IFileRepository,
) : MessageUseCase(messageRepository, messageCacheRepository, fileRepository) {

    companion object {
        private val TAG = LocalMessageUseCase::class.java.simpleName
    }

    override fun getMessageFlow(): Flow<List<Message>> {
        val localPath = messageRepository.getLocalPath()
        File(localPath).mkdirs()

        val filter = messageRepository.getFileFilter()

        return messageRepository.getLocalModelPaths()
            .mergeListFlow { path ->
                XLog.v(TAG, "[getLocalStatusFlow] path: $path")
                fileRepository.getFileListFlow(path, filter)
                    .catch {
                        emit(emptyList())
                    }
            }
            .map {
                it.toMessageList(true)
            }
    }

    override suspend fun getMessage(): List<Message> {
        val filter = messageRepository.getFileFilter()

        return messageRepository.getLocalModelPaths()
            .flatMap { path ->
                XLog.v(TAG, "[getLocalStatus] path: $path")
                fileRepository.getFileList(path, filter)
            }
            .toMessageList(true)
    }
}
