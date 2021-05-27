package com.qltech.whatsweb.util

object UserAgentConstants {

    object OS {
        const val MAC = "MacOS"
        const val WINDOW = "Windows"
    }

    object Brower {
        const val IE = "IE"
        const val CHROME = "Chrome"//89.0.4389.105
        const val FIREFOX = "Firefox"
        const val SAFARI = "Safari"//14.0.3 (16610.4.3.1.4)
        const val OPERA = "Opera"//75.0.3969.171
    }

    object UserAngentOS {
        const val MAC = "(Macintosh; Intel Mac OS X 11_2_0)"
        const val WINDOW = "(Windows NT 10.0; Win64; x64)"
        const val LINUX = "(Linux;)"
    }

    object UserAngent {
        const val DEFAULT="Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_0) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/89.0.4389.105 Safari/16610.4.3.1.4"
        const val MAC_SAFARI = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_0) AppleWebKit/537.36 (KHTML, like Gecko) Version/14.0.3 Safari/16610.4.3.1.4"
        const val MAC_CHROME = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/16610.4.3.1.4"
        const val MAC_IE = ""
        const val MAC_OPERA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36 OPR/75.0.3969.171"
        const val WINDOW_SAFARI = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Version/14.0.3 Safari/16610.4.3.1.4"
        const val WINDOW_CHROME = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/16610.4.3.1.4"
        const val WINDOW_IE = ""
        const val WINDOW_OPERA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/16610.4.3.1.4 OPR/75.0.3969.171"
    }

}
