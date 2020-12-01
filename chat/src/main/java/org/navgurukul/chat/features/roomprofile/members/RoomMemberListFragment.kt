package org.navgurukul.chat.features.roomprofile.members

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import im.vector.matrix.android.api.session.events.model.Event
import im.vector.matrix.android.api.session.events.model.toModel
import im.vector.matrix.android.api.session.room.model.RoomMemberSummary
import im.vector.matrix.android.api.session.room.model.RoomThirdPartyInviteContent
import im.vector.matrix.android.api.util.toMatrixItem
import kotlinx.android.synthetic.main.fragment_room_setting_generic.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.args
import org.navgurukul.chat.core.extensions.cleanup
import org.navgurukul.chat.core.extensions.configureWith
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.navigator.ChatInternalNavigator
import org.navgurukul.chat.features.roomprofile.RoomProfileArgs
import org.navgurukul.commonui.platform.BaseFragment

class RoomMemberListFragment: BaseFragment(), RoomMemberListController.Callback {

    private val roomMemberListController: RoomMemberListController by inject()
    private val avatarRenderer: AvatarRenderer by inject()

    private val roomProfileArgs: RoomProfileArgs by args()

    private val viewModel: RoomMemberListViewModel by viewModel(parameters = { parametersOf(RoomMemberListViewState(roomProfileArgs.roomId))})
    private val navigator: ChatInternalNavigator by inject()

    override fun getLayoutResId() = R.layout.fragment_room_member_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomMemberListController.callback = this
        setupToolbar(roomSettingsToolbar)
        setupSearchView()
//        setupInviteUsersButton()
        recyclerView.configureWith(roomMemberListController, hasFixedSize = true)

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            setUpWithState(it)
        })
    }

//    private fun setupInviteUsersButton() {
//        inviteUsersButton.debouncedClicks {
//            navigator.openInviteUsersToRoom(requireContext(), roomProfileArgs.roomId)
//        }
//        // Hide FAB when list is scrolling
//        recyclerView.addOnScrollListener(
//                object : RecyclerView.OnScrollListener() {
//                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                        when (newState) {
//                            RecyclerView.SCROLL_STATE_IDLE     -> {
//                                if (withState(viewModel) { it.actionsPermissions.canInvite }) {
//                                    inviteUsersButton.show()
//                                }
//                            }
//                            RecyclerView.SCROLL_STATE_DRAGGING,
//                            RecyclerView.SCROLL_STATE_SETTLING -> {
//                                inviteUsersButton.hide()
//                            }
//                        }
//                    }
//                }
//        )
//    }

    private fun setupSearchView() {
        searchView.queryHint = getString(R.string.search_members_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.handle(RoomMemberListAction.FilterMemberList(newText))
                return true
            }
        })
    }

    override fun onDestroyView() {
        recyclerView.cleanup()
        super.onDestroyView()
    }

    private fun setUpWithState(viewState: RoomMemberListViewState) {
        roomMemberListController.setData(viewState)
        renderRoomSummary(viewState)
//        inviteUsersButton.isVisible = viewState.actionsPermissions.canInvite
        // Display filter only if there are more than 2 members in this room
        searchViewAppBarLayout.isVisible = viewState.roomSummary()?.otherMemberIds.orEmpty().size > 1
    }

    override fun onRoomMemberClicked(roomMember: RoomMemberSummary) {
    }

    override fun onThreePidInviteClicked(event: Event) {
        // Display a dialog to revoke invite if power level is high enough
        val content = event.content.toModel<RoomThirdPartyInviteContent>() ?: return
        val stateKey = event.stateKey ?: return
        if (withState(viewModel) { it.actionsPermissions.canRevokeThreePidInvite }) {
            AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.three_pid_revoke_invite_dialog_title)
                    .setMessage(getString(R.string.three_pid_revoke_invite_dialog_content, content.displayName))
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.revoke) { _, _ ->
                        viewModel.handle(RoomMemberListAction.RevokeThreePidInvite(stateKey))
                    }
                    .show()
        }
    }

    private fun renderRoomSummary(state: RoomMemberListViewState) {
        state.roomSummary()?.let {
            roomSettingsToolbarTitleView.text = it.displayName
            avatarRenderer.render(it.toMatrixItem(), roomSettingsToolbarAvatarImageView)
        }
    }
}