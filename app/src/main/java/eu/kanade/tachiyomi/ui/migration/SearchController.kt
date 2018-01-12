package eu.kanade.tachiyomi.ui.migration

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.preference.getOrDefault
import eu.kanade.tachiyomi.ui.base.controller.DialogController
import eu.kanade.tachiyomi.ui.catalogue.global_search.CatalogueSearchController
import eu.kanade.tachiyomi.ui.catalogue.global_search.CatalogueSearchPresenter
import uy.kohesive.injekt.injectLazy

class SearchController(
        private var manga: Manga? = null
) : CatalogueSearchController(manga?.title) {

    private var newManga: Manga? = null

    override fun createPresenter(): CatalogueSearchPresenter {
        return SearchPresenter(initialQuery, manga!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(::manga.name, manga)
        outState.putSerializable(::newManga.name, newManga)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        manga = savedInstanceState.getSerializable(::manga.name) as? Manga
        newManga = savedInstanceState.getSerializable(::newManga.name) as? Manga
    }

    fun migrateManga() {
        val target = targetController as? MigrationController ?: return
        val manga = manga ?: return
        val newManga = newManga ?: return

        router.popController(this)
        target.migrateManga(manga, newManga)
    }

    fun copyManga() {
        val target = targetController as? MigrationController ?: return
        val manga = manga ?: return
        val newManga = newManga ?: return

        router.popController(this)
        target.copyManga(manga, newManga)
    }

    override fun onMangaClick(manga: Manga) {
        newManga = manga
        val dialog = MigrationDialog()
        dialog.targetController = this
        dialog.showDialog(router)
    }

    class MigrationDialog : DialogController() {

        private val preferences: PreferencesHelper by injectLazy()

        override fun onCreateDialog(savedViewState: Bundle?): Dialog {
            val optionTitles = arrayOf(
                    R.string.chapters,
                    R.string.categories,
                    R.string.track
            )

            val optionPrefs = arrayOf(
                    preferences.migrateChapters(),
                    preferences.migrateCategories(),
                    preferences.migrateTracks()
            )

            val preselected = optionPrefs.mapIndexedNotNull { index, preference ->
                if (preference.getOrDefault()) index else null
            }

            return MaterialDialog.Builder(activity!!)
                    .content(R.string.migration_dialog_what_to_include)
                    .items(optionTitles.map { resources?.getString(it) })
                    .alwaysCallMultiChoiceCallback()
                    .itemsCallbackMultiChoice(preselected.toTypedArray(), { _, positions, _ ->
                        // Save current settings for the next time
                        optionPrefs.forEachIndexed { index, preference ->
                            preference.set(index in positions)
                        }
                        true
                    })
                    .positiveText(R.string.migrate)
                    .negativeText(R.string.copy)
                    .neutralText(android.R.string.cancel)
                    .onPositive { _, _ ->
                        (targetController as? SearchController)?.migrateManga()
                    }
                    .onNegative { _, _ ->
                        (targetController as? SearchController)?.copyManga()
                    }
                    .build()
        }

    }

}