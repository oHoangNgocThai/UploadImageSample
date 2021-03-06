package android.thaihn.uploadimagesample.ui.url

import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.base.common.DataBoundAdapter
import android.thaihn.uploadimagesample.base.extension.invisible
import android.thaihn.uploadimagesample.base.extension.visible
import android.thaihn.uploadimagesample.entity.Url
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_url.view.*

class UrlAdapter(
        private val listener: UrlListener
) : DataBoundAdapter<Url>() {

    interface UrlListener {
        fun onDeleteUrl(index: Int)

        fun onClickListener(index: Int)

        fun onEditUrl(index: Int)
    }

    override fun inflateView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.item_url, parent, false)
    }

    override fun bind(itemView: View, item: Url, position: Int) {
        itemView.textTitle.text = item.url
        if (item.isChecked) {
            itemView.imageCheck.visible()
        } else {
            itemView.imageCheck.invisible()
        }

        itemView.imageDelete.setOnClickListener {
            listener.onDeleteUrl(position)
        }

        itemView.imageEdit.setOnClickListener {
            listener.onEditUrl(position)
        }

        itemView.setOnClickListener {
            listener.onClickListener(position)
        }
    }

    fun updateAllData(newList: ArrayList<Url>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun addData(field: Url) {
        items.add(field)
        notifyDataSetChanged()
    }
}
