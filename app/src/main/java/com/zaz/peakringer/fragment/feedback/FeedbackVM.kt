package com.zaz.peakringer.fragment.feedback

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zaz.peakringer.PRApp
import com.zaz.peakringer.R
import com.zaz.peakringer.bean.FeedbackTypeBean
import com.zaz.peakringer.network.httpApi
import com.zaz.support.base.BaseViewModel
import kotlinx.coroutines.launch

class FeedbackVM: BaseViewModel() {
    protected val _showFeedbackType = MutableLiveData<List<FeedbackTypeBean>>()
    val showFeedbackType: LiveData<List<FeedbackTypeBean>> = _showFeedbackType
    fun getFeedbackType(context: Context){
        viewModelScope.launch {
            val feedbackTypes = mutableListOf<FeedbackTypeBean>()
            feedbackTypes.add(FeedbackTypeBean(FeedbackTypeBean.TYPE_SUGGESTION,context.getString(R.string.suggestion)))
            feedbackTypes.add(FeedbackTypeBean(FeedbackTypeBean.TYPE_ISSUE,context.getString(R.string.issue)))
            _showFeedbackType.postValue(feedbackTypes)
        }
    }

    fun submit(type:Int,content:String,contactInfo:String?,finishCallback:()->Unit){
        _showLoading.postValue(PRApp.application.getString(R.string.submitting))
        viewModelScope.launch {
            httpApi.postFeedback(type,content,contactInfo).collect{
                _showLoading.postValue(null)
                _toast.postValue(if(it.isSuccessful()) PRApp.application.getString(R.string.submit_success) else PRApp.application.getString(R.string.submit_fail))
                finishCallback()
            }
        }
    }
}