package com.zaz.peakringer.fragment.setting

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
import com.zaz.peakringer.bean.StringItemBean
import com.zaz.peakringer.databinding.FragementSettingBinding
import com.zaz.peakringer.utils.disableFeature
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
            SettingItemsAdapter(viewModel.getSettingItems(requireContext()), ::onSettingItemClick,::onSubtitleClick)
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
    }

    override fun getBaseViewModel(): BaseViewModel {
        return viewModel
    }

    private fun onSettingItemClick(position:Int,item: SettingItemBean) {
        when (item.id) {
            SettingItemBean.ID_TOGGLE -> {
                val isOpened = requireContext().isFeatureOpen()
                if(isOpened){
                    //open->close
                    //show dialog content
                    val menu = viewModel.getCloseMenu(requireContext())
                    BottomItemDialog.show(childFragmentManager,menu,::onDisableMenuItemClick)
                }else{
                    //close->open
                    item.subTitle = requireContext().getString(R.string.enabled)
                    item.title = requireContext().getString(R.string.disable)
                    settingItemsAdapter.notifyItemChanged(position)
                    viewModel.enable(requireContext())
                    PRToast.show(requireContext().applicationContext,requireContext().getString(R.string.enabled))
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

    private fun onSubtitleClick(position: Int,data:SettingItemBean){
        when(data.subTitleClickTag){
            SettingItemBean.SUBTITLE_CLICK_TAG_MODIFY_TEMP_CLOSE_TIME->{
                val menu = viewModel.getCloseMenu(requireContext())
                BottomItemDialog.show(childFragmentManager,menu,::onDisableMenuItemClick)
            }
        }
    }

    private fun onDisableMenuItemClick(item:StringItemBean){
        when(item.id){
            StringItemBean.PowerCloseType.TYPE_CLOSE_NOW->{
                with(requireContext()){
                    disableFeature()
                    closeItemUi(getString(R.string.enable),getString(R.string.disabled))
                }
            }
            StringItemBean.PowerCloseType.TYPE_CLOSE_MIN_30->{
                with(requireContext()){
                    viewModel.disableTemporary(this,30 * 60)
                    closeItemUi(getString(R.string.enable_immediately),getString(R.string.auto_enable_after,"30${getString(com.zaz.support.R.string.time_min)}"))
                }
            }
            StringItemBean.PowerCloseType.TYPE_CLOSE_MIN_60->{
                with(requireContext()){
                    viewModel.disableTemporary(this,60 * 60)
                    closeItemUi(getString(R.string.enable_immediately),getString(R.string.auto_enable_after,"60${getString(com.zaz.support.R.string.time_min)}"))
                }
            }
            StringItemBean.PowerCloseType.TYPE_CLOSE_HOUR_3->{
                with(requireContext()){
                    viewModel.disableTemporary(this,3* 60 * 60)
                    closeItemUi(getString(R.string.enable_immediately),getString(R.string.auto_enable_after,"3${getString(com.zaz.support.R.string.time_hour)}"))
                }
            }
            else->{
                DatePickerBottomDialog.show(childFragmentManager){
                    viewModel.disableBefore(requireContext(),it)
                    closeItemUi(getString(R.string.enable_immediately),getString(R.string.auto_enable_after, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it * 1000)))
                }
            }
        }
    }

    private fun closeItemUi(title:String,subtitle:String){
        PRToast.show(requireContext().applicationContext,title)
        settingItemsAdapter.getItem(SettingItemBean.ID_TOGGLE)?.let {
            settingItemsAdapter.notifyItemChanged(it.apply {
                this.title = title
                this.subTitle = subtitle
            })
        }
    }
}