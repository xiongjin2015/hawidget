
package com.haha.hwidget;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.haha.hwidget.adapter.HaBaseAdapter;
import com.haha.hwidget.adapter.HaBaseAdapter.OnItemLoadingView;
import com.haha.hwidget.util.HaScreen;

/**
 * 水平循环滚动控件
 * 
 * @author xj
 */
@SuppressLint("ClickableViewAccessibility")
public class HaCycleView<T> extends FrameLayout implements OnItemClickListener,
        OnItemSelectedListener, View.OnTouchListener {

    private final static String TAG = "HaCycleView";

    /* auto scroll message's what */
    private final static int AUTO_SCROLL = 100; // 标识自动滚动消息的ID

    /* time of auto scroll */
    private final static int AUTO_SCROLL_TIME = 3 * 1000; // 自动滚动间隔时间

    // overtime cardinal number
    private final static int OVERTIME_CARDINAL_NUMBER = 2; // 延迟基数

    private HaExclusiveEventGallery mSlideDataHome;
    private RadioGroup mSlideIndex;
    private View mBaseView;

    private OnCycleItemClickListener<T> mOnCycleItemClickListener;
    private OnCycleItemSelectedListener<T> mOnCycleItemSelectedListener = null;

    /* 底部标识小圆点的大小 */
    private int mSlideIndexBtnSize;

    private HaCycleAdapter<T> mCycleAdapter;

    public HaCycleView(Context context) {
        super(context);
        initialize(null);
    }

    public HaCycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public HaCycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        initView();
        initResources();
        initFromAttributes(attrs);
    }

    private void initResources() {
        mSlideIndexBtnSize = HaScreen.dip2px(getContext(), 12);
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_cycle, this, true);
        mSlideDataHome = (HaExclusiveEventGallery) findViewById(R.id.cycle_gallery);
        mSlideIndex = (RadioGroup) findViewById(R.id.cycle_radiogroup);
        mBaseView = findViewById(R.id.cycle_base);
        initViewListener();
    }

    private void initFromAttributes(AttributeSet attrs) {
        if (attrs == null)
            return;
        mBaseView.setLayoutParams(generateLayoutParams(attrs));
        TypedArray typeds = getContext().obtainStyledAttributes(attrs, R.styleable.HaCycle);

        /* 用于控制首页推荐循环控件的高度的属性 */
        if (typeds.hasValue(R.styleable.HaCycle_view_height)) {
            int height = typeds.getDimensionPixelSize(R.styleable.HaCycle_view_height,
                    HaScreen.dip2px(getContext(), 160));
            setHeightPx(height);
        }

        /* 用于控制循环控制控件底部标识索引点大小的熟悉 */
        if (typeds.hasValue(R.styleable.HaCycle_index_size)) {
            int size = typeds.getDimensionPixelSize(R.styleable.HaCycle_index_size,
                    HaScreen.dip2px(getContext(), 12));
            setIndexSize(size);
        }

        /* 用于控制循环控制控件底部标识索引点展示位置的控件:靠左、居中或者靠右 */
        if (typeds.hasValue(R.styleable.HaCycle_index_gravity)) {
            int gravity = typeds.getInt(R.styleable.HaCycle_index_gravity, Gravity.RIGHT);
            setIndexGravityByXml(gravity);
        }
        typeds.recycle();
    }

    /**
     * 设置自动水平循环滚动控件HaCycleView高度的方法 该方法之所以设置为public：支持代码里动态设置
     * 也提供了xml布局文件里设置，见方法：initFromAttributes(AttributeSet attrs)
     * 
     * @param height
     */
    public void setHeightPx(int height) {
        ViewGroup.LayoutParams params = mBaseView.getLayoutParams();
        params.height = height;
        mBaseView.setLayoutParams(params);
    }

    /**
     * 设置自动水平循环滚动控件HaCycleView里的底部标识索引小圆点的大小 该方法之所以设置为public：支持代码里动态设置
     * 也提供了xml布局文件里设置，见方法：initFromAttributes(AttributeSet attrs)
     * 
     * @param height
     */
    public void setIndexSize(int size) {
        mSlideIndexBtnSize = size;
        if (mSlideIndex == null)
            return;
        final int count = mSlideIndex.getChildCount();
        if (count <= 0)
            return;

        for (int i = 0; i < count; i++) {
            RadioButton rb = (RadioButton) mSlideIndex.getChildAt(i);
            setRdBtnSize(rb);
        }
    }

    private void setRdBtnSize(RadioButton rb) {
        rb.setWidth(mSlideIndexBtnSize);
    }

    private void setIndexGravityByXml(int gravity) {
        switch (gravity) {
            case 0:
                gravity = Gravity.LEFT;
                break;
            case 1:
                gravity = Gravity.CENTER;
                break;
            case 2:
                gravity = Gravity.RIGHT;
                break;
        }
        setIndexGravity(gravity);
    }

    /**
     * Describes how the child views are positioned. Defaults to GRAVITY_TOP. If this layout has a
     * VERTICAL orientation, this controls where all the child views are placed if there is extra
     * vertical space. If this layout has a HORIZONTAL orientation, this controls the alignment of
     * the children.
     * 
     * @param gravity See {@link android.view.Gravity}
     * @attr ref android.R.styleable#LinearLayout_gravity
     */
    public void setIndexGravity(int gravity) {
        mSlideIndex.setGravity(gravity);
    }

    private void initViewListener() {
        mSlideDataHome.setOnItemClickListener(this);
        // mSlideDataHome.setOnItemSelectedListener(this);
        // mSlideDataHome.setOnTouchListener(this);
    }

    /**
     * 初始化HaCycleView的数据&绘制界面
     * 
     * @param items:数据源列表
     * @param loadingView:interface of loading module view
     * @see init(List<T> items,OnItemLoadingView<T> loadingView, boolean startAutoScroll);
     */
    public void init(List<T> items, OnItemLoadingView<T> loadingView) {
        init(items, loadingView, false);
    }

    /**
     * 初始化HaCycleView的数据&绘制界面
     * 
     * @param items:数据源列表
     * @param loadingView:interface of loading module view
     * @param startAutoScroll:是否开启自动滚动；true:开启自动滚动
     * @see init(List<T> items, OnItemLoadingView<T> loadingView, ViewGroup conflictingView, boolean
     *      startAutoScroll)
     */
    public void init(List<T> items, OnItemLoadingView<T> loadingView, boolean startAutoScroll) {
        init(items, loadingView, null, false);
    }

    /**
     * 初始化HaCycleView的数据&绘制界面
     * 
     * @param items:数据源列表
     * @param loadingView:interface of loading module view
     * @param conflictingView:设置事件冲突的控件，滑动事件冲突
     * @see init(List<T> items, OnItemLoadingView<T> loadingView, ViewGroup conflictingView, boolean
     *      startAutoScroll)
     */
    public void init(List<T> items, OnItemLoadingView<T> loadingView, ViewGroup conflictingView) {
        init(items, loadingView, conflictingView, false);
    }

    /**
     * 初始化HaCycleView的数据&绘制界面
     * 
     * @param items:数据源列表
     * @param loadingView:interface of loading module view
     * @param conflictingView:设置事件冲突的控件，滑动事件冲突
     * @param startAutoScroll:是否开启自动滚动；true:开启自动滚动
     */
    public void init(List<T> items, OnItemLoadingView<T> loadingView, ViewGroup conflictingView,
            boolean startAutoScroll) {
        mCycleAdapter = new HaCycleAdapter<T>(items, loadingView);
        mSlideDataHome.setAdapter(mCycleAdapter);
        resetSlideIndex();
        setConflictingView(conflictingView);
        if (startAutoScroll)
            startAutoScroll();
    }

    /**
     * 设置事件冲突的控件:滑动事件冲突
     * 
     * @param conflictingView
     */
    public void setConflictingView(ViewGroup conflictingView) {
        mSlideDataHome.setConflictingView(conflictingView);
    }

    /**
     * 设置底部的数据索引view 根据items的条数，动态向RadioGroup里添加RadioButton
     */
    private void resetSlideIndex() {
        final int itemCount = mCycleAdapter.getRealCount();
        mSlideIndex.removeAllViews();
        LayoutParams params = new LayoutParams(mSlideIndexBtnSize, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, HaScreen.dip2px(getContext(), 2), 0);
        for (int i = 0; i < itemCount; i++) {
            RadioButton rb = new RadioButton(getContext());
            rb.setBackgroundColor(0x00000000);
            rb.setButtonDrawable(R.drawable.cycleview_radiobutton_selector);
            setRdBtnSize(rb);
            rb.setTag(i);
            mSlideIndex.addView(rb, params);
            rb.setSelected(i == 0);
        }
        showFirstItem();
    }

    /**
     * show first item of mSlideDataHome<Gallery>
     * 
     * @param count : size of FSCycleView's data items
     */
    private void showFirstItem() {
        final int count = mCycleAdapter.getRealCount();
        if (count <= 0)
            return;
        final int middle = Integer.MAX_VALUE / 2;
        final int remainder = middle % count;
        final int start = middle - remainder;
        mSlideDataHome.setSelection(start);
    }

    public void onDestory() {
        try {
            stopAutoScroll();
            mSlideDataHome.setAdapter(null);
            mSlideDataHome.removeAllViews();
            mCycleAdapter = null;
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == mSlideDataHome) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stopAutoScroll();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    startAutoScroll();
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    /**
     * start auto scroll data view
     * 
     * @return true : start auto scroll success , false : start auto scroll failure
     */
    public boolean startAutoScroll() {
        return sendAutoScrollMsg(true);
    }

    /**
     * stop auto scroll data view
     */
    public void stopAutoScroll() {
        removeAutoScrollMsg();
    }

    private void removeAutoScrollMsg() {
        mHandler.removeMessages(AUTO_SCROLL);
    }

    /**
     * @param overtime 是否延迟：当触发了手动滑动，触发延迟，展示时间将长点：扩大一倍；
     * @return
     */
    private boolean sendAutoScrollMsg(boolean overtime) {
        if (mHandler.hasMessages(AUTO_SCROLL))
            return true;
        int time = AUTO_SCROLL_TIME;
        if (overtime)
            time = OVERTIME_CARDINAL_NUMBER * time;
        return mHandler.sendEmptyMessageDelayed(AUTO_SCROLL, time);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AUTO_SCROLL:
                    sendAutoScrollMsg(false);
                    try {
                        mSlideDataHome.setSoundEffectsEnabled(false);
                        mSlideDataHome.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                    } catch (Exception e) {
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnCycleItemClickListener == null)
            return;

        try {
            T entity = mCycleAdapter.getItem(position); // 等价于 T entity = (T)
                                                        // parent.getAdapter().getItem(position);
            int pos = getPosition(position);
            mOnCycleItemClickListener.onItemClick(entity, pos);
        } catch (Exception e) {
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        rereshSlideIndex(position);
        onItemSelected(position);
    }

    private void onItemSelected(int position) {
        if (mOnCycleItemSelectedListener == null)
            return;

        try {
            position = getPosition(position);
            mOnCycleItemSelectedListener.onItemSelected(mCycleAdapter.getItem(position), position);
        } catch (Exception e) {
            // Log.e(TAG, "onItemSelected", e);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private int getPosition(int position) throws Exception {
        final int count = mCycleAdapter.getRealCount();
        if (count <= 0)
            throw new Exception("Divisor cannot be zero");
        position = position % count;
        return position;
    }

    /**
     * refresh slideindex
     * 
     * @param position: position of HaCycleView's data items
     */
    private void rereshSlideIndex(int position) {
        try {
            position = getPosition(position);
        } catch (Exception e) {
            // Log.e(TAG, "rereshSlideIndex", e);
            return;
        }

        final int slideIndexChildCount = mSlideIndex.getChildCount();
        if (slideIndexChildCount <= 0)
            return;

        for (int i = 0; i < slideIndexChildCount; i++) {
            RadioButton rb = (RadioButton) mSlideIndex.getChildAt(i);
            rb.setSelected(i == position);
        }
    }

    /**
     * Register a callback to be invoked when an item in this AdapterView has been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mSlideDataHome.setOnItemClickListener(listener);
        mOnCycleItemClickListener = null;
    }

    /**
     * Register a callback to be invoked when an item in this AdapterView has been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(OnCycleItemClickListener<T> listener) {
        mOnCycleItemClickListener = listener;
    }

    public void setOnItemSelectedListener(OnCycleItemSelectedListener<T> listener) {
        mOnCycleItemSelectedListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when an item in this AdapterView has been
     * clicked. define self for user invoked
     */
    public static interface OnCycleItemClickListener<T> {

        /**
         * Callback method to be invoked when an item in this AdapterView has been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need to access the data
         * associated with the selected item.
         * 
         * @param <T>
         * @param t
         */
        public void onItemClick(T t, int position);
    }

    /**
     * Interface definition for a callback to be invoked when an item in this view has been
     * selected.
     */
    public static interface OnCycleItemSelectedListener<T> {
        public void onItemSelected(T entity, int position);
    }

    private static class HaCycleAdapter<T> extends HaBaseAdapter<T> {

        public HaCycleAdapter(List<T> item, OnItemLoadingView<T> loadingView) {
            super(item, loadingView);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        public int getRealCount() {
            return super.getCount();
        }

        @Override
        public T getItem(int position) {
            int realCount = getRealCount();
            if (realCount <= 0)
                return null;
            position = position % realCount;
            return super.getItem(position);
        }
    }

}
