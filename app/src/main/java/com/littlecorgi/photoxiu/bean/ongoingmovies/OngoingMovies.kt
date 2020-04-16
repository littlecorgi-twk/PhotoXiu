package com.littlecorgi.photoxiu.bean.ongoingmovies

import com.google.gson.annotations.SerializedName

/**
 * 正在上映的电影
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-08 21:44
 */
class OngoingMovies {
    /**
     * bImg : http://img5.mtime.cn/mg/2018/09/04/124630.14485487.jpg
     * date : 2020-02-03
     * hasPromo : false
     * lid : 290
     * ms : [{"NearestCinemaCount":1,"NearestDay":1580630400,"NearestShowtimeCount":1,"aN1":"巩俐","aN2":"黄渤","actors":"巩俐 / 黄渤 / 吴刚 / 彭昱畅","cC":1,"commonSpecial":"中国女排拼搏奋进的热血历程","d":"135","dN":"陈可辛","def":0,"id":254336,"img":"http://img5.mtime.cn/mt/2020/01/19/095651.33942967_1280X720X2.jpg","is3D":false,"isDMAX":true,"isFilter":false,"isHasTrailer":true,"isHot":false,"isIMAX":true,"isIMAX3D":false,"isNew":false,"isTicket":true,"m":"","movieId":254336,"movieType":"剧情 / 运动","p":["剧情运动"],"preferentialFlag":false,"r":-1,"rc":0,"rd":"0","rsC":0,"sC":0,"t":"夺冠","tCn":"夺冠","tEn":"Duo Guan","ua":-1,"versions":[{"enum":1,"version":"2D"},{"enum":3,"version":"IMAX"},{"enum":6,"version":"中国巨幕"}],"wantedCount":1636,"year":"2020"},{"NearestCinemaCount":1,"NearestDay":1580630400,"NearestShowtimeCount":1,"aN1":"徐峥","aN2":"黄梅莹","actors":"徐峥 / 黄梅莹 / 袁泉 / 贾冰","cC":1,"commonSpecial":"徐峥与母亲的俄罗斯囧途","d":"126","dN":"徐峥","def":0,"id":262895,"img":"http://img5.mtime.cn/mt/2020/01/13/144906.94615802_1280X720X2.jpg","is3D":false,"isDMAX":true,"isFilter":false,"isHasTrailer":true,"isHot":false,"isIMAX":true,"isIMAX3D":false,"isNew":false,"isTicket":true,"m":"","movieId":262895,"movieType":"剧情 / 喜剧","p":["剧情喜剧"],"preferentialFlag":false,"r":6.3,"rc":0,"rd":"20200125","rsC":0,"sC":0,"t":"囧妈","tCn":"囧妈","tEn":"Lost In Russia","ua":-1,"versions":[{"enum":1,"version":"2D"},{"enum":3,"version":"IMAX"},{"enum":6,"version":"中国巨幕"}],"wantedCount":1937,"year":"2020"},{"NearestCinemaCount":1,"NearestDay":1580630400,"NearestShowtimeCount":1,"aN1":"王宝强","aN2":"刘昊然","actors":"王宝强 / 刘昊然 / 妻夫木聪 / 托尼·贾","cC":1,"commonSpecial":"王宝强刘昊然空降东京破案","d":"136","dN":"陈思诚","def":0,"id":254785,"img":"http://img5.mtime.cn/mt/2019/12/18/101618.39683289_1280X720X2.jpg","is3D":false,"isDMAX":true,"isFilter":false,"isHasTrailer":true,"isHot":false,"isIMAX":true,"isIMAX3D":false,"isNew":false,"isTicket":true,"m":"","movieId":254785,"movieType":"喜剧 / 悬疑","p":["喜剧悬疑"],"preferentialFlag":false,"r":-1,"rc":0,"rd":"0","rsC":0,"sC":0,"t":"唐人街探案3","tCn":"唐人街探案3","tEn":"Detective Chinatown 3","ua":-1,"versions":[{"enum":1,"version":"2D"},{"enum":3,"version":"IMAX"},{"enum":6,"version":"中国巨幕"}],"wantedCount":3469,"year":"2020"}]
     * newActivitiesTime : 0
     * promo : {}
     * totalComingMovie : 31
     * voucherMsg :
     */
    var bImg: String? = null
    var date: String? = null
    var isHasPromo = false
    var lid = 0
    var newActivitiesTime = 0
    var promo: PromoBean? = null
    var totalComingMovie = 0
    var voucherMsg: String? = null
    var ms: List<MsBean>? = null

