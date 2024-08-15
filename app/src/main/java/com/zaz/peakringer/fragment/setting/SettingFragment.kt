package com.zaz.peakringer.fragment.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaz.peakringer.activity.CommonActivity
import com.zaz.peakringer.databinding.FragementSettingBinding
import com.zaz.peakringer.utils.startFragment
import com.zaz.support.R
import com.zaz.support.base.BaseFragment
import com.zaz.support.base.BaseViewModel
import com.zaz.support.dercoration.VerticalDecoration
import com.zaz.support.utils.color

class SettingFragment : BaseFragment() {
    private lateinit var binding: FragementSettingBinding
    private val viewModel: SettingItemVM by viewModels()
    private lateinit var settingItemsAdapter: SettingItemsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragementSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingItemsAdapter =
            SettingItemsAdapter(viewModel.getSettingItems(requireContext()), ::onSettingItemClick)
        with(binding.settingItems) {
            this.adapter = settingItemsAdapter
            addItemDecoration(
                VerticalDecoration(
                    "#cccccc".color, 1f
                )
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun getBaseViewModel(): BaseViewModel {
        return viewModel
    }

    private fun onSettingItemClick(item: SettingItemBean) {
        when (item.id) {
            SettingItemBean.ID_TOGGLE -> {
                startFragment(CommonActivity.FRAGMENT_TYPE_TOGGLE)
            }

            SettingItemBean.ID_FEEDBACK -> {
                startFragment(CommonActivity.FRAGMENT_TYPE_FEEDBACK)
            }
        }
    }
}