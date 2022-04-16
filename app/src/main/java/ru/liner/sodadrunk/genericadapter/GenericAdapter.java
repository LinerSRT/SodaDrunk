package ru.liner.sodadrunk.genericadapter;

import android.annotation.SuppressLint;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("rawtypes | UnusedReturnValue | unused | ConstantConditions | unchecked")
public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.ViewHolder> {
    private final List<Object> items = new ArrayList<>();
    private final SparseArray<Class> binders = new SparseArray<>();
    private final SparseArray<Class> layoutsToTypes = new SparseArray<>();
    private final Map<Class, Integer> typesToLayouts = new HashMap<>();
    private final Map<Class, Binder> binderHashMap = new HashMap<>();
    private final Map<Class, OnBindedCallback> boundCallbackMap = new HashMap<>();
    private final Set<ViewHolder> viewHolderSet = new HashSet<>();


    public <R extends Binder<T>, T> GenericAdapter register(@LayoutRes int layout, @NonNull Class<T> clazz, @NonNull Class<R> binder) {
        binders.put(layout, binder);
        typesToLayouts.put(clazz, layout);
        layoutsToTypes.put(layout, clazz);
        return this;
    }

    @Nullable
    public <O extends Binder> O getBinder(Class<O> binderClass) {
        if (binderHashMap.containsKey(binderClass))
            return (O) binderHashMap.get(binderClass);
        return null;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Class binderClass = binders.get(viewType);
        if (binderClass != null) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            try {
                Binder binder = (Binder) binderClass.newInstance();
                binder.setItemView(itemView);
                if (!binderHashMap.containsKey(binderClass))
                    binderHashMap.put(binderClass, binder);
                return new ViewHolder(itemView, binder, layoutsToTypes.get(viewType));
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("No binder added for viewType " + viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binder.position = position;
        holder.bind(items.get(position));
        viewHolderSet.add(holder);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        viewHolderSet.remove(holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int indexOf(@NonNull Object o) {
        return items.indexOf(o);
    }

    public void remove(int index) {
        items.remove(index);
        notifyItemChanged(index);
    }

    public void insert(int index, @NonNull Object o) {
        items.add(index, o);
        notifyItemChanged(index);
    }

    public void replace(int index, @NonNull Object o) {
        items.remove(index);
        items.add(index, o);
    }


    @Override
    public int getItemViewType(int position) {
        Object o = items.get(position);
        if (o == null)
            throw new IllegalStateException("Item at index " + position + " is null!");
        if (typesToLayouts.containsKey(o.getClass())) {
            return typesToLayouts.get(o.getClass());
        } else {
            throw new IllegalStateException("Class " + o.getClass().getSimpleName() + " not registered in the adapter");
        }
    }

    public Class getObjectClassType(int position) {
        return layoutsToTypes.get(position);
    }


    public void add(@NonNull Object item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void add(@NonNull Object... items) {
        Collections.addAll(this.items, items);
        notifyDataSetChanged();
    }


    public void add(@NonNull Collection<?> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }


    public void set(@NonNull Object... items) {
        this.items.clear();
        Collections.addAll(this.items, items);
        notifyDataSetChanged();
    }


    public void set(@NonNull Collection<?> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public <S extends Binder<T>, T> void onBinded(@NonNull Class<T> clazz, @NonNull OnBindedCallback<T, S> callback) {
        boundCallbackMap.put(clazz, callback);
    }

    @NonNull
    public <T> T get(int i) {
        return (T) items.get(i);
    }

    public List getItems() {
        return items;
    }

    class ViewHolder<R extends Binder<T>, T> extends RecyclerView.ViewHolder {
        private final R binder;
        @NonNull
        private final Class<T> modelClass;

        ViewHolder(@NonNull View itemView, R binder, @NonNull Class<T> modelClass) {
            super(itemView);
            this.modelClass = modelClass;
            this.binder = binder;
            this.binder.init();
        }

        void bind(T model) {
            binder.bind(model);
            if (boundCallbackMap.containsKey(modelClass)) {
                Objects.requireNonNull(boundCallbackMap.get(modelClass)).itemBound(binder, model);
            }
        }

        public boolean allowDrag() {
            return binder.getDragDirections() != 0;
        }

        public int getDragDirections() {
            return binder.getDragDirections();
        }

        public boolean allowSwipe() {
            return binder.getSwipeDirections() != 0;
        }

        public int getSwipeDirections() {
            return binder.getSwipeDirections();
        }

        public void onCentered(T model) {
            binder.onCentered(model);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
