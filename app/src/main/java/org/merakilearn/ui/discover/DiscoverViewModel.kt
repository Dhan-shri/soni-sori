package org.merakilearn.ui.discover

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.merakilearn.datasource.ApplicationRepo
import org.merakilearn.datasource.Config
import org.merakilearn.datasource.network.model.Classes
import org.merakilearn.datasource.network.model.Language
import org.merakilearn.util.relativeDay
import org.merakilearn.util.toDate
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider

class DiscoverViewModel(
    private val applicationRepo: ApplicationRepo,
    private val stringProvider: StringProvider,
    config: Config
) :
    BaseViewModel<EmptyViewEvents, DiscoverViewState>(DiscoverViewState()) {

    private var classes: List<Classes>? = null

    val supportedLanguages = MutableLiveData<List<Language>>(config.getObjectifiedValue(Config.KEY_AVAILABLE_LANG))

    init {
        fetchClassesData(null)
    }

    private fun fetchClassesData(langCode: String?) {
        viewModelScope.launch {
            classes = applicationRepo.fetchUpcomingClassData(langCode)
            classes?.let {
                setState {
                    val items = it.toDiscoverData()
                    val emptyData = items.isEmpty()
                    copy(
                        isLoading = false,
                        searchEnabled = !emptyData,
                        showError = false,
                        showNoContent = emptyData,
                        itemList = items
                    )
                }
            } ?: run {
                setState { copy(isLoading = false, searchEnabled = false, showError = true) }
            }
        }
    }

    fun handle(action: DiscoverViewActions) {
        when (action) {
            is DiscoverViewActions.Query -> handleQuery(action)

            is DiscoverViewActions.FilterFromClass -> handleClassFromLangCode(action)
        }
    }


    private fun handleQuery(action: DiscoverViewActions.Query) {
        val classes = classes ?: return
        viewModelScope.launch(Dispatchers.Default) {
            if (action.query.isNullOrBlank()) {
                setState { copy(itemList = classes.toDiscoverData()) }
            } else {
                val result = classes.filter {
                    val query = action.query
                    val wordsToCompare = it.title.split(" ") + it.type.split("_")
                    wordsToCompare.find { word -> word.startsWith(query, true) } != null
                }.toDiscoverData()
                setState { copy(itemList = result) }
            }
        }
    }

    private fun handleClassFromLangCode(action: DiscoverViewActions.FilterFromClass) {
        fetchClassesData(action.langCode)
    }

    private fun List<Classes>.toDiscoverData(): List<DiscoverData> {
        return groupBy { it.startTime.toDate() }
            .map {
                val title = "${it.key.toDate().relativeDay(stringProvider)}, ${it.key}"
                DiscoverData(it.key, title, it.value)
            }
    }
}

data class DiscoverData(val date: String, val title: String, val data: List<Classes>)

data class DiscoverViewState(
    val isLoading: Boolean = true,
    val showError: Boolean = false,
    val showNoContent: Boolean = false,
    val searchEnabled: Boolean = false,
    val itemList: List<DiscoverData> = arrayListOf()
) : ViewState

sealed class DiscoverViewActions : ViewModelAction {
    data class Query(val query: String?) : DiscoverViewActions()
    data class FilterFromClass(val langCode: String?) : DiscoverViewActions()
}