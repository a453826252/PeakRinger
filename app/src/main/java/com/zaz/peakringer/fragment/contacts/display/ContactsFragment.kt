package com.zaz.peakringer.fragment.contacts.display

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
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaz.peakringer.CallScreenRoleManager
import com.zaz.peakringer.MainActivity
import com.zaz.peakringer.R
import com.zaz.peakringer.databinding.FragmentContactsListBinding
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.support.base.BaseFragment
import com.zaz.support.dialog.PRDialog
import com.zaz.support.dialog.permission.PermissionDialog
import com.zaz.support.dialog.permission.PermissionItem
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.string

/**
 * A fragment representing a list of Items.
 */
class ContactsFragment : BaseFragment() {
    private var activity:MainActivity?=null
    private val TAG = "ContactsFragment"
    private lateinit var binding: FragmentContactsListBinding
    private val viewModel: ContactsFragmentVM by viewModels()
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CallScreenRoleManager.register(this)
        contactsAdapter = ContactsAdapter(this::editContact)
        binding.contactsList.layoutManager = LinearLayoutManager(requireContext())
        binding.contactsList.adapter = contactsAdapter
        viewModel.contacts.observe(viewLifecycleOwner){
            showContacts(it?: mutableListOf())
        }
        setEvent()
    }

    private fun showContacts(contacts:List<ContactsBean>){
        Log.d(TAG, "showContacts: count=${contacts.size}")
        contactsAdapter.submitData(contacts)
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
            activity?.showEditOrAddFragment(contactsBean)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshContacts()
    }

    private fun setEvent(){
        binding.fab.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(),view)
            with(popupMenu){
                menuInflater.inflate(R.menu.menu_add_contacts,menu)
                setOnMenuItemClickListener { menu->
                    if(menu.itemId == R.id.add_from_phonebook){
                        addFromPhonebook()
                        return@setOnMenuItemClickListener  true
                    }else if(menu.itemId == R.id.add_by_hand){
                        addByHand()
                        return@setOnMenuItemClickListener  true
                    }
                    return@setOnMenuItemClickListener false
                }
                show()
            }
        }
    }

    private fun addFromPhonebook(){
        PermissionDialog.checkAndShow(requireActivity(),childFragmentManager, PermissionItem(
            android.Manifest.permission.READ_CONTACTS,
            R.string.read_contacts.string(requireContext())
            ,R.mipmap.ic_phonebook
        )){
            pickContacts()
        }
    }


    private fun pickContacts(){
        Log.d(TAG, "pickContacts")
        pickContactLauncher.launch(null)
    }

    private fun addByHand(){
        activity?.showEditOrAddFragment(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.contacts.removeObservers(viewLifecycleOwner)
    }
}