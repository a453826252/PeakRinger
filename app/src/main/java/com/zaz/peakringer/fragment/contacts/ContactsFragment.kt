package com.zaz.peakringer.fragment.contacts

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaz.peakringer.CallScreenRoleManager
import com.zaz.peakringer.R
import com.zaz.peakringer.databinding.FragmentContactsListBinding
import com.zaz.support.dialog.permission.PermissionDialog
import com.zaz.support.dialog.permission.PermissionItem
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.string

/**
 * A fragment representing a list of Items.
 */
class ContactsFragment : Fragment() {
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CallScreenRoleManager.register(this)
        contactsAdapter = ContactsAdapter()
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

    override fun onResume() {
        super.onResume()
        viewModel.getContacts()
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
        //check permission
        if(ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            // no permission,request
            PermissionDialog.show(childFragmentManager, ArrayList<PermissionItem>().apply {
                add(PermissionItem(android.Manifest.permission.READ_CONTACTS,R.string.read_contacts.string(requireContext()),R.mipmap.ic_phonebook))
            }){
                it?.forEach { (permission, granted) ->
                    Log.d(TAG, "request permission result,permission=$permission,granted=$granted")
                }
                it?.let {
                    if(it[android.Manifest.permission.READ_CONTACTS] == true){
                        Log.d(TAG, "addFromPhonebook: grant read_contacts permission")
                        pickContacts()
                    }else{
                        PRToast.show(requireContext(),R.string.request_contacts_permission_failed.string(requireContext(),-1))
                    }
                }?:PRToast.show(requireContext(),R.string.request_contacts_permission_failed.string(requireContext(),-2))
            }
        }else{
            // have permission,pick contacts
            pickContacts()
        }
    }


    private fun pickContacts(){
        Log.d(TAG, "pickContacts")
        pickContactLauncher.launch(null)
    }

    private fun addByHand(){
        TODO("Not yet implemented")
    }

}