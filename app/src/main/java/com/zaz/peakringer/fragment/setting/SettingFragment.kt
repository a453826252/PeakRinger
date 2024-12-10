package com.zaz.peakringer.fragment.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaz.peakringer.Constant
import com.zaz.peakringer.R
import com.zaz.peakringer.activity.CommonActivity
import com.zaz.peakringer.bean.StringItemBean
import com.zaz.peakringer.databinding.FragementSettingBinding
import com.zaz.peakringer.manager.ListenContactsSwitchStateManager
import com.zaz.peakringer.utils.disableFeature
import com.zaz.peakringer.utils.enableFeature
import com.zaz.peakringer.utils.isFeatureOpen
import com.zaz.peakringer.utils.startFragment
import com.zaz.support.base.BaseFragment
import com.zaz.support.base.BaseViewModel
import com.zaz.support.dercoration.VerticalDecoration
import com.zaz.support.dialog.PRDialog
import com.zaz.support.dialog.bottom.BottomItemDialog
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.color
import java.text.SimpleDateFormat

class SettingFragment : BaseFragment() {
    private lateinit var binding: FragementSettingBinding
    private val viewModel: SettingItemVM by viewModels()
    private lateinit var settingItemsAdapter: SettingItemsAdapter
    companion object{
        private const val TAG = "SettingFragment"
    }
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
                    1f,
                    "#cccccc".color
                )
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
        ListenContactsSwitchStateManager.addObserver(this){ state->
            settingItemsAdapter.getItem(SettingItemBean.ID_TOGGLE)?.let {
                settingItemsAdapter.notifyItemChanged(it.apply {
                    this.subTitle = viewModel.getStateTxt(requireContext(),state.isOpen,state.autoOpenAt)
                })
            }
        }
    }

    override fun getBaseViewModel(): BaseViewModel {
        return viewModel
    }

    private fun onSettingItemClick(position:Int,item: SettingItemBean) {
        when (item.id) {
            SettingItemBean.ID_TOGGLE -> {
                val menu = viewModel.getCloseMenu(requireContext())
                BottomItemDialog.show(childFragmentManager,menu,::onDisableMenuItemClick)
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
            SettingItemBean.ID_ABOUT->{
                //关于我们
                val email = "peakringer@outlook.com"
                val txt = "Powered by PeakRinger\n($email)"
                PRDialog.Builder()
                    .setTitle(getString(R.string.about_us))
                    .setContent(txt)
                    .setRightBtnName("OK")
                    .show(childFragmentManager)
            }
        }
    }

    private fun onDisableMenuItemClick(item:StringItemBean){
        when(item.id){
            StringItemBean.PowerCloseType.TYPE_OPEN->{
                with(requireContext()){
                    enableFeature()
                    PRToast.show(requireContext(),getString(R.string.enabled))
                }
            }
            StringItemBean.PowerCloseType.TYPE_CLOSE_NOW->{
                with(requireContext()){
                    disableFeature()
                    PRToast.show(requireContext(),getString(R.string.disabled))
                }
            }
            StringItemBean.PowerCloseType.TYPE_CLOSE_MIN_30->{
                with(requireContext()){
                    viewModel.disableTemporary(this,30 * 60)
                    PRToast.show(requireContext(),getString(R.string.auto_enable_after,"30${getString(com.zaz.support.R.string.time_min)}"))
                }
            }
            StringItemBean.PowerCloseType.TYPE_CLOSE_MIN_60->{
                with(requireContext()){
                    viewModel.disableTemporary(this,60 * 60)
                    PRToast.show(requireContext(),getString(R.string.auto_enable_after,"60${getString(com.zaz.support.R.string.time_min)}"))
                }
            }
            StringItemBean.PowerCloseType.TYPE_CLOSE_HOUR_3->{
                with(requireContext()){
                    viewModel.disableTemporary(this,3* 60 * 60)
                    PRToast.show(requireContext(),getString(R.string.auto_enable_after,"3${getString(com.zaz.support.R.string.time_hour)}"))
                }
            }
            else->{
                DatePickerBottomDialog.show(childFragmentManager){
                    Log.d(TAG, "onDisableMenuItemClick: selected time=${it / 1000}")
                    if(System.currentTimeMillis() >= it){
                        PRToast.show(requireContext(),getString(R.string.selected_time_less_then_current))
                        viewModel.enable(requireContext())
                    }else{
                        viewModel.disableBefore(requireContext(),it / 1000)
                        PRToast.show(requireContext(),getString(R.string.auto_enable_after, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it)))
                    }
                }
            }
        }
    }

}