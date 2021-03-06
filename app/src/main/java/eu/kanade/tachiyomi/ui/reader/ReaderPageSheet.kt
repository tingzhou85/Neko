package eu.kanade.tachiyomi.ui.reader

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeDp
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.reader.model.ReaderPage
import kotlinx.android.synthetic.main.reader_page_sheet.*

/**
 * Sheet to show when a page is long clicked.
 */
class ReaderPageSheet(
        private val activity: ReaderActivity,
        private val page: ReaderPage
) : BottomSheetDialog(activity) {

    /**
     * View used on this sheet.
     */
    private val view = activity.layoutInflater.inflate(R.layout.reader_page_sheet, null)

    init {
        setContentView(view)
        share_layout.setOnClickListener { share() }
        save_layout.setOnClickListener { save() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val width = context.resources.getDimensionPixelSize(R.dimen.bottom_sheet_width)
        if (width > 0) {
            window?.setLayout(width, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun setContentView(view: View) {
        super.setContentView(view!!)
        share_image.setImageDrawable(IconicsDrawable(context).icon(CommunityMaterial.Icon2.cmd_share_variant)
                .colorInt(Color.GRAY).sizeDp(20))
        save_image.setImageDrawable(IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_download)
                .colorInt(Color.GRAY).sizeDp(20))
    }


    /**
     * Shares the image of this page with external apps.
     */
    private fun share() {
        activity.shareImage(page)
        dismiss()
    }

    /**
     * Saves the image of this page on external storage.
     */
    private fun save() {
        activity.saveImage(page)
        dismiss()
    }

}
