package com.sorrowblue.comicviewer.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asFlow
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.sorrowblue.comicviewer.app.databinding.FragmentWorkBinding
import com.sorrowblue.comicviewer.app.databinding.ItemWorkHeaderBinding
import com.sorrowblue.comicviewer.app.databinding.ItemWorkInfoBinding
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.jetpack.binding.viewBinding
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class WorkFragment : FrameworkFragment(R.layout.fragment_work) {

    private val binding: FragmentWorkBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val workManager = WorkManager.getInstance(requireContext())
        val workInfo = workManager.getWorkInfosByTagLiveData("observable")
        val running = WorkInfoAdapter()
        val runningHeader = HeaderAdapter().apply { submitList(listOf("Running")) }
        binding.recyclerView.adapter = ConcatAdapter(runningHeader, running)
        workInfo.asFlow().map { workInfoList ->
            workInfoList.sortedBy {
                when (it.state) {
                    WorkInfo.State.ENQUEUED -> 2
                    WorkInfo.State.RUNNING -> 0
                    WorkInfo.State.SUCCEEDED -> 4
                    WorkInfo.State.FAILED -> 1
                    WorkInfo.State.BLOCKED -> 3
                    WorkInfo.State.CANCELLED -> 5
                }
            }
        }.onEach {
            running.submitList(it)
        }.launchInWithLifecycle()
    }
}

internal class WorkInfoAdapter : ListAdapter<WorkInfo, WorkInfoAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<WorkInfo>() {
        override fun areItemsTheSame(oldItem: WorkInfo, newItem: WorkInfo) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: WorkInfo, newItem: WorkInfo) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<ItemWorkInfoBinding>(parent, ItemWorkInfoBinding::inflate) {
        fun bind(item: WorkInfo) {
            binding.headline.text = item.id.toString()
            binding.supportingText.text = "${item.progress.getInt("count", -1)} count"
            binding.trailingSupportingText.text = item.state.name
        }
    }
}

internal class HeaderAdapter : ListAdapter<String, HeaderAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<ItemWorkHeaderBinding>(parent, ItemWorkHeaderBinding::inflate) {
        fun bind(item: String) {
            binding.headline.text = item
        }
    }
}
