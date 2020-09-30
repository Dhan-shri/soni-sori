package org.navgurukul.chat.features.home.room.detail.timeline.item

import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R

@EpoxyModelClass
abstract class RedactedMessageItem : AbsMessageItem<RedactedMessageItem.Holder>() {

    override fun getDefaultLayout(): Int =
        if (attributes.informationData.sentByMe) {
            R.layout.sent_item_timeline_event_base
        } else {
            R.layout.item_timeline_event_base
        }

    override fun getViewType() = if (attributes.informationData.sentByMe) {
        STUB_ID + R.drawable.sent_timeline_item_background
    } else {
        STUB_ID + R.drawable.received_timeline_item_background
    }

    override fun shouldShowReactionAtBottom() = false

    class Holder : AbsMessageItem.Holder(STUB_ID)

    companion object {
        private val STUB_ID = R.id.messageContentRedactedStub
    }
}