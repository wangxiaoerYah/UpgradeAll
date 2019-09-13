package net.xzos.upgradeAll.ui.viewmodels.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.content_list.*
import kotlinx.android.synthetic.main.fragment_app_list.*
import net.xzos.upgradeAll.R
import net.xzos.upgradeAll.database.RepoDatabase
import net.xzos.upgradeAll.json.cache.ItemCardViewExtraData
import net.xzos.upgradeAll.ui.viewmodels.adapters.AppItemAdapter
import net.xzos.upgradeAll.ui.viewmodels.view.ItemCardView
import org.litepal.LitePal

class AppListFragment : Fragment() {
    private val itemCardViewList = ArrayList<ItemCardView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener { this.refreshCardView() }
        refreshCardView()
    }

    private fun refreshCardView() {
        swipeRefresh.isRefreshing = true
        refreshAppList()
        swipeRefresh.isRefreshing = false
    }

    private fun refreshAppList() {
        val repoDatabase = LitePal.findAll(RepoDatabase::class.java)
        itemCardViewList.clear()
        for (updateItem in repoDatabase) {
            val databaseId = updateItem.id
            val name = updateItem.name
            val api = updateItem.api
            val url = updateItem.url
            itemCardViewList.add(ItemCardView(name, url, api, ItemCardViewExtraData(databaseId = databaseId)))
        }
        if (itemCardViewList.size != 0) {
            itemCardViewList.add(ItemCardView(null, null, null, ItemCardViewExtraData(isEmpty = true)))
            guidelinesTextView.visibility = View.GONE
            setRecyclerView()
        } else {
            guidelinesTextView.visibility = View.VISIBLE
        }
    }

    private fun setRecyclerView() {
        val layoutManager = GridLayoutManager(activity, 1)
        cardItemRecyclerView.layoutManager = layoutManager
        val adapter = AppItemAdapter(itemCardViewList)
        cardItemRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}