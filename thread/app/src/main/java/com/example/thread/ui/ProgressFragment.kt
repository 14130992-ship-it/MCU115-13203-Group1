package com.example.thread.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.thread.TaskState
import com.example.thread.TaskViewModel
import com.example.thread.databinding.FragmentProgressBinding

class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TaskState.Idle -> {
                    binding.circularProgressBar.progress = 0
                    binding.circularProgressBar.centerText = ""
                    binding.tvProgressStatus.text = "Idle"
                }
                is TaskState.Preparing -> {
                    binding.circularProgressBar.progress = 0
                    binding.circularProgressBar.centerText = ""
                    binding.tvProgressStatus.text = "Preparing..."
                }
                is TaskState.Working -> {
                    binding.circularProgressBar.progress = state.progress
                    binding.circularProgressBar.centerText = "${state.progress}%"
                    binding.tvProgressStatus.text = "Working... ${state.progress}%"
                }
                is TaskState.Finished -> {
                    binding.circularProgressBar.progress = 100
                    binding.circularProgressBar.centerText = "100%"
                    binding.tvProgressStatus.text = "背景工作結束!"
                }
                is TaskState.Canceled -> {
                    binding.circularProgressBar.centerText = ""
                    binding.tvProgressStatus.text = "Canceled"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
