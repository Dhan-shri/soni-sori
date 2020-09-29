package org.merakilearn.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.EnrollActivity
import org.merakilearn.R
import org.merakilearn.databinding.FragmentHomeBinding
import org.merakilearn.ui.home.adapter.MyUpcomingClassAdapter
import org.merakilearn.ui.home.adapter.OtherCourseAdapter
import org.merakilearn.ui.home.adapter.WhereYouLeftAdapter
import org.merakilearn.ui.onboarding.LoginFragment
import org.merakilearn.util.AppUtils
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.CourseDetailActivity


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
        const val TAG = "HomeFragment"
    }

    private lateinit var mBinding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var mWhereYouLeftAdapter: WhereYouLeftAdapter
    private lateinit var mOtherCourseAdapter: OtherCourseAdapter
    private lateinit var mMyUpcomingClassAdapter: MyUpcomingClassAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        initMyClassViewAndData()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initDiscoverClassButton()
        initOtherCourseViewAndData()
    }

    private fun initOtherCourseViewAndData() {
        initOtherCourseRV()
        fetchDataOtherCourse()
    }

    private fun initMyClassViewAndData() {
        initUpComingClassesRV()
        fetchMyClassData()
    }

    private fun initDiscoverClassButton() {
        mBinding.emptyMyClass.root.setOnClickListener {
            startDiscoverFragment()
        }

        mBinding.viewAll.setOnClickListener {
            startDiscoverFragment()
        }
    }

    private fun startDiscoverFragment() {
        AppUtils.changeFragment(
            parentFragmentManager,
            DiscoverFragment.newInstance(),
            R.id.nav_host_fragment,
            true,
            LoginFragment.TAG
        )
    }

    private fun fetchDataOtherCourse() {
        mBinding.progressBarButton.visibility = View.VISIBLE
        viewModel.fetchOtherCourseData().observe(viewLifecycleOwner, Observer {
            mBinding.progressBarButton.visibility = View.GONE
            if (null != it && it.isNotEmpty()) {
                mOtherCourseAdapter.submitList(it)
            }
        })
    }

    private fun fetchMyClassData() {
        mBinding.progressBarButtonMy.visibility = View.VISIBLE
        viewModel.fetchMyClasses().observe(viewLifecycleOwner, Observer {
            mBinding.progressBarButtonMy.visibility = View.GONE
            if (null != it && it.isNotEmpty()) {
                mBinding.emptyMyClass.root.visibility = View.GONE
                mBinding.recyclerviewMyUpcomingClass.visibility = View.VISIBLE
                mMyUpcomingClassAdapter.submitList(it)
            } else {
                mBinding.emptyMyClass.root.visibility = View.VISIBLE
                mBinding.recyclerviewMyUpcomingClass.visibility = View.GONE
            }
        })
    }

    private fun initWhereYouLeftRV() {
        mWhereYouLeftAdapter = WhereYouLeftAdapter {
            toast(it.first.toString())
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewCourseContinue.layoutManager = layoutManager
        mBinding.recyclerviewCourseContinue.adapter = mWhereYouLeftAdapter
    }

    private fun initOtherCourseRV() {
        mOtherCourseAdapter = OtherCourseAdapter {
            CourseDetailActivity.start(requireContext(), it.id, it.name)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewOtherCourse.layoutManager = layoutManager
        mBinding.recyclerviewOtherCourse.adapter = mOtherCourseAdapter
    }

    private fun initUpComingClassesRV() {
        mMyUpcomingClassAdapter = MyUpcomingClassAdapter {
            EnrollActivity.start(requireContext(), it.classX?.id, true)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerviewMyUpcomingClass.layoutManager = layoutManager
        mBinding.recyclerviewMyUpcomingClass.adapter = mMyUpcomingClassAdapter
    }

}