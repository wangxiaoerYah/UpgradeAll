package net.xzos.upgradeAll.server.app.engine.js

import net.xzos.upgradeAll.data.json.gson.JSReturnData
import net.xzos.upgradeAll.data.json.nongson.JSCache
import net.xzos.upgradeAll.server.ServerContainer
import net.xzos.upgradeAll.server.app.engine.api.CoreApi

class JavaScriptEngine internal constructor(
        internal val logObjectTag: Pair<String, String>,
        URL: String?,
        jsCode: String?,
        debugMode: Boolean = false
) : CoreApi {

    private val javaScriptCoreEngine = JavaScriptCoreEngine(logObjectTag, URL, jsCode)

    init {
        if (!debugMode) {
            Log.i(this.logObjectTag, TAG, String.format("JavaScriptCoreEngine: jsCode: \n%s", jsCode))  // 只打印一次 JS 脚本
        }
        javaScriptCoreEngine.jsUtils.debugMode = debugMode
        JSCache.clearCache(logObjectTag)
    }


    override suspend fun getDefaultName(): String? = javaScriptCoreEngine.getDefaultName()

    override suspend fun getAppIconUrl(): String? = javaScriptCoreEngine.getAppIconUrl()

    override suspend fun getReleaseNum(): Int = javaScriptCoreEngine.getReleaseNum()

    override suspend fun getVersionNumber(releaseNum: Int): String? = when {
        releaseNum >= 0 -> javaScriptCoreEngine.getVersionNumber(releaseNum)
        else -> null
    }

    override suspend fun getChangelog(releaseNum: Int): String? = when {
        releaseNum >= 0 -> javaScriptCoreEngine.getChangelog(releaseNum)
        else -> null
    }

    override suspend fun getReleaseDownload(releaseNum: Int): Map<String, String> = when {
        releaseNum >= 0 -> javaScriptCoreEngine.getReleaseDownload(releaseNum)
        else -> mapOf()
    }

    override suspend fun downloadReleaseFile(downloadIndex: Pair<Int, Int>): Boolean {
        val jsSuccessDownload = javaScriptCoreEngine.downloadReleaseFile(downloadIndex)
        return if (jsSuccessDownload)
            true
        else
            downloadFile(downloadIndex)
    }

    // TODO: 无用接口
    override suspend fun getReleaseInfo(): JSReturnData? = null

    internal suspend fun downloadFile(downloadIndex: Pair<Int, Int>, externalDownloader: Boolean = false): Boolean {
        Log.e(logObjectTag, TAG, "downloadFile: 尝试直接下载")
        val downloadReleaseMap = getReleaseDownload(downloadIndex.first)
        val fileIndex = downloadIndex.second
        val fileNameList = downloadReleaseMap.keys.toList()
        val fileName =
                if (fileIndex < fileNameList.size)
                    fileNameList[fileIndex]
                else
                    null
        val downloadUrl = downloadReleaseMap[fileName]
        return if (fileName != null && downloadUrl != null) {
            javaScriptCoreEngine.jsUtils.downloadFile(fileName, downloadUrl, externalDownloader = externalDownloader)
            true
        } else
            false
    }

    companion object {
        private const val TAG = "JavaScriptEngine"
        private val Log = ServerContainer.Log
    }
}
