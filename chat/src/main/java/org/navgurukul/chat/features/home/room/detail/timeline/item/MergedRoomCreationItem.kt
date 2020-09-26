package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController

@EpoxyModelClass
abstract class MergedRoomCreationItem : BasedMergedItem<MergedRoomCreationItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.item_timeline_event_base_noinfo

    @EpoxyAttribute
    override lateinit var attributes: Attributes

    override fun getViewType() = STUB_ID

    override fun bind(holder: Holder) {
        super.bind(holder)

        if (attributes.isCollapsed) {
            // Take the oldest data
            val data = distinctMergeData.lastOrNull()

            val createdFromCurrentUser = data?.userId == attributes.currentUserId
            val summary = if (createdFromCurrentUser) {
                holder.expandView.resources.getString(R.string.room_created_summary_item_by_you)
            } else {
                holder.expandView.resources.getString(R.string.room_created_summary_item, data?.memberName ?: data?.userId ?: "")
            }
            holder.summaryView.text = summary
            holder.summaryView.visibility = View.VISIBLE
            holder.avatarView.visibility = View.VISIBLE
            if (data != null) {
                holder.avatarView.visibility = View.VISIBLE
                attributes.avatarRenderer.render(data.toMatrixItem(), holder.avatarView)
            } else {
                holder.avatarView.visibility = View.GONE
            }

            if (attributes.hasEncryptionEvent) {
                holder.encryptionTile.isVisible = true
                holder.encryptionTile.updateLayoutParams<RelativeLayout.LayoutParams> {
                    this.marginEnd = leftGuideline
                }
                if (attributes.isEncryptionAlgorithmSecure) {
                    holder.e2eTitleTextView.text = holder.expandView.resources.getString(R.string.encryption_enabled)
                    holder.e2eTitleDescriptionView.text = holder.expandView.resources.getString(R.string.encryption_enabled_tile_description)
                    holder.e2eTitleDescriptionView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    holder.e2eTitleTextView.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(holder.view.context, R.drawable.ic_shield_black),
                        null, null, null
                    )
                } else {
                    holder.e2eTitleTextView.text = holder.expandView.resources.getString(R.string.encryption_not_enabled)
                    holder.e2eTitleDescriptionView.text = holder.expandView.resources.getString(R.string.encryption_unknown_algorithm_tile_description)
                    holder.e2eTitleTextView.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(holder.view.context, R.drawable.ic_shield_warning),
                        null, null, null
                    )
                }
            } else {
                holder.encryptionTile.isVisible = false
            }
        } else {
            holder.avatarView.visibility = View.INVISIBLE
            holder.summaryView.visibility = View.GONE
            holder.encryptionTile.isGone = true
        }
        // No read receipt for this item
//        holder.readReceiptsView.isVisible = false
    }

    class Holder : BasedMergedItem.Holder(STUB_ID) {
        val summaryView by bind<TextView>(R.id.itemNoticeTextView)
        val avatarView by bind<ImageView>(R.id.itemNoticeAvatarView)
        val encryptionTile by bind<ViewGroup>(R.id.creationEncryptionTile)

        val e2eTitleTextView by bind<TextView>(R.id.itemVerificationDoneTitleTextView)
        val e2eTitleDescriptionView by bind<TextView>(R.id.itemVerificationDoneDetailTextView)
    }

    companion object {
        private val STUB_ID = R.id.messageContentMergedCreationStub
    }

    data class Attributes(
        override val isCollapsed: Boolean,
        override val mergeData: List<Data>,
        override val avatarRenderer: AvatarRenderer,
        override val readReceiptsCallback: TimelineEventController.ReadReceiptsCallback? = null,
        override val onCollapsedStateChanged: (Boolean) -> Unit,
        val currentUserId: String,
        val hasEncryptionEvent: Boolean,
        val isEncryptionAlgorithmSecure: Boolean
    ) : BasedMergedItem.Attributes
}
