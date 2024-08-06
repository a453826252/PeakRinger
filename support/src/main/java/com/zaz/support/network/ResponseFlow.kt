package com.zaz.support.network

import kotlinx.coroutines.flow.Flow


interface ResponseFlow<T>: Flow<Response<T>>