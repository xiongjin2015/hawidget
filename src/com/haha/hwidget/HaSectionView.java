
package com.haha.hwidget;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haha.hwidget.adapter.HaBaseAdapter;
import com.haha.hwidget.adapter.HaBaseAdapter.OnItemLoadingView;
import com.haha.hwidget.adapter.HaRefreshAdapterCallBack.Current;
import com.haha.hwidget.util.Utils;

/**
 * @author xj GridView分块数据，包含head； 该GridView将不能够滑动
 * @param <T>
 */
public class HaSectionView<T> extends LinearLayout implements OnClickListener, OnItemClickListener {

    // base view
    private View mBase = null;
    // title view : show title
    private TextView mTitle = null;
    // more button view : show more view
    private TextView mMore = null;
    // GridView : show data
    private GridView mDataHome = null;
    // GridView adapter
    private SectionAdapter mDataHomeAdapter = null;
    /** The listener that receives notifications when an item is clicked. */
    private OnItemClickListener mOnItemClickListener = null;
    /** The listener that receives notifications when an item is clicked. */
    private OnBlockItemClickListener<T> mOnBlockItemClickListener = null;
    /** The listener that receives notifications when a view has been clicked. */
    private OnHeadClickListener mHeadItemClickListener = null;
    /** GridView列数 */
    private int mNumColumns = 1;
    /** 完整的行显示： */
    private boolean isCompleteRow = false;
    private boolean reuseabule = true;

    public HaSectionView(Context context) {
        super(context);
        initialize(null);
    }

