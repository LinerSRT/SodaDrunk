package ru.liner.sodadrunk.genericadapter;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

@SuppressWarnings("unchecked")
public abstract class Binder<T> {
    public View itemView;
    private final SparseArray<View> viewSparseArray = new SparseArray<>();
    private final int UNSPECIFIED = -1;
    public int position = UNSPECIFIED;

    void setItemView(@NonNull View itemView) {
        this.itemView = itemView;
    }

    public abstract void init();

    public abstract void bind(@NonNull T model);

    protected <V> V find(@IdRes int id) {
        View view = viewSparseArray.get(id);
        if (view == null) {
            view = f(id);
            viewSparseArray.put(id, view);
        }
        return (V) view;
    }

    private View f(@IdRes int id) {
        return itemView.findViewById(id);
    }

    public abstract int getDragDirections();

    public abstract int getSwipeDirections();

    public abstract void onCentered(T model);

    public abstract void onNotCentered(T model);
}
