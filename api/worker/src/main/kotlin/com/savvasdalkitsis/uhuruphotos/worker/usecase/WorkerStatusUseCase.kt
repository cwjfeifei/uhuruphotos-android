/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.worker.usecase

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.savvasdalkitsis.uhuruphotos.api.launchers.onMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class WorkerStatusUseCase @Inject constructor(
    private val workManager: WorkManager,
) {

    fun monitorUniqueJobStatus(jobName: String): Flow<WorkInfo.State> =
        monitorUniqueJob(jobName).map { it.state }

    fun monitorUniqueJob(jobName: String): Flow<WorkInfo> {
        var observer: ((MutableList<WorkInfo>) -> Unit)?
        var liveData: LiveData<MutableList<WorkInfo>>?
        return channelFlow {
            observer = {
                val workInfo = it.getOrNull(0)
                workInfo?.let {
                    CoroutineScope(Dispatchers.Default).launch {
                        send(it)
                    }
                }
            }
            liveData = workManager.getWorkInfosForUniqueWorkLiveData(jobName)
            onMain {
                liveData!!.observeForever(observer!!)
            }
            awaitClose {
                onMain {
                    liveData!!.removeObserver(observer!!)
                }
            }
        }.cancellable()
    }
}