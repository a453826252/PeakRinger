package com.zaz.peakringer

object Constant {
    object H5{
        val PRIVACY_PROTOCOL = "https://www.peakringer.com/h5/privacyProtocol"
    }
    object ErrorCode{
        //获取拍摄文件存储路径Uri时出错
        const val ERR_CREATE_WAIT_CROP_URI_FAILED = -1
        const val ERR_INSTALL_NEW_VER = -2
    }
    object NotificationId{
        const val APP_UPDATE = 1
    }
    object NotificationChannelId{
        const val APP_UPDATE = "app_update"
    }

    object SpName{
        const val PR_CONFIG = "pr_config"
    }

    object SpKey {
        const val INIT_VERSION = "init_version"
    }
}