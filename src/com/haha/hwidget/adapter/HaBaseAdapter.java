
package com.haha.hwidget.adapter;

import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 适配器基类
 * 
 * @author xj
 * @param <T>
 */
public class HaBaseAdapter<T> extends BaseAdapter implements HaRefreshAdapterCallBack<T> {

    private final int ADD_END = -1;
    private OnItemLoadingView<T> mLoadingItemView;
    protected List<T> mItems;

    public HaBaseAdapter(List<T> items, OnItemLoadingView<T> loadingView) {
        this.mItems = items;
        this.mLoadingItemView = loadingView;
    }

    @Override
    public void add(T t) {
        this.add(t, ADD_END);
    }

    @Override
    public void add(T t, int position) {
        this.addItem(t, position);
        this.notifyDataSetChanged();
    }

    @Override
    public void addAll(List<T> list) {
        this.addAll(list, ADD_END);
    }

    @Override
    public void addAll(List<T> list, int position) {
        this.addItems(list, position);
        this.notifyDataSetChanged();
    }

    private boolean addItems(List<T> items, int position) {
        if (addlast(position))
            return this.mItems.addAll(items);

        return this.mItems.addAll(position, items);
    }

    @Override
    public void addCurrentAll(List<Current<T>> list) {
        if (isEmptyList(list))
            return;

        for (Current<T> item : list)
            this.add(item.t, item.position);

        this.notifyDataSetChanged();
    }

    @Override
    public void remove(T t) {
        this.removeItem(t);
        this.notifyDataSetChanged();
    }

    private void removeItem(T t) {
        this.remove(t);
    }

    @Override
    public void remove(int position) {
        this.removeItem(position);
        this.notifyDataSetChanged();
    }

    @Override
    public void removeCurrentAll(List<Current<T>> list) {
        if (isEmptyList(list))
            return;

        for (Current<T> item : list)
            this.removeItem(item.position);

        this.notifyDataSetChanged();
    }

    @Override
    public void replace(T t, int position) {
        this.replaceItem(t, position);
        this.notifyDataSetChanged();
    }

    @Override
    public void replaceCurrentAll(List<Current<T>> list) {
        if (isEmptyList(list))
            return;

        for (Current<T> item : list)
            this.replaceItem(item.t, item.position);

        this.notifyDataSetChanged();
    }

    private void replaceItem(T t, int position) {
        this.removeItem(position);
        this.addItem(t, position);
    }

    private T removeItem(int position) {
        return this.mItems.remove(position);
    }

    private boolean addItem(T t, int position) {
        if (addlast(position))
            return mItems.add(t);

        mItems.add(position, t);
        return true;
    }

    private boolean addlast(int position) {
        return position == ADD_END || position < 0 || position >= getCount();
    }

    @Override
    public void reload(List<T> list) {
        this.setItems(list);
        this.notifyDataSetChanged();
    }

    private <E> boolean isEmptyList(List<E> list) {
        return list == null || list.isEmpty();
    }

    private void setItems(List<T> list) {
        if (this.mItems != list)
            this.mItems = list;
    }

    @Override
    public void clear() {
        this.mItems.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        Log.i("HaBaseAdapter", "getCount()");
        return this.mItems.size();
    }

    @Override
    public T getItem(int position) {
        Log.i("HaBaseAdapter", "getItem");
        return this.mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        Log.i("HaBaseAdapter", "getView:");
        return this.mLoadingItemView.getView(convertView, getItem(position));
    }

    public static interface OnItemLoadingView<T> {
        public View getView(View convertView, T item);
    }

}
