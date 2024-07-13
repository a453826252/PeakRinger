package com.zaz.support.dialog.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zaz.support.R
import com.zaz.support.databinding.DialogPermissionBinding
import com.zaz.support.dialog.PRDialog
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.string

class PermissionDialog private constructor(): BottomSheetDialogFragment() {
    private lateinit var viewBinding: DialogPermissionBinding
    private lateinit var permissionAdapter:PermissionListAdapter
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var requestPermissionCallBack:((PermissionDialog,Map<String,Boolean>?)->Unit)?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ result->
           requestPermissionCallBack?.invoke(this@PermissionDialog,result)
            checkIfGranted()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DialogPermissionBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permissions = arguments?.getParcelableArrayList<PermissionItem>("permissions")
        checkPermissionArgument(permissions)
        permissionAdapter = PermissionListAdapter(permissions!!,::onAuthorizeBtnClick)
        viewBinding.permissionList.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.permissionList.adapter = permissionAdapter
        viewBinding.dialogPermissionCancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        checkIfGranted()
    }

    private fun checkIfGranted(){
        val permissions = permissionAdapter.getData()
        val noPermissionItems = permissions.filter {
            it.granted = ActivityCompat.checkSelfPermission(requireContext(),it.permission) == PackageManager.PERMISSION_GRANTED
            !it.granted
        }
        if(noPermissionItems.isEmpty()){
            dismiss()
        }else{
            permissionAdapter.submitData(noPermissionItems)
        }
    }

    private fun onAuthorizeBtnClick(permissionItem: PermissionItem){
        requestPermissionLauncher.launch(arrayOf( permissionItem.permission))
    }

    private fun checkPermissionArgument(permissions:List<PermissionItem>?){
        if(permissions.isNullOrEmpty()){
            throw IllegalArgumentException("$TAG:permission array is null or empty")
        }

        for (per in permissions){
            if(per.permission.isBlank() || per.title.isBlank()){
                throw IllegalArgumentException("$TAG: permission is empty,permission=${per.permission},title=${per.title}")
            }
            if(per.icon == 0){
                throw IllegalArgumentException("$TAG: permission iconRes is empty,title=${per.title}")
            }
            per.granted=ActivityCompat.checkSelfPermission(requireContext(),per.permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object{
        const val TAG = "PermissionDialog"
        @JvmStatic
        fun show(fm:FragmentManager,permissions:ArrayList<PermissionItem>,requestPermissionCallBack:(PermissionDialog,Map<String,Boolean>?)->Unit): PermissionDialog {
            val dialog = PermissionDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("permissions",permissions)
                }
            }
            dialog.requestPermissionCallBack = requestPermissionCallBack
            fm.commit { add(dialog, TAG) }
            return dialog
        }
        @JvmStatic
        fun checkAndShow(activity: Activity, fm: FragmentManager, permissionItem: PermissionItem, hasPermissionCallback:()->Unit){
            if(ActivityCompat.checkSelfPermission(activity, permissionItem.permission) != PackageManager.PERMISSION_GRANTED){
                // no permission,request
                show(fm, ArrayList<PermissionItem>().apply {
                    add(permissionItem)
                }){ dialog,permissions->
                    permissions?.forEach { (permission, granted) ->
                        Log.d(TAG, "request permission result,permission=$permission,granted=$granted")
                    }
                    permissions?.let {
                        if(it[permissionItem.permission] == true){
                            Log.d(TAG, "checkCameraPermission: grant ${permissionItem.permission} permission")
                            hasPermissionCallback()
                        }else{
                            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionItem.permission)){
                                PRDialog.Builder()
                                    .setTitle(permissionItem.title)
                                    .setContent(R.string.request_permission_failed.string(activity,permissionItem.title,-1))
                                    .setLeftBtnName(R.string.cancel.string(activity))
                                    .setRightBtnName(com.zaz.support.R.string.authorize.string(activity))
                                    .setHideNotShowBtn(true)
                                    .setRightBtnListener {
                                        activity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = Uri.fromParts("package",activity.packageName,null) })
                                    }
                                    .show(fm)
                            }else{
                                PRToast.show(activity,R.string.request_permission_failed.string(activity,permissionItem.title,-2))
                            }
                            dialog.dismiss()
                        }
                    }?: PRToast.show(activity,R.string.request_permission_failed.string(activity,permissionItem.title,-3))
                }
            }else{
                // have permission
                hasPermissionCallback()
            }
        }
    }
}