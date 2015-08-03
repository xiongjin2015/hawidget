
package com.haha.hwidget.adapter;

import java.util.List;

/**
 * 刷新适配器data的数据
 * 
 * @author xj
 * @param <T>
 */
public interface HaRefreshAdapterCallBack<T> {

    public void add(T t);

    public void add(T t, int position);

    public void addAll(List<T> list);

    public void addAll(List<T> list, int position);

    public void addCurrentAll(List<Current<T>> list);

    public void remove(T t);

    public void remove(int position);

    public void removeCurrentAll(List<Current<T>> list);

    public void replace(T t, int position);

    public void replaceCurrentAll(List<Current<T>> list);

    public void reload(List<T> list);

    public void clear();

    public static class Current<T> {
        public int position;
        public T t;
    }

}
