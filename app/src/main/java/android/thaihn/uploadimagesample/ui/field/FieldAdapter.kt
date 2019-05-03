package android.thaihn.uploadimagesample.ui.field

import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.base.common.DataBoundAdapter
import android.thaihn.uploadimagesample.entity.Field
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_field.view.*

class FieldAdapter(
        private val listener: FieldListener
) : DataBoundAdapter<Field>() {

    interface FieldListener {
        fun onDeleteField(index: Int)

        fun onSelected(item: Field, index: Int, isChecked: Boolean)

        fun onEditField(item: Field, index: Int)
    }

    override fun inflateView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.item_field, parent, false)
    }

    override fun bind(itemView: View, item: Field, position: Int) {

        itemView.textTitle.text = item.title
        itemView.checkbox.isChecked = item.isChecked

        itemView.imageDelete.setOnClickListener {
            listener.onDeleteField(position)
        }

        itemView.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            listener.onSelected(item, position, isChecked)
        }

        itemView.imageEdit.setOnClickListener {
            listener.onEditField(item, position)
        }
    }

    fun updateAllData(newList: List<Field>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun addData(field: Field) {
        items.add(field)
        notifyDataSetChanged()
    }
}
