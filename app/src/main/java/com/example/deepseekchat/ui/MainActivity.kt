package com.example.deepseekchat.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deepseekchat.R          // ✅ 添加这行
import com.example.deepseekchat.databinding.ActivityMainBinding
import com.example.deepseekchat.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChatAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 初始化 API Key 显示
        binding.etApiKey.setText(viewModel.apiKey.value)

        // 保存 API Key
        binding.btnSaveKey.setOnClickListener {
            val key = binding.etApiKey.text.toString().trim()
            if (key.isNotEmpty()) {
                viewModel.saveApiKey(key)
                Toast.makeText(this, "API Key 已保存", Toast.LENGTH_SHORT).show()
            }
        }

        // 模型切换监听（已升级到 V4）
        binding.radioGroupModel.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioChat -> viewModel.setModel("deepseek-v4-flash")   // ✅ 通用模型
                R.id.radioReasoner -> viewModel.setModel("deepseek-v4-pro") // ✅ 高性能推理
            }
        }

        // 发送消息
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.sendMessage(text)
                binding.etMessage.text?.clear()
            }
        }

        // 观察消息列表
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messages.collectLatest { list ->
                    adapter.submitList(list.toList())
                    if (list.isNotEmpty()) {
                        binding.recyclerView.smoothScrollToPosition(list.size - 1)
                    }
                }
            }
        }

        // 加载状态
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collectLatest { loading ->
                    binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                    binding.btnSend.isEnabled = !loading
                }
            }
        }

        // 错误提示
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collectLatest { error ->
                    if (error != null) {
                        binding.tvError.text = error
                        binding.tvError.visibility = View.VISIBLE
                    } else {
                        binding.tvError.visibility = View.GONE
                    }
                }
            }
        }
    }
}
