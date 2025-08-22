package com.example.design_system

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CursorAdapter
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.SimpleCursorAdapter

class LoadingSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    private val searchView: SearchView
    private val progressBar: ProgressBar

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.loading_search_view, this, true)
        searchView = root.findViewById(R.id.searchView)
        progressBar = root.findViewById(R.id.progressBar)
        loadAttr(attrs)
    }

    private fun loadAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingSearchView)
        val loading = typedArray.getBoolean(R.styleable.LoadingSearchView_isLoading, false)
        val queryHint = typedArray.getString(R.styleable.LoadingSearchView_queryHint)
        typedArray.recycle()
        setLoading(loading)
        setQueryHint(queryHint)
    }

    fun setOnQueryTextListener(listener: SearchView.OnQueryTextListener) =
        searchView.setOnQueryTextListener(listener)

    fun setOnSuggestionListener(listener: SearchView.OnSuggestionListener) =
        searchView.setOnSuggestionListener(listener)

    fun setQuery(query: String, submit: Boolean) =
        searchView.setQuery(query, submit)

    fun setSuggestionsAdapter(adapter: SimpleCursorAdapter) {
        searchView.suggestionsAdapter = adapter
    }

    fun getSuggestionsAdapter(): CursorAdapter {
        return searchView.suggestionsAdapter
    }

    fun requestSearchViewFocus() {
        searchView.requestFocus()
    }

    private fun setQueryHint(hint: String?) {
        searchView.queryHint = hint ?: ""
    }

    fun setLoading(loading: Boolean) {
        if (loading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}