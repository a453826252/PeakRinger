package com.zaz.peakringer.fragment.contacts.edit

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.zaz.peakringer.Constant
import com.zaz.peakringer.R
import com.zaz.peakringer.databinding.FragmentEditOrAddContactBinding
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.support.acitivityresultcontract.CropImage
import com.zaz.support.base.BaseFragment
import com.zaz.support.base.BaseViewModel
import com.zaz.support.dialog.bottom.BottomItemDialog
import com.zaz.support.dialog.permission.PermissionDialog
import com.zaz.support.dialog.permission.PermissionItem
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.finishFragment
import com.zaz.support.utils.pickPhoneNum
import com.zaz.support.utils.string
import java.io.File

class EditOrAddContactFragment : BaseFragment(), View.OnClickListener {
    private lateinit var binding: FragmentEditOrAddContactBinding
    private var cropAvatarUri: Uri? = null
    private var cacheCameraFileUri: Uri? = null
    private val viewModel:EditOrAddContactVM by viewModels()
    private lateinit var pickPhotoLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var cropImageLauncher: ActivityResultLauncher<CropImage.CropRequest>
    private var contact:ContactsBean?=null
    companion object {
        const val TAG = "EditOrAddContactFragment"

        @JvmStatic
        fun show(fm:FragmentManager,containerId:Int,contactsBean: ContactsBean?):EditOrAddContactFragment{
           val fragment =  EditOrAddContactFragment().apply {
                contactsBean?.let {
                    arguments = Bundle().apply {
                        putParcelable("contact", it)
                    }
                }
            }
            show(fm,fragment,containerId,TAG)
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
            Log.d(TAG, "pickPhoto,uri=$it")
            it?.let {
                crop(it)
            }
        }
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()){
            Log.d(TAG, "takePicture: result=$it")
            if(it){
                cacheCameraFileUri?.let {src->
                    crop(src)
                }
            }
        }

        cropImageLauncher = registerForActivityResult(CropImage()){
            Log.d(TAG, "cropImage: uri=$it")
            it?.let {
                cacheCameraFileUri?.let {cache->
                    requireContext().contentResolver.delete(cache,null,null)
                }
                cropAvatarUri = it
                showAvatar(it)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditOrAddContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        viewModel.avatarUpdate.observe(viewLifecycleOwner){
            Log.d(TAG, "refresh avatar,path=$it")
            showAvatar(it)
        }
    }

    override fun getBaseViewModel(): BaseViewModel = viewModel

    private fun showAvatar(uri: Uri){
        Glide.with(requireContext())
            .load(uri)
            .error(R.mipmap.ic_default_avatar_gray)
            .placeholder(R.mipmap.ic_default_avatar_gray)
            .into(binding.editOrAddContactExistAvatar)
    }

    private fun crop(uri: Uri){
        val targetFile = viewModel.getTmpAvatarUri()
        Log.d(TAG, "targetUri: $targetFile")
        targetFile?.let { target->
            val request = CropImage.CropRequest(uri,target)
            cropImageLauncher.launch(request)
        }
    }
    private fun initView(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            binding.editOrAddContactInputName.setText(it.getString("input_name", ""))
            binding.editOrAddContactInputPhone.setText(it.getString("input_phone", ""))
        }
        contact = arguments?.getParcelable<ContactsBean>("contact")
        contact?.let {
            Log.d(TAG, "initView: edit,phoneNum=${it.displayPhoneNumber}")
            binding.editOrAddContactTitle.text = R.string.edit_contact.string(requireContext())
            if (!it.icon.isNullOrBlank()) {
                Glide.with(requireContext())
                    .load(File(it.icon))
                    .error(R.mipmap.ic_default_avatar_gray)
                    .placeholder(R.mipmap.ic_default_avatar_gray)
                    .into(binding.editOrAddContactExistAvatar)
            }
            binding.editOrAddContactInputName.setText(it.name)
            with(binding.editOrAddContactInputPhone) {
                setText(it.displayPhoneNumber)
                isEnabled = false
            }
        }
        binding.editOrAddContactCancel.setOnClickListener(this)
        binding.editOrAddContactDo.setOnClickListener(this)
        binding.editOrAddContactExistAvatar.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("input_name", binding.editOrAddContactInputName.text.toString())
        outState.putString("input_phone", binding.editOrAddContactInputPhone.text.toString())
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_or_add_contact_do -> {
                addContact()
            }

            R.id.edit_or_add_contact_cancel -> {
                finishFragment()
            }

            R.id.edit_or_add_contact_exist_avatar -> {
                onAvatarClick()
            }
        }
    }

    private fun onAvatarClick(){
        if (!requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            onSelectPhotoItemClick(AvatarSelectItem(AvatarSelectItem.TYPE_GALLERY,""))
        }else{
            val items = mutableListOf<AvatarSelectItem>().apply {
                add(AvatarSelectItem(AvatarSelectItem.TYPE_CAMERA,R.string.take_photo.string(requireContext())))
                add(AvatarSelectItem(AvatarSelectItem.TYPE_GALLERY,R.string.pick_from_gallery.string(requireContext())))
            }
            BottomItemDialog.show(childFragmentManager, items,::onSelectPhotoItemClick)
        }
    }

    private fun onSelectPhotoItemClick(item: AvatarSelectItem){
        Log.d(TAG, "onSelectPhotoClick: item=$item")
        if(item.type == AvatarSelectItem.TYPE_GALLERY){
            pickPhotoLauncher.launch(PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()
            )
        }else if(item.type == AvatarSelectItem.TYPE_CAMERA){
            PermissionDialog.checkAndShow(requireActivity(),childFragmentManager,PermissionItem(
                Manifest.permission.CAMERA,
                R.string.camera.string(requireContext()),
                R.mipmap.ic_camera_blue
            )){
                val cacheFileUri = viewModel.getWaitCropUri(requireContext())
                if(cacheFileUri != null){
                    this.cacheCameraFileUri = cacheFileUri
                    cameraLauncher.launch(cacheFileUri)
                }else{
                    PRToast.show(requireContext(),R.string.err_retry.string(requireContext(),Constant.ErrorCode.ERR_CREATE_WAIT_CROP_URI_FAILED))
                }
            }
        }
    }

    private fun addContact() {
        //check
        val name = binding.editOrAddContactInputName.text?.toString()
        if (name.isNullOrBlank()) {
            PRToast.show(requireContext(), R.string.fill_contact_name.string(requireContext()))
            return
        }
        val displayPhoneNum = binding.editOrAddContactInputPhone.text?.toString()
        val phoneNumber = displayPhoneNum?.pickPhoneNum
        if (phoneNumber.isNullOrBlank()) {
            PRToast.show(requireContext(), R.string.fill_contact_phone.string(requireContext()))
            return
        }

        val addResult = viewModel.addContact(
            requireContext(),
            ContactsBean(phoneNumber, displayPhoneNum, name,id=contact?.id ?: 0),
            cropAvatarUri
        )
        Log.d(TAG, "addContact:phone=$phoneNumber,name=$name,avatar=$cropAvatarUri succeed=$addResult")
        if (addResult) {
            PRToast.show(
                requireContext().applicationContext,
                R.string.add_contact_succeed.string(requireContext())
            )
            finishFragment()
        } else {
            PRToast.show(
                requireContext().applicationContext,
                R.string.pick_contacts_failed.string(requireContext())
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.avatarUpdate.removeObservers(viewLifecycleOwner)
    }
}