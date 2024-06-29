package com.zaz.support.dialog.permission

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zaz.support.databinding.DialogPermissionBinding

class PermissionDialog private constructor(): BottomSheetDialogFragment() {
    private lateinit var viewBinding: DialogPermissionBinding
    private lateinit var permissionAdapter:PermissionListAdapter
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var requestPermissionCallBack:((Map<String,Boolean>?)->Unit)?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ result->
           requestPermissionCallBack?.invoke(result)
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
    }

    override fun onResume() {
        super.onResume()
        checkIfGranted()
    }

    private fun checkIfGranted(){
        val permissions = permissionAdapter.data
        for (per in permissions){
            per.granted = ActivityCompat.checkSelfPermission(requireContext(),per.permission) == PackageManager.PERMISSION_GRANTED
        }
        permissionAdapter.submitData(permissions)
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
        fun show(fm:FragmentManager,permissions:ArrayList<PermissionItem>,requestPermissionCallBack:(Map<String,Boolean>?)->Unit): PermissionDialog {
            val dialog = PermissionDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("permissions",permissions)
                }
            }
            dialog.requestPermissionCallBack = requestPermissionCallBack
            fm.commit { add(dialog, TAG) }
            return dialog
        }
    }
}