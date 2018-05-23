package cc.aoeiuv020.panovel.search

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import cc.aoeiuv020.panovel.App
import cc.aoeiuv020.panovel.R
import cc.aoeiuv020.panovel.api.NovelItem
import cc.aoeiuv020.panovel.api.NovelSite
import cc.aoeiuv020.panovel.base.item.BaseItemListView
import cc.aoeiuv020.panovel.base.item.DefaultItemListAdapter
import cc.aoeiuv020.panovel.local.NovelHistory
import cc.aoeiuv020.panovel.local.Settings
import cc.aoeiuv020.panovel.local.toBean
import cc.aoeiuv020.panovel.local.toJson
import cc.aoeiuv020.panovel.util.getStringExtra
import cc.aoeiuv020.panovel.util.show
import com.google.android.gms.ads.AdListener
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_fuzzy_search.*
import kotlinx.android.synthetic.main.novel_item_list.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity


class FuzzySearchActivity : AppCompatActivity(), BaseItemListView, AnkoLogger {
    companion object {
        fun start(context: Context) {
            context.startActivity<FuzzySearchActivity>()
        }

        fun start(context: Context, novelItem: NovelItem) {
            // 精确搜索，refine search,
            start(context, novelItem.name, novelItem.author)
        }

        fun start(context: Context, name: String) {
            // 模糊搜索，fuzzy search,
            context.startActivity<FuzzySearchActivity>("name" to name)
        }

        fun start(context: Context, name: String, author: String) {
            // 精确搜索，refine search,
            context.startActivity<FuzzySearchActivity>("name" to name, "author" to author)
        }

        fun start(ctx: Context, site: NovelSite) {
            ctx.startActivity<FuzzySearchActivity>("site" to site.toJson())
        }

        fun start(ctx: Context, site: NovelSite, name: String) {
            ctx.startActivity<FuzzySearchActivity>("site" to site.toJson(), "name" to name)
        }
    }

    private lateinit var presenter: FuzzySearchPresenter
    private lateinit var mAdapter: DefaultItemListAdapter
    private var name: String? = null
    private var author: String? = null
    private var site: NovelSite? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fuzzy_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.hideKeyboard(searchView)
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        rvNovel.setLayoutManager(LinearLayoutManager(this))
        presenter = FuzzySearchPresenter()
        presenter.attach(this)
        mAdapter = DefaultItemListAdapter(this, presenter)
        rvNovel.setAdapter(mAdapter)
        rvNovel.setRefreshAction {
            forceRefresh()
        }

        name = getStringExtra("name", savedInstanceState)
        author = getStringExtra("author", savedInstanceState)
        site = getStringExtra("site", savedInstanceState)?.toBean()

        site?.let {
            presenter.singleSite(it)
        }

        name?.let { nameNonnull ->
            search(nameNonnull, author)
        } ?: searchView.post { showSearch() }

        ad_view.adListener = object : AdListener() {
            override fun onAdLoaded() {
                ad_view.show()
            }
        }

        if (Settings.adEnabled) {
            ad_view.loadAd(App.adRequest)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("name", name)
        outState.putString("author", author)
    }

    private fun showSearch() {
        searchView.showSearch()
        searchView.setQuery(presenter.name, false)
    }

    override fun onPause() {
        ad_view.pause()
        super.onPause()
    }

    override fun onRestart() {
        super.onRestart()
        refresh()
    }

    override fun onResume() {
        super.onResume()
        ad_view.resume()
    }

    override fun onDestroy() {
        presenter.detach()
        ad_view.destroy()
        super.onDestroy()
    }

    private fun search(name: String, author: String? = null) {
        title = name
        this.name = name
        this.author = author
        mAdapter.clear()
        mAdapter.openLoadMore()
        presenter.search(name, author)
    }

    private fun refresh() {
        mAdapter.notifyDataSetChanged()
    }

    /**
     * 刷新列表，同时刷新小说章节信息，
     */
    private fun forceRefresh() {
        mAdapter.clear()
        mAdapter.openLoadMore()
        presenter.forceRefresh()
        rvNovel.dismissSwipeRefresh()
    }

    fun addNovel(item: NovelItem) {
        // 插入有时会导致下滑，原因不明，保存状态解决，
        val state = rvNovel.recyclerView.layoutManager.onSaveInstanceState()
        mAdapter.add(NovelHistory(item))
        rvNovel.recyclerView.layoutManager.onRestoreInstanceState(state)
    }

    fun showOnComplete() {
        rvNovel.dismissSwipeRefresh()
        rvNovel.showNoMore()
    }

    private val snack: Snackbar by lazy {
        Snackbar.make(rvNovel, "", Snackbar.LENGTH_SHORT)
    }

    override fun showError(message: String, e: Throwable) {
        snack.setText(message + e.message)
        snack.show()
        rvNovel.dismissSwipeRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_fuzzy_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> searchView.showSearch()
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