    class PromoBean
    class MsBean {
        /**
         * NearestCinemaCount : 1
         * NearestDay : 1580630400
         * NearestShowtimeCount : 1
         * aN1 : 巩俐
         * aN2 : 黄渤
         * actors : 巩俐 / 黄渤 / 吴刚 / 彭昱畅
         * cC : 1
         * commonSpecial : 中国女排拼搏奋进的热血历程
         * d : 135
         * dN : 陈可辛
         * def : 0
         * id : 254336
         * img : http://img5.mtime.cn/mt/2020/01/19/095651.33942967_1280X720X2.jpg
         * is3D : false
         * isDMAX : true
         * isFilter : false
         * isHasTrailer : true
         * isHot : false
         * isIMAX : true
         * isIMAX3D : false
         * isNew : false
         * isTicket : true
         * m :
         * movieId : 254336
         * movieType : 剧情 / 运动
         * p : ["剧情运动"]
         * preferentialFlag : false
         * r : -1
         * rc : 0
         * rd : 0
         * rsC : 0
         * sC : 0
         * t : 夺冠
         * tCn : 夺冠
         * tEn : Duo Guan
         * ua : -1
         * versions : [{"enum":1,"version":"2D"},{"enum":3,"version":"IMAX"},{"enum":6,"version":"中国巨幕"}]
         * wantedCount : 1636
         * year : 2020
         */
        var nearestCinemaCount = 0
        var nearestDay = 0
        var nearestShowtimeCount = 0
        var aN1: String? = null
        var aN2: String? = null
        var actors: String? = null
        var cC = 0
        var commonSpecial: String? = null
        var d: String? = null
        var dN: String? = null
        var def = 0
        var id = 0
        var img: String? = null
        var isIs3D = false
            private set
        var isIsDMAX = false
            private set
        var isIsFilter = false
            private set
        var isIsHasTrailer = false
            private set
        var isIsHot = false
            private set
        var isIsIMAX = false
            private set
        var isIsIMAX3D = false
            private set
        var isIsNew = false
            private set
        var isIsTicket = false
            private set
        var m: String? = null
        var movieId = 0
        var movieType: String? = null
        var isPreferentialFlag = false
        var r = 0f
        var rc = 0
        var rd: String? = null
        var rsC = 0
        var sC = 0
        var t: String? = null
        var tCn: String? = null
        var tEn: String? = null
        var ua = 0
        var wantedCount = 0
        var year: String? = null
        var p: List<String>? = null
        var versions: List<VersionsBean>? = null

        fun setIs3D(is3D: Boolean) {
            isIs3D = is3D
        }

        fun setIsDMAX(isDMAX: Boolean) {
            isIsDMAX = isDMAX
        }

        fun setIsFilter(isFilter: Boolean) {
            isIsFilter = isFilter
        }

        fun setIsHasTrailer(isHasTrailer: Boolean) {
            isIsHasTrailer = isHasTrailer
        }

        fun setIsHot(isHot: Boolean) {
            isIsHot = isHot
        }

        fun setIsIMAX(isIMAX: Boolean) {
            isIsIMAX = isIMAX
        }

        fun setIsIMAX3D(isIMAX3D: Boolean) {
            isIsIMAX3D = isIMAX3D
        }

        fun setIsNew(isNew: Boolean) {
            isIsNew = isNew
        }

        fun setIsTicket(isTicket: Boolean) {
            isIsTicket = isTicket
        }

        class VersionsBean {
            /**
             * enum : 1
             * version : 2D
             */
            @SerializedName("enum")
            var enumX = 0
            var version: String? = null

        }
    }
}