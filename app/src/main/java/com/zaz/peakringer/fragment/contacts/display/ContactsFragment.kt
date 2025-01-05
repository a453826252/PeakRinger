package com.zaz.peakringer.fragment.contacts.display

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaz.peakringer.activity.main.MainActivity
import com.zaz.peakringer.R
import com.zaz.peakringer.activity.CommonActivity
import com.zaz.peakringer.databinding.FragmentContactsListBinding
import com.zaz.peakringer.dialog.ContactsPermissionDialog
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.peakringer.utils.isFeatureOpen
import com.zaz.peakringer.utils.startFragment
import com.zaz.support.base.BaseFragment
import com.zaz.support.base.BaseViewModel
import com.zaz.support.dialog.PRDialog
import com.zaz.support.dialog.permission.PermissionDialog
import com.zaz.support.dialog.permission.PermissionItem
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.SpUtils
import com.zaz.support.utils.color
import com.zaz.support.utils.isPermissionGranted
import com.zaz.support.utils.string

/**
 * A fragment representing a list of Items.
 */
class ContactsFragment : BaseFragment() {
    private val TAG = "ContactsFragment"
    private lateinit var binding: FragmentContactsListBinding
    private val viewModel:ContactsFragmentVM by viewModels()
    private lateinit var pickContactLauncher: ActivityResultLauncher<Void?>
    private lateinit var contactsAdapter: ContactsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentContactsListBinding.inflate(layoutInflater)
        pickContactLauncher = registerForActivityResult(ActivityResultContracts.PickContact()){
            if(it == null){
                PRToast.show(requireContext(),R.string.pick_contacts_failed.string(requireContext()))
                return@registerForActivityResult
            }
            viewModel.addContacts(requireContext(),it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactsAdapter = ContactsAdapter(this::editContact)
        binding.contactsList.layoutManager = LinearLayoutManager(requireContext())
        binding.contactsList.adapter = contactsAdapter
        viewModel.contacts.observe(viewLifecycleOwner){
            showContacts(it?: mutableListOf())
        }
        setEvent()
    }

    override fun getBaseViewModel(): BaseViewModel  = viewModel

    private fun showContacts(contacts:List<ContactsBean>){
        Log.d(TAG, "showContacts: count=${contacts.size}")
        contactsAdapter.submitData(contacts)
        if(contacts.isEmpty()){
            binding.contactsList.visibility = View.INVISIBLE
            binding.contactsEmptyView.visibility = View.VISIBLE
        }else{
            binding.contactsList.visibility = View.VISIBLE
            binding.contactsEmptyView.visibility = View.INVISIBLE
        }
    }

    private fun editContact(type:Int,contactsBean: ContactsBean){
        Log.d(TAG, "editContact: type=$type,contact=$contactsBean")
        if(type == ContactsAdapter.EDIT_TYPE_DEL){
            PRDialog.Builder()
                .setTitle(R.string.confirm.string(requireContext()))
                .setContent(R.string.del_contact.string(requireContext(),contactsBean.name,contactsBean.name))
                .setLeftBtnName(R.string.cancel.string(requireContext()))
                .setRightBtnName(R.string.confirm.string(requireContext()))
                .setRightBtnListener {
                    val delResult = viewModel.delContact(contactsBean)
                    if(!delResult){
                        PRToast.show(requireContext(),R.string.del_failed.string(requireContext()))
                    }else{
                        PRToast.show(requireContext(),R.string.del_succeed.string(requireContext()))
                        viewModel.refreshContacts()
                    }
                }
                .show(childFragmentManager)
        }else{
            startFragment(CommonActivity.FRAGMENT_TYPE_EDIT_OR_DEL_CONTACTS,Bundle().apply {
                putParcelable("contact",contactsBean)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshContacts()
        binding.toolbar.background =  if(requireContext().isFeatureOpen()){
            binding.toolbar.subtitle = ""
            ColorDrawable(com.zaz.support.R.color.main_color.color(requireContext()))
        }else{
            binding.toolbar.subtitle = requireContext().getString(R.string.feature_disabled)
            ColorDrawable(com.zaz.support.R.color.border_of_cancel_btn.color(requireContext()))
        }
    }

    private fun setEvent(){
        binding.fab.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(),view)
            with(popupMenu){
                menuInflater.inflate(R.menu.menu_add_contacts,menu)
                setOnMenuItemClickListener { menu->
                    when (menu.itemId) {
                        R.id.add_from_phonebook -> {
                            addFromPhonebook()
                            return@setOnMenuItemClickListener  true
                        }
                        R.id.add_by_hand -> {
                            addByHand()
                            return@setOnMenuItemClickListener  true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
                show()
            }
        }
    }

    private fun addFromPhonebook(){
        if(!requireContext().isPermissionGranted(android.Manifest.permission.READ_CONTACTS)){
            ContactsPermissionDialog.show(requireContext(),childFragmentManager)
        }else{
            pickContacts()
        }
    }


    private fun pickContacts(){
        Log.d(TAG, "pickContacts")
        try {
            pickContactLauncher.launch(null)
        } catch (e: Exception) {
            Log.e(TAG, "pickContacts: e=${e.message}",e )
            PRToast.show(requireContext().applicationContext,getString(R.string.open_phone_book_failed))
        }
    }

    private fun addByHand(){
        startFragment(CommonActivity.FRAGMENT_TYPE_EDIT_OR_DEL_CONTACTS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.contacts.removeObservers(viewLifecycleOwner)
    }
}