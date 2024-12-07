package br.com.firstsoft.target.server

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

object ApplicationViewModelStoreOwner : ViewModelStoreOwner {
    private val _store = ViewModelStore()
    override val viewModelStore: ViewModelStore
        get() = _store
}
