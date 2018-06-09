package cc.aoeiuv020.panovel.api.site

import org.junit.Test

/**
 * Created by AoEiuV020 on 2018.06.09-19:48:19.
 */
class ZhuajiTest : BaseNovelContextText(Zhuaji::class) {
    @Test
    fun search() {
        search("都市")
        search("麻衣神算子", "骑马钓鱼", "2471")
        search("劫天运", "浮梦流年", "2294")
    }

    @Test
    fun detail() {
        detail("2471", "2471", "麻衣神算子", "骑马钓鱼",
                "http://www.zhuaji.org/files/article/image/2/2471/2471s.jpg",
                "爷爷教了我一身算命的本事，却在我帮人算了三次命后，离开了我。\n" +
                        "从此之后，我不光给活人看命，还要给死人看，更要给……\n" +
                        "更新：每天中午12点之前一更，下午四点之前一更，加更的话，都在晚上",
                "2017-04-20 00:00:00")
        detail("2294", "2294", "劫天运", "浮梦流年",
                "http://www.zhuaji.org/files/article/image/2/2294/2294s.jpg",
                "本书原名《养鬼为祸》\n" +
                        "我从出生前就给人算计了，五阴俱全，天生招厉鬼，懂行的先生说我活不过七岁，死后是要给人养成血衣小鬼害人的。\n" +
                        "外婆为了救我，给我娶了童养媳，让我过起了安生日子，虽然后来我发现媳妇姐姐不是人……\n" +
                        "从小苟延馋喘的我能活到现在，本已习惯逆来顺受，可唯独外婆被人害死了这件事。\n" +
                        "为此，我不顾因果报应，继承了外婆养鬼的职业，发誓要把害死她的人全都送下地狱。",
                "2018-06-09 00:00:00")
    }

    @Test
    fun chapters() {
        chapters("2471", "第001章 看相", "2471/913859", null,
                "番外（3）", "2471/4984164", null,
                1727)
        chapters("2294", "第一章 算计", "2294/843846", null,
                "第三千七百七十四章 八剑", "2294/14740604", null,
                3775)
    }

    @Test
    fun content() {
        content("2471/913859",
                "我叫李初一，今年二十岁整，跟爷爷相依为命，" +
                        "目前在北方一个小县城经营一家花圈寿衣店，" +
                        "我们店的门脸是自己的房子，一栋两层的小楼，一楼有我们的住房，" +
                        "还有我们那家寿衣店的门脸，二楼是往外租的房子，有四家租户。",
                "所以在去之前，我还要好好地打扮一下，把我最好的一面展露在小花和她母亲的面前，" +
                        "当然我还要先去县城的商城里，给小花和她的家人挑选一些拿得出手的礼物。",
                43)
        content("2294/14740604",
                "“未必？你不是说你快要死了，让我们看你坐化登仙么？”修鱼兰玉一脸的诧异，目光带着审视态度",
                "请记住本书首发域名：.。手机版阅读网址：m.",
                45)
    }

}