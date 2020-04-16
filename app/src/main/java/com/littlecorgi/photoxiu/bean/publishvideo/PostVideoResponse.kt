package com.littlecorgi.photoxiu.bean.publishvideo

/**
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-09 22:40
 */
class PostVideoResponse {
    /**
     * status : 0
     * data : {"uri":"708c3f48-cb6c-448d-8909-253665ab17b0.png","url":"ftp://www.zhangshuo.fun/708c3f48-cb6c-448d-8909-253665ab17b0.png"}
     */
    var status = 0
    var data: DataBean? = null
    var msg: String? = null

    class DataBean {
        /**
         * uri : 708c3f48-cb6c-448d-8909-253665ab17b0.png
         * url : ftp://www.zhangshuo.fun/708c3f48-cb6c-448d-8909-253665ab17b0.png
         */
        var uri: String? = null
        var url: String? = null

    }
}