package com.zaz.peakringer.fragment.feedback

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.zaz.peakringer.R
import com.zaz.peakringer.bean.FeedbackTypeBean
import com.zaz.peakringer.databinding.FragmentFeedbackBinding
import com.zaz.support.base.BaseFragment
import com.zaz.support.base.BaseViewModel
import com.zaz.support.dialog.bottom.BottomItemDialog
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.finishFragment

class FeedbackFragment private constructor(): BaseFragment(),View.OnClickListener {
    private lateinit var binding:FragmentFeedbackBinding
    private val viewModel:FeedbackVM by viewModels()
    private lateinit var feedbackTypeDialog:BottomItemDialog<FeedbackTypeBean>
    companion object{
        private const val TAG = "FeedbackFragment"
        fun show(fm:FragmentManager,container:Int):FeedbackFragment{
            val fragment = FeedbackFragment()
            fm.commit {
                addToBackStack(null)
                add(container,fragment,"FeedbackFragment")
            }
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedbackBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showFeedbackType.observe(viewLifecycleOwner){
            showFeedbackType(it)
        }
        binding.feedbackType.setOnClickListener(this)
        binding.feedbackSubmit.setOnClickListener(this)
        binding.feedbackTitle.setNavigationOnClickListener {
            finishFragment()
        }
    }

    override fun getBaseViewModel(): BaseViewModel = viewModel

    override fun onClick(v: View) {
        when(v.id){
            R.id.feedback_type->{
                viewModel.getFeedbackType()
            }
            R.id.feedback_submit->{
                submit()
            }
        }
    }

    private fun submit(){
        if(binding.feedbackTypeContent.tag == null){
            binding.feedbackType.error = getString(R.string.select_feedback_type)
            PRToast.show(requireContext(),getString(R.string.select_feedback_type))
            return
        }

        val feedbackContent = binding.feedbackContent.text?.toString() ?: ""
        if(feedbackContent.isEmpty()){
           binding.feedbackContentLy.error = getString(R.string.input_feedback_content)
            PRToast.show(requireContext(),getString(R.string.input_feedback_content))
        }else if(feedbackContent.length > 500){
            binding.feedbackContentLy.error = getString(R.string.input_feedback_content_max)
            PRToast.show(requireContext(),getString(R.string.input_feedback_content_max))
        }else{
            val type = (binding.feedbackTypeContent.tag as FeedbackTypeBean).id
            val contactInfo = binding.feedbackContactInfo.text?.toString() ?: ""
            viewModel.submit(type,feedbackContent,contactInfo)
        }
    }
    private fun showFeedbackType(types:List<FeedbackTypeBean>){
        Log.d(TAG, "showFeedbackType: size=${types.size}")
        feedbackTypeDialog = BottomItemDialog.show(childFragmentManager,types){
            binding.feedbackTypeContent.setText(it.showTxt)
            binding.feedbackTypeContent.tag = it
        }
    }
}