    public HaSectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    @SuppressLint("NewApi")
    public HaSectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        initView();
        initFromAttributes(attrs);
    }

    public boolean isReuseabule() {
        return reuseabule;
    }

    public void reuseabule(boolean reuseabule) {
        this.reuseabule = reuseabule;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_section, this, true);
        mBase = findViewById(R.id.section_base);
        mTitle = (TextView) findViewById(R.id.section_head_title);
        mMore = (TextView) findViewById(R.id.section_head_more);
        mDataHome = (GridView) findViewById(R.id.section_gridView);
        initViewListener();
    }

    /** 初始化属性 */
    private void initFromAttributes(AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray typeds = getContext().obtainStyledAttributes(attrs, R.styleable.HaSectionView);
        if (typeds.hasValue(R.styleable.HaSectionView_title_text)) {
            CharSequence titleText = typeds.getText(R.styleable.HaSectionView_title_text);
            setTitleText(titleText);
        }

        if (typeds.hasValue(R.styleable.HaSectionView_title_textColor)) {
            ColorStateList titleTextColor = typeds
                    .getColorStateList(R.styleable.HaSectionView_title_textColor);
            setTitleTextColor(titleTextColor);
        }

        if (typeds.hasValue(R.styleable.HaSectionView_title_textSize)) {
            float titleTextSize = typeds.getDimension(R.styleable.HaSectionView_title_textSize, -1);
            setTitleTextSize(titleTextSize);
        }

        if (typeds.hasValue(R.styleable.HaSectionView_more_text)) {
            CharSequence moreText = typeds.getText(R.styleable.HaSectionView_more_text);
            setMoreText(moreText);
        }

        if (typeds.hasValue(R.styleable.HaSectionView_more_textColor)) {
            ColorStateList moreTextColor = typeds
                    .getColorStateList(R.styleable.HaSectionView_more_textColor);
            setMoreTextColor(moreTextColor);
        }

        if (typeds.hasValue(R.styleable.HaSectionView_more_textSize)) {
            float moreTextSize = typeds.getDimension(R.styleable.HaSectionView_more_textSize, -1);
            setMoreTextSize(moreTextSize);
        }

        if (typeds.hasValue(R.styleable.HaSectionView_more_src)) {
            // int moreSrc = typeds.getResourceId(R.styleable.FSSectionView_more_src,
            // R.drawable.icon_more);
            // setMoreSrc(moreSrc);
        }

        if (typeds.hasValue(R.styleable.HaSectionView_moreVisibility)) {
            int visibility = typeds.getInt(R.styleable.HaSectionView_moreVisibility, View.VISIBLE);
            visibility = Utils.getRealVisibility(visibility, View.VISIBLE);
            setMoreVisibility(visibility);
        }

        if (typeds.hasValue(R.styleable.HaSectionView_spacing)) {
            int spacing = typeds.getDimensionPixelSize(R.styleable.HaSectionView_spacing, -1);
            setSpacing(spacing);
        }
        typeds.recycle();
    }

    public void setBasePadding(int left, int top, int right, int bottom) {
        this.mBase.setPadding(left, top, right, bottom);
    }

    public int getBasePaddingLeft() {
        return this.mBase.getPaddingLeft();
    }

    public int getBasePaddingTop() {
        return this.mBase.getPaddingTop();
    }

    public int getBasePaddingRight() {
        return this.mBase.getPaddingRight();
    }

    public int getBasePaddingBottom() {
        return this.mBase.getPaddingBottom();
    }

    public void setTitleText(CharSequence titleText) {
        mTitle.setText(titleText);
    }

    public void setTitleText(String titleText) {
        mTitle.setText(titleText);
    }

    public void setTitleText(int textRes) {
        String titleText = getContext().getString(textRes);
        this.setTitleText(titleText);
    }

    @SuppressWarnings("unused")
    private void setTitleTextColor(int titleTextColor) {
        mTitle.setTextColor(titleTextColor);
    }

    private void setTitleTextColor(ColorStateList titleTextColor) {
        mTitle.setTextColor(titleTextColor);
    }

    private void setTitleTextSize(float titleTextSize) {
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
    }

    private void setMoreText(CharSequence moreText) {
        mMore.setText(moreText);
    }

    @SuppressWarnings("unused")
    private void setMoreTextColor(int moreTextColor) {
        mMore.setTextColor(moreTextColor);
    }

    private void setMoreTextColor(ColorStateList moreTextColor) {
        mMore.setTextColor(moreTextColor);
    }

    private void setMoreTextSize(float moreTextSize) {
        mMore.setTextSize(TypedValue.COMPLEX_UNIT_PX, moreTextSize);
    }

    /**
     * Set the enabled state of this view.
     * 
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setMoreVisibility(int visibility) {
        mMore.setVisibility(visibility);
    }

    public void setSpacing(int verticalSpacing) {
        setVerticalSpacing(verticalSpacing);
        setHorizontalSpacing(verticalSpacing);
    }

    public void setVerticalSpacing(int verticalSpacing) {
        mDataHome.setVerticalSpacing(verticalSpacing);
    }

    public void setHorizontalSpacing(int verticalSpacing) {
        mDataHome.setHorizontalSpacing(verticalSpacing);
    }

    public void setNumColumns(int num) {
        mDataHome.setNumColumns(num);
        mNumColumns = num;
    }

    /** 监听 */
    private void initViewListener() {
        mTitle.setOnClickListener(this);
        mMore.setOnClickListener(this);
        findViewById(R.id.section_head_home).setOnClickListener(this);
        ;
        mDataHome.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(parent, view, position, id);
        } else if (mOnBlockItemClickListener != null) {
            mOnBlockItemClickListener.onItemClick(mDataHomeAdapter.getItem(position));
        }
    }

    @Override
    public void onClick(View v) {
        if (mHeadItemClickListener == null)
            return;
        HeadItem item = HeadItem.getItem(v.getId());
        mHeadItemClickListener.onClick(item);
    }

    public void init(List<T> items, OnItemLoadingView<T> loadingView) {
        mDataHomeAdapter = new SectionAdapter(items, loadingView);
        mDataHome.setAdapter(mDataHomeAdapter);
    }

    /**
     * 只显示整行数据时设为true，当为true是将不显示不为整行的部分数据；默认为false
     * 
     * @param isCompleteRow
     */
    public void setCompleteRow(boolean isCompleteRow) {
        this.isCompleteRow = isCompleteRow;
        if (mDataHomeAdapter != null)
            mDataHomeAdapter.notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (mDataHomeAdapter != null)
            mDataHomeAdapter.notifyDataSetChanged();
    }

    public void reload(List<T> items) {
        mDataHomeAdapter.reload(items);
    }

    public void add(T t) {
        mDataHomeAdapter.add(t);
    }

    public void add(T t, int position) {
        mDataHomeAdapter.add(t, position);
    }

    public void addAll(List<T> items) {
        mDataHomeAdapter.addAll(items);
    }

    public void addAll(List<T> items, int position) {
        mDataHomeAdapter.addAll(items, position);
    }

    public void addCurrentAll(List<Current<T>> list) {
        mDataHomeAdapter.addCurrentAll(list);
    }

    public void replace(T t, int position) {
        mDataHomeAdapter.replace(t, position);
    }

    public void replace(List<Current<T>> list) {
        mDataHomeAdapter.replaceCurrentAll(list);
    }

    public int getCount() {
        return mDataHomeAdapter.getRealCount();
    }

    /** 设置item监听，系统item监听接口 */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /** 设置item监听，自定义item监听接口，欲调用该接口不要设置系统监听接口 */
    public void setOnItemClickListener(OnBlockItemClickListener<T> listener) {
        mOnBlockItemClickListener = listener;
    }

    /**
     * Author: 张维亚 创建时间：2014年6月20日 上午11:17:18 修改时间：2014年6月20日 上午11:17:18 Description: 监听item点击
     */
    public static interface OnBlockItemClickListener<T> {

        /**
         * Callback method to be invoked when an item in this AdapterView has been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need to access the data
         * associated with the selected item.
         *
         * @param obj The item that was clicked.
         */
        public void onItemClick(T t);
    }

    public void setOnClickListener(OnHeadClickListener listener) {
        mHeadItemClickListener = listener;
    }

    /**
     * Author: 张维亚 创建时间：2014年6月20日 上午11:17:18 修改时间：2014年6月20日 上午11:17:18 Description: Called when a
     * head view has been clicked
     */
    public static interface OnHeadClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        public void onClick(HeadItem item);
    }

    public static enum HeadItem {
        TITLE(R.id.section_head_title),
        MORE(R.id.section_head_more),
        ALL(R.id.section_head_home),
        NONE(0);
        public int id;

        private HeadItem(int id) {
            this.id = id;
        }

        public static HeadItem getItem(int id) {
            for (HeadItem item : HeadItem.values()) {
                if (item.id == id)
                    return item;
            }
            return NONE;
        }
    }

    public class SectionAdapter extends HaBaseAdapter<T> {

        public SectionAdapter(List<T> items, OnItemLoadingView<T> loadingView) {
            super(items, loadingView);
        }

        public int getRealCount() {
            return super.getCount();
        }

        @Override
        public int getCount() {
            if (isCompleteRow && mNumColumns > 1 && super.getCount() > mNumColumns) {
                int lines = super.getCount() / mNumColumns;
                return lines * mNumColumns;
            } else {
                return super.getCount();
            }
        }
    }
}
