package com.zaz.peakringer.fragment.setting

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaz.peakringer.Constant
import com.zaz.peakringer.R
import com.zaz.peakringer.activity.CommonActivity
import com.zaz.peakringer.databinding.FragementSettingBinding
import com.zaz.peakringer.utils.changeFeatureOpen
import com.zaz.peakringer.utils.startFragment
import com.zaz.support.base.BaseFragment
import com.zaz.support.base.BaseViewModel
import com.zaz.support.dercoration.VerticalDecoration
import com.zaz.support.dialog.PRDialog
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.SpUtils
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

    private fun onSettingItemClick(position:Int,item: SettingItemBean) {
        when (item.id) {
            SettingItemBean.ID_TOGGLE -> {
                if(item.switchValue){
                    //open->close
                    PRDialog.Builder()
                        .setTitle(getString(R.string.confirm))
                        .setContent(getString(R.string.close_feature_confirm_content))
                        .setLeftBtnName(getString(R.string.cancel))
                        .setRightBtnName(getString(R.string.close))
                        .setRightBtnListener {
                            item.switchValue = false
                            settingItemsAdapter.notifyItemChanged(position)
                            requireContext().changeFeatureOpen(false)
                        }
                        .show(childFragmentManager)
                }else{
                    //close->open
                    item.switchValue = true
                    settingItemsAdapter.notifyItemChanged(position)
                    requireContext().changeFeatureOpen(true)
                }
            }

            SettingItemBean.ID_FEEDBACK -> {
                startFragment(CommonActivity.FRAGMENT_TYPE_FEEDBACK)
            }
            SettingItemBean.ID_CONTACT_US->{
                //联系我们
                val email = "peakringer@outlook.com"
                PRDialog.Builder()
                    .setTitle(getString(R.string.tips))
                    .setContent(getString(R.string.contact_us_dialog_content, email))
                    .setRightBtnName(getString(com.zaz.support.R.string.sure))
                    .setRightBtnListener {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            setData(Uri.fromParts("mailto", email, null))
                        }
                        try {
                            startActivity(intent)
                        } catch (_: Exception) {
                        }
                    }
                    .show(childFragmentManager)
            }
            SettingItemBean.ID_PRIVACY_PROTOCOL->{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constant.H5.PRIVACY_PROTOCOL))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    PRToast.show(requireContext(),getString(R.string.install_browser_first))
                }
            }
        }
    }
}