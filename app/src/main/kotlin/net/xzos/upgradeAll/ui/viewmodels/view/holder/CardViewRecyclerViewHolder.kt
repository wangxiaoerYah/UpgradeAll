package net.xzos.upgradeAll.ui.viewmodels.view.holder

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.cardview_item.view.*

data class CardViewRecyclerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.nameTextView
    val descTextView: TextView = view.descTextView
    val api: TextView = view.apiTextView
    val endTextView: TextView = view.endTextView
    val itemCardView: CardView = view.itemCardView
    val versionCheckingBar: ProgressBar = view.statusChangingBar
    val versionCheckButton: ImageView = view.statusCheckButton
}