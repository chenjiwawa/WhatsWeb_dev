package com.qltech.whatsweb.ui.setting.language.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.qltech.whatsweb.R
import com.qltech.whatsweb.ui.event.LanguageEvent
import com.qltech.whatsweb.ui.setting.language.model.Language
import com.qltech.whatsweb.util.SpUtil
import kotlinx.android.synthetic.main.item_language.view.*
import org.greenrobot.eventbus.EventBus

class LanguageAdapter(private val list: List<Language>, private var context: Context?) :
    RecyclerView.Adapter<LanguageAdapter.LanguageHolder>() {

    class LanguageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.item_content
        val item: View = itemView.item
        val select: ImageView = itemView.select
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        return LanguageHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: LanguageHolder, position: Int) {
        val currentItem = list[position]
        holder.content.text = currentItem.language
        holder.select.visibility = if (currentItem.select) View.VISIBLE else View.GONE

        holder.item.setOnClickListener {
            Logger.d(" setOnClickListener " + currentItem.language);
            EventBus.getDefault().post(LanguageEvent(currentItem))
            SpUtil.encode(
                SpUtil.Key.KEY_DEFAULT_LANGUAGE_SIMPLIFIED,
                currentItem.languageSimplified
            )
            SpUtil.encode(SpUtil.Key.KEY_DEFAULT_COUNTRY_SIMPLIFIED, currentItem.countrySimplified)
        }
    }
}