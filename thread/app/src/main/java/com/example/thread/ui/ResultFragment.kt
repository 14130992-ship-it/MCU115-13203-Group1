package com.example.thread.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.thread.TaskState
import com.example.thread.TaskViewModel
import com.example.thread.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TaskState.Idle -> {
                    binding.tvResultStatus.text = "Idle"
                }
                is TaskState.Preparing -> {
                    binding.tvResultStatus.text = "Preparing..."
                }
                is TaskState.Working -> {
                    binding.tvResultStatus.text = "Working..."
                }
                is TaskState.Finished -> {
                    binding.tvResultStatus.text = "背景工作結束!"
                }
                is TaskState.Canceled -> {
                    binding.tvResultStatus.text = "Canceled"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
