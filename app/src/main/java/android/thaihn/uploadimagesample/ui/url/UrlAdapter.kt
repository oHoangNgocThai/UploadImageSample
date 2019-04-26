package android.thaihn.uploadimagesample.ui.url

import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.base.common.DataBoundAdapter
import android.thaihn.uploadimagesample.entity.Url
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_url.view.*

class UrlAdapter(
        private val listener: UrlListener
) : DataBoundAdapter<Url>() {

    interface UrlListener {
        fun onDeleteUrl(item: Url)

        fun onClickListener(position: Int, item: Url)
    }

    override fun inflateView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.item_url, parent, false)
    }

    override fun bind(itemView: View, item: Url, position: Int) {
        itemView.textTitle.text = item.url
        if (item.isChecked) {
            itemView.imageCheck.visibility = View.VISIBLE
        } else {
            itemView.imageCheck.visibility = View.INVISIBLE
        }

        itemView.imageDelete.setOnClickListener {
            listener.onDeleteUrl(item)
        }

        itemView.setOnClickListener {
            listener.onClickListener(position, item)
        }
    }

    fun updateAllData(newList: ArrayList<Url>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
