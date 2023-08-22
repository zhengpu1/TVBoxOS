package com.github.tvbox.osc.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.github.tvbox.osc.R
import com.github.tvbox.osc.base.App
import com.github.tvbox.osc.bean.VodInfo
import com.github.tvbox.osc.bean.VodInfo.VodSeries
import com.github.tvbox.osc.cache.RoomDataManger
import com.github.tvbox.osc.event.RefreshEvent
import com.github.tvbox.osc.server.ControlManager.mContext
import com.github.tvbox.osc.ui.activity.PlayActivity
import com.github.tvbox.osc.ui.activity.PlayEvent
import com.github.tvbox.osc.ui.adapter.SeriesAdapter
import com.github.tvbox.osc.ui.adapter.SeriesAdapters
import com.github.tvbox.osc.util.FastClickCheckUtil
import com.owen.tvrecyclerview.widget.TvRecyclerView
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager
import me.jessyan.autosize.utils.AutoSizeUtils
import org.greenrobot.eventbus.EventBus
import razerdp.basepopup.BasePopupWindow

class ChoosePlayPopUp(context: Context?) : BasePopupWindow(context) {
    private var seriesAdapters: SeriesAdapters? = null
    private var mGridView: TvRecyclerView? = null
    private val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    init {
        setContentView(R.layout.choose_play_layout)
        mGridView = contentView.findViewById(R.id.mGridView)
        mGridView = findViewById(R.id.mGridView)
        seriesAdapters = SeriesAdapters()
        mGridView?.run {
            setHasFixedSize(true)
            layoutManager = V7LinearLayoutManager(getContext(), 0,false)
            adapter = seriesAdapters
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
        val vodInfo = App.instance?.getVodInfo()

        val list: MutableList<VodSeries> =
            vodInfo?.seriesMap?.get(vodInfo.playFlag) ?: mutableListOf()
        seriesAdapters?.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, view, position ->
                FastClickCheckUtil.check(view)
                if (context is PlayActivity) {//解决打开多个PlayActivity的问题
                    context.finish()
                }
                if ((list.size) > 0) {
                    for (j in list.indices) {
                        seriesAdapters!!.data[j].selected = false
                        seriesAdapters!!.notifyItemChanged(j)
                    }
                    //解决倒叙不刷新
                    if (vodInfo?.playIndex != position) {
                        seriesAdapters!!.data[position].selected = true
                        seriesAdapters!!.notifyItemChanged(position)
                        vodInfo?.playIndex = position
                    }
                    seriesAdapters!!.data[vodInfo?.playIndex ?: 0].selected = true
                    seriesAdapters!!.notifyItemChanged(vodInfo?.playIndex ?: 0)
                    insertVod(vodInfo ?: return@OnItemClickListener)
                    EventBus.getDefault().post(PlayEvent().apply {
                        this.vodInfo = vodInfo
                    })
                    this.dismiss()
                }
            }
        setBackgroundColor(Color.TRANSPARENT)
        val pFont = Paint()
        val rect = Rect()
        var w = 1
        for (i in list.indices) {
            val name = list[i].name
            pFont.getTextBounds(name, 0, name.length, rect)
            if (w < rect.width()) {
                w = rect.width()
            }
        }
        w += 32
        val screenWidth: Int = ScreenUtils.getScreenWidth() / 3
        var offset = screenWidth / w
        if (offset <= 1) offset = 1
        if (offset > 6) offset = 10
        //(this.mGridView?.layoutManager as V7GridLayoutManager).spanCount = offset
        seriesAdapters?.setNewData(vodInfo?.seriesMap?.get(vodInfo.playFlag))

        mHandler.postDelayed({
            mGridView?.layoutManager?.let {
                val childCount = it.childCount;
                for (i in 0 until childCount) {
                    it.getChildAt(i)?.setOnFocusChangeListener { _, _ ->
                        mHandler.removeCallbacksAndMessages(null)
                        mHandler.postDelayed(dismissRunnable, 4000)
                    }
                }
            }
        }, 500)

        mGridView?.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mHandler.removeCallbacks(dismissRunnable)
                mHandler.postDelayed(dismissRunnable, 4000)
            }
        })
        mGridView?.setOnTouchListener { v, event ->
            mHandler.removeCallbacks(dismissRunnable)
            mHandler.postDelayed(dismissRunnable, 4000)
            false
        }

        mGridView?.scrollToPositionWithOffset(vodInfo?.playIndex ?: 0, 0, true)

    }

    private fun LParams(RIDD: LinearLayout) {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.width = AutoSizeUtils.mm2px(mContext, 112f)
        RIDD.layoutParams = params
    }
    override fun showPopupWindow(anchorView: View?) {
        super.showPopupWindow(anchorView)
        popupWindow.isFocusable = true
        mHandler.postDelayed(dismissRunnable, 14000)
    }

    override fun dismiss() {
        super.dismiss()
        mHandler.removeCallbacksAndMessages(null)
    }

    private fun insertVod(vodInfo: VodInfo) {
        try {
            vodInfo.playNote = vodInfo.seriesMap[vodInfo.playFlag]!![vodInfo.playIndex].name
        } catch (th: Throwable) {
            vodInfo.playNote = ""
        }
        RoomDataManger.insertVodRecord(vodInfo.sourceKey, vodInfo)
        EventBus.getDefault().post(RefreshEvent(RefreshEvent.TYPE_HISTORY_REFRESH))
    }

    private val dismissRunnable: Runnable = Runnable { this@ChoosePlayPopUp.dismiss() }

}