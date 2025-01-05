package com.zaz.peakringer.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.FragmentManager
import com.zaz.peakringer.R
import com.zaz.support.dialog.PRDialog
import com.zaz.support.dialog.permission.PermissionDialog
import com.zaz.support.dialog.permission.PermissionItem
import com.zaz.support.utils.goToAppDetails
import com.zaz.support.utils.string

class ContactsPermissionDialog: PermissionDialog() {
    companion object{
        fun show(context: Context,fm:FragmentManager){
            val dialog = ContactsPermissionDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("permissions", ArrayList<PermissionItem>().apply {
                        add(
                            PermissionItem(
                                android.Manifest.permission.READ_CONTACTS,
                                R.string.read_contacts.string(context),
                                R.mipmap.ic_phonebook
                            )
                        )
                    })
                }
            }
            dialog.show(fm, TAG)
        }
        fun authorizeContactsPermission(context: Context,fragmentManager: FragmentManager){
            with(context){
                PRDialog.Builder()
                    .setTitle(getString(R.string.important))
                    .setContent(getString(R.string.above_11_need_contacts_permission))
                    .setContentGravity(Gravity.LEFT)
                    .setLeftBtnName(getString(R.string.cancel))
                    .setLeftBtnListener {
                        it.dismissAllowingStateLoss()
                    }
                    .setRightBtnName(getString(com.zaz.support.R.string.authorize))
                    .setRightBtnListener {
                        it.dismissAllowingStateLoss()
                        goToAppDetails()
                    }
                    .show(fragmentManager)
            }
        }
    }

    override fun onAuthorizeBtnClick(permissionItem: PermissionItem) {
        authorizeContactsPermission(requireContext(),childFragmentManager)
    }
}