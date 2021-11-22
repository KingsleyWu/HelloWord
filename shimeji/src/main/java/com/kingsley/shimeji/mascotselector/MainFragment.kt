package com.kingsley.shimeji.mascotselector

import android.os.Bundle

import android.view.ViewGroup

import android.view.LayoutInflater

import com.kingsley.shimeji.data.TeamListingService

import android.widget.Toast

import com.kingsley.shimeji.service.ShimejiService

import android.app.ActivityManager
import android.content.Context

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView

import android.widget.Switch
import androidx.fragment.app.Fragment

import com.kingsley.shimeji.MainActivity
import com.kingsley.shimeji.data.Helper

//
//class MainFragment : Fragment(), SelectorView {
//    @BindView(2131230866)
//    var onTopSwitch: Switch? = null
//    private val presenter: SelectorPresenter = SelectorPresenterImpl(this)
//    private var thumbnails: MutableList<ImageView>? = null
//    private var unbinder: Unbinder? = null
//    private fun navigateToMascots(paramInt: Int) {
//        val fragmentTransaction: FragmentTransaction = getFragmentManager().beginTransaction()
//        fragmentTransaction.replace(2131230798, MascotsFragment.newInstance(paramInt) as Fragment)
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()
//    }
//
//    private fun navigateToPurchases() {
//        (getActivity() as MainActivity).selectItem(1)
//    }
//
//    override fun emptySlot(position: Int) {
//        val imageView: ImageView = thumbnails!![position]
//        imageView.setImageResource(2131165268)
//        imageView.setOnClickListener{
//                navigateToMascots(position)
//            }
//    }
//
//    override fun fillSlot(position: Int, bitmap: Bitmap?) {
//        val imageView: ImageView = thumbnails!![position]
//        imageView.setOnClickListener{
//                presenter.clearMascot(position)
//            }
//        imageView.setImageBitmap(bitmap)
//    }
//
//    override fun fillSlotWithErrorImage(position: Int) {
//        val imageView: ImageView = thumbnails!![position]
//        imageView.setImageDrawable(getResources().getDrawable(17301533))
//        imageView.setOnClickListener{
//            navigateToMascots(position)
//        }
//    }
//
//    protected val title: String
//        protected get() = getString(2131558468)
//    val isDisplayServiceRunning: Boolean
//        get() {
//            val activityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//            if (activityManager != null) for (runningServiceInfo in activityManager.getRunningServices(2147483647)) {
//                if (ShimejiService::class.java.name == runningServiceInfo.service.className) return true
//            }
//            return false
//        }
//
//    override fun lockSlot(position: Int) {
//        val imageView: ImageView = thumbnails!![position]
//        imageView.setImageResource(2131492865)
//        imageView.setOnClickListener{
//            navigateToPurchases()
//        }
//    }
//
//    override fun notifyLastSlotEmpty() {
//        Toast.makeText(getActivity(), getString(2131558443), 0).show()
//    }
//
//    override fun onCreate(paramBundle: Bundle?) {
//        super.onCreate(paramBundle)
//        presenter.loadMascots(TeamListingService.getInstance(requireContext())!!.activeMascots)
//        if (arguments != null) {
//            val i: Int = arguments?.getInt(ARG_MASCOT_ID) ?: 0
//            presenter.addMascot(TeamListingService.getInstance(requireContext())!!.getThumbById(i))
//        }
//    }
//
//    override fun onCreateView(
//        paramLayoutInflater: LayoutInflater,
//        paramViewGroup: ViewGroup?,
//        paramBundle: Bundle?
//    ): View? {
//        val view: View = paramLayoutInflater.inflate(2131361827, paramViewGroup, false)
//        unbinder = ButterKnife.bind(this, view)
//        val arrayList: ArrayList<ImageView> = ArrayList()
//        thumbnails = arrayList
//        arrayList.add(view.findViewById(2131230750) as ImageView)
//        thumbnails.add(view.findViewById(2131230751) as ImageView)
//        thumbnails.add(view.findViewById(2131230752) as ImageView)
//        thumbnails.add(view.findViewById(2131230753) as ImageView)
//        thumbnails.add(view.findViewById(2131230754) as ImageView)
//        thumbnails.add(view.findViewById(2131230755) as ImageView)
//        return view
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        unbinder.unbind()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        (getActivity() as MainActivity).removeMainFragmentHandler()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        presenter.updateMascots()
//        presenter.setDisplayServiceUI()
//        (getActivity() as MainActivity).addMainFragmentHandler(object : Callback {
//            override fun updateUI() {
//                presenter.updateMascots()
//            }
//        })
//    }
//
//    override fun saveMascotSelection(paramList: List<Int>) {
//        Helper.saveActiveTeamMembers(requireContext(), paramList)
//    }
//
//    override fun setSwitchChangeListener(action: SelectorView.OnDisplayMascotsAction?) {
//        onTopSwitch?.setOnCheckedChangeListener { _, checked ->
//            if (checked) {
//                action?.showMascots()
//            } else {
//                action?.hideMascots()
//            }
//        }
//    }
//
//    override fun setSwitchChecked(paramBoolean: Boolean) {
//        onTopSwitch?.isChecked = paramBoolean
//    }
//
//    override fun startDisplayService(): Boolean {
//        return (getActivity() as MainActivity).startShimejiService()
//    }
//
//    override fun stopDisplayService() {
//        (getActivity() as MainActivity).stopShimejiService()
//    }
//
//    interface Callback {
//        fun updateUI()
//    }
//
//    companion object {
//        private const val ARG_MASCOT_ID = "id"
//        fun newInstance(): MainFragment {
//            return MainFragment()
//        }
//
//        fun newInstance(paramInt: Int): MainFragment {
//            val mainFragment = MainFragment()
//            val bundle = Bundle()
//            bundle.putInt(ARG_MASCOT_ID, paramInt)
//            mainFragment.arguments = bundle
//            return mainFragment
//        }
//    }
//}
