package cc.aoeiuv020.panovel.api.site

import cc.aoeiuv020.base.jar.compilePattern
import cc.aoeiuv020.base.jar.pick
import cc.aoeiuv020.panovel.api.base.DslJsoupNovelContext

/**
 * Created by AoEiuV020 on 2018.06.02-20:00:14.
 */
class Fenghuaju : DslJsoupNovelContext() {init {
    site {
        name = "风华居"
        baseUrl = "http://www.fenghuaju.cc"
        logo = "https://imgsa.baidu.com/forum/w%3D580/sign=ebd2f12bdd58ccbf1bbcb53229d9bcd4/670693fa513d269701e250c459fbb2fb4216d865.jpg"
    }
    search {
        get {
            charset = "GBK"
            url = "/search.php"
            data {
                "searchkey" to it
                // 加上&page=1可以避开搜索时间间隔的限制，
                // 也可以通过不加载cookies避开搜索时间间隔的限制，
                "page" to "1"
            }
        }
        document {
            items("#main > table > tbody > tr:not(:nth-child(1)):not(:nth-last-child(1))") {
                name("td:nth-child(1) > a")
                author("td:nth-child(1)") {
                    it.childNode(2).text().pick(" / (.*)").first()
                }
            }
        }
    }
    bookIdRegex = compilePattern("/(\\d+_\\d+)")
    detailPageTemplate = "/%s/"
    detail {
        document {
            novel {
                name("#info > h1")
                author("#info > p:nth-child(2)", block = pickString("作    者：(\\S*)"))
            }
            image("#fmimg > img")
            update("#info > p:nth-child(4)", format = "yyyy/MM/dd HH:mm:ss", block = pickString("最后更新：(.*)"))
            introduction("#intro > p:not(:nth-last-child(1))")
        }
    }
    chapters {
        document {
            items("#list > dl > dd > a")
            lastUpdate("#info > p:nth-child(4)", format = "yyyy/MM/dd HH:mm:ss", block = pickString("最后更新：(.*)"))
        }
    }
    chapterIdRegex = compilePattern("/(\\d+_\\d+/\\d+)")
    contentPageTemplate = "/%s.html"
    content {
        document {
            items("#content")
        }
    }
}
}

