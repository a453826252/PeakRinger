package com.zaz.peakringer.fragment.contacts

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zaz.peakringer.CallScreenRoleManager
import com.zaz.peakringer.R
import com.zaz.peakringer.databinding.FragmentContactsListBinding

/**
 * A fragment representing a list of Items.
 */
class ContactsFragment : Fragment() {
    private lateinit var binding: FragmentContactsListBinding
    private val viewModel: ContactsFragmentVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentContactsListBinding.inflate(layoutInflater)
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
        setEvent()
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
            TODO("Not yet implemented")
        }else{
            // have permission,pick contacts
            TODO("Not yet implemented")
        }
    }

    private fun addByHand(){
        TODO("Not yet implemented")
    }

}