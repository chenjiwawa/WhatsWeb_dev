package com.qltech.whatsweb.ui.setting.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.flurry.sdk.fa
import com.orhanobut.logger.Logger
import com.qltech.whatsweb.R
import com.qltech.whatsweb.ui.setting.language.adapter.LanguageAdapter
import com.qltech.whatsweb.ui.setting.language.model.Language
import com.qltech.whatsweb.util.Constants
import com.qltech.whatsweb.util.LanguageUtil
import com.qltech.whatsweb.util.SpUtil
import kotlinx.android.synthetic.main.fragment_language_list.*

class LanguageListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_language_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = LanguageAdapter(getLanguageList(), context)
    }

    private fun getLanguageList(): List<Language> {
        val list = ArrayList<Language>()
        list.add(
            Language(
                0,
                getString(R.string.language_en),
                Constants.LanguageSimplified.en,
                Constants.CountrySimplified.en,
                false
            )
        )
        list.add(
            Language(
                1,
                getString(R.string.language_es),
                Constants.LanguageSimplified.es,
                Constants.CountrySimplified.es,
                false
            )
        )
        list.add(
            Language(
                2,
                getString(R.string.language_pt),
                Constants.LanguageSimplified.pt,
                Constants.CountrySimplified.pt,
                false
            )
        )
        list.add(
            Language(
                3,
                getString(R.string.language_id),
                Constants.LanguageSimplified.id,
                Constants.CountrySimplified.id,
                false
            )
        )

        Logger.d(" getLanguageList default " + getLanguage());

        for (i in list.indices) {
            if (getLanguage()?.contains(list[i].languageSimplified, true)!!) {
                list[i].select = true
            }
        }

        return list
    }

    fun getLanguage(): String? {
        var languageSimplified: String? =
            SpUtil.decodeString(SpUtil.Key.KEY_DEFAULT_LANGUAGE_SIMPLIFIED)
        var countrySimplified: String? =
            SpUtil.decodeString(SpUtil.Key.KEY_DEFAULT_COUNTRY_SIMPLIFIED)

        Logger.d(" getLanguage languageSimplified " + languageSimplified + " countrySimplified " + countrySimplified);
        if (!(languageSimplified?.isBlank()!!)) {
            return languageSimplified;
        }

        var systemLanguage: String? = LanguageUtil.getSystemDefaultLocale()?.language

        if ((systemLanguage?.contains(
                Constants.LanguageSimplified.en,
                true
            )!!) || (systemLanguage?.contains(
                Constants.LanguageSimplified.es,
                true
            )!!) || (systemLanguage?.contains(
                Constants.LanguageSimplified.pt,
                true
            )!!) || (systemLanguage?.contains(
                Constants.LanguageSimplified.id,
                true
            )!!)
        ) {
            return systemLanguage
        }

        return Constants.LanguageSimplified.en;
    }

}