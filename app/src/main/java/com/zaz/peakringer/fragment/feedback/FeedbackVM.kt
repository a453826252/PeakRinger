package com.zaz.peakringer.fragment.feedback

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
    fun getFeedbackType(){
        _showLoading.postValue(PRApp.application.getString(com.zaz.support.R.string.loading))
        viewModelScope.launch {
            httpApi.getFeedbackType().collect{
                _showLoading.postValue(null)
                if(it.isSuccessful() && it.data?.isNotEmpty() == true){
                    _showFeedbackType.postValue(it.data)
                }else{
                    _toast.postValue(PRApp.application.getString(R.string.loading_failed_retry))
                }
            }
        }
    }

    fun submit(type:Int,content:String,contactInfo:String?){
        _showLoading.postValue(PRApp.application.getString(R.string.submitting))
        viewModelScope.launch {
            httpApi.postFeedback(type,content,contactInfo).collect{
                _showLoading.postValue(null)
                _toast.postValue(if(it.isSuccessful()) PRApp.application.getString(R.string.submit_success) else PRApp.application.getString(R.string.submit_fail))
            }
        }
    }
}