package com.qltech.whatsweb.Repository

import android.os.FileObserver
import com.qltech.common.utils.XLog
import com.qltech.messagesaver.common.utils.FileUtils
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.io.FileFilter

interface IFileRepository {
    fun getFileListFlow(filePath: String, fileFilter: FileFilter? = null): Flow<List<File>>
    suspend fun getFileList(filePath: String, fileFilter: FileFilter? = null): List<File>
    suspend fun copyFile(sourcePath: String, newPath: String): Boolean
    suspend fun removeFile(filePath: String): Boolean
}

class FileRepository : IFileRepository {

    companion object {
        private val TAG = FileRepository::class.java.simpleName
        private const val FOLDER_REMOVED = 32768
    }

    override suspend fun getFileList(filePath: String, fileFilter: FileFilter?): List<File> {
        return File(filePath)
            .listFiles(fileFilter)
            ?.toList()
            ?.also { files ->
                XLog.v(TAG, "[getFileList] filePath: $filePath, result: ${files.joinToString { it.name }}")
            }
            ?: emptyList()

    }

    override fun getFileListFlow(filePath: String, fileFilter: FileFilter?): Flow<List<File>> {
        return callbackFlow {
            val resultList: MutableList<File> = ArrayList()

            class FlowFileObserver(path: String) : FileObserver(path, CREATE or DELETE) {
                val parentFile = File(path)

                @Synchronized
                override fun onEvent(event: Int, path: String?) {
                    XLog.v(TAG, "[getFileListFlow] event: $event, path: $path")

                    if (null != path) {
                        val file = File(parentFile, path)
                        when (event) {
                            CREATE -> {
                                if (null == fileFilter || fileFilter.accept(file)) {
                                    resultList.add(file)
                                }
                            }
                            DELETE -> resultList.remove(file)
                        }
                        sendBlocking(resultList)
                    }

                    if (event == FOLDER_REMOVED) {
                        close()
                    }
                }
            }

            XLog.d(TAG, "[getFileListFlow] filePath = $filePath")

            val file = File(filePath)
            if (file.exists()) {
                file.listFiles(fileFilter)?.run(resultList::addAll)
                sendBlocking(resultList)
            } else {
                throw IllegalArgumentException("File${filePath} is not found")
            }

            val fileObserver = FlowFileObserver(filePath)
            XLog.d(TAG, "[getFileListFlow] start")
            fileObserver.startWatching()
            awaitClose {
                XLog.d(TAG, "[getFileListFlow] stop")
                fileObserver.stopWatching()
                resultList.clear()
            }
        }
    }

    override suspend fun copyFile(sourcePath: String, newPath: String): Boolean {
        return FileUtils.copyFile(File(sourcePath), File(newPath))
    }

    override suspend fun removeFile(filePath: String): Boolean {
        val file = File(filePath)

        return file.exists() && file.delete()
    }

}