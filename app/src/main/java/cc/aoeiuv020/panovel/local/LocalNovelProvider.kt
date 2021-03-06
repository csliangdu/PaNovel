package cc.aoeiuv020.panovel.local

import cc.aoeiuv020.base.jar.interrupt
import cc.aoeiuv020.panovel.api.NovelChapter
import cc.aoeiuv020.panovel.data.NovelProvider
import cc.aoeiuv020.panovel.data.entity.Novel
import java.io.File
import java.net.URL
import java.util.*

/**
 * Created by AoEiuV020 on 2018.06.13-15:38:49.
 */
class LocalNovelProvider(
        private val novel: Novel
) : NovelProvider {
    // detail直接保存文件全路径，/x/y/z
    private val file = File(novel.detail)
    private val type = LocalNovelType.values()
            .firstOrNull { it.suffix == novel.site }
            ?: interrupt("本地小说类型<${novel.site}>不支持")
    private val parser = when (type) {
        LocalNovelType.TEXT -> TextParser(file, charset(novel.nChapters))
        LocalNovelType.EPUB -> EpubParser(file, charset(novel.nChapters))
    }

    override fun requestNovelChapters(): List<NovelChapter> {
        val info = parser.parse()
        update(novel, info)
        return info.chapters.map {
            NovelChapter(name = it.name, extra = it.extra)
        }
    }

    override fun getNovelContent(chapter: NovelChapter, listener: ((Long, Long) -> Unit)?): List<String> {
        // epub章节内容开头可能是章节名，过滤掉不要，
        // 按理说是内容比章节名更适合保留着，但是不方便统一处理，
        return parser.getNovelContent(LocalNovelChapter(name = chapter.name, extra = chapter.extra)).dropWhile { it == chapter.name }
    }

    override fun getContentUrl(chapter: NovelChapter): String {
        return getDetailUrl()
    }

    override fun getDetailUrl(): String {
        return file.toURI().toString()
    }

    override fun getImage(extra: String): URL {
        return parser.getImage(extra)
    }

    override fun updateNovelDetail() {
        // 不真的刷新什么，但是以防万一，chapters为null会反复调用这个方法，
        if (novel.chapters == null) {
            // 按理说没用，编码是一开始就决定好了写死的，不会为空，
            novel.chapters = "null"
        }
    }

    override fun cleanData() {
        file.delete()
    }

    override fun cleanCache() {
        // 没什么缓存文件可以删除的，
        // 复制到内部的备份不方便删除，
    }

    companion object {
        // 因为要在导入时和刷新章节列表时调用，所以写在伴生对象里，
        fun update(novel: Novel, info: LocalNovelInfo) {
            novel.apply {
                checkUpdateTime = Date()
                if (info.chapters.size > chaptersCount) {
                    // 本地导入小说按导入时间存一个receiveUpdateTime, 方便排序时算上，
                    receiveUpdateTime = checkUpdateTime
                }
            }
            val list = info.chapters
            novel.apply {
                chaptersCount = list.size
                if (readAtChapterIndex == 0) {
                    // 阅读至第一章代表没阅读过，保存第一章的章节名，
                    readAtChapterName = list.firstOrNull()?.name ?: "(null)"
                }
                lastChapterName = list.lastOrNull()?.name ?: "(null)"
            }
        }
    }
}