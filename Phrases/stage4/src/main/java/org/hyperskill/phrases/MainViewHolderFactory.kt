package org.hyperskill.phrases

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import org.hyperskill.phrases.data.PhrasesRepository
import org.hyperskill.phrases.ui.MainViewModel

class MainViewHolderFactory(val application: Application, val repository: PhrasesRepository) : ViewModelProvider.Factory {


    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application, repository) as T
    }
}