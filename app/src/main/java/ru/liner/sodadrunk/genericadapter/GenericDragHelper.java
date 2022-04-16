package ru.liner.sodadrunk.genericadapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("rawtypes | unused")
public class GenericDragHelper extends ItemTouchHelper.Callback {
    private final Callback callback;

    public GenericDragHelper(Callback callback) {
        this.callback = callback;
    }

    public GenericDragHelper attachToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper touchHelper = new ItemTouchHelper(this);
        touchHelper.attachToRecyclerView(recyclerView);
        return this;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof GenericAdapter.ViewHolder) {
            GenericAdapter.ViewHolder holder = (GenericAdapter.ViewHolder) viewHolder;
            return makeMovementFlags((holder.allowDrag()) ? holder.getDragDirections() : 0, (holder.allowSwipe()) ? holder.getSwipeDirections() : 0);
        }
        return 0;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if (viewHolder instanceof GenericAdapter.ViewHolder) {
            GenericAdapter.ViewHolder holder = (GenericAdapter.ViewHolder) viewHolder;
            GenericAdapter.ViewHolder targetHolder = (GenericAdapter.ViewHolder) target;
            if (holder.allowDrag() && targetHolder.allowDrag()) {
                callback.onMoved(holder.getAdapterPosition(), targetHolder.getAdapterPosition());
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof GenericAdapter.ViewHolder) {
            GenericAdapter.ViewHolder holder = (GenericAdapter.ViewHolder) viewHolder;
            if (holder.allowSwipe()) {
                callback.onSwiped(holder.itemView, holder.getAdapterPosition(), direction);
            }
        }
    }


    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null)
            callback.onStartDrag(viewHolder.itemView, viewHolder.getAdapterPosition());
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        callback.onEndDrag(viewHolder.itemView);
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public interface Callback {
        void onStartDrag(View view, int position);

        void onMoved(int from, int to);

        void onEndDrag(View view);

        void onSwiped(View view, int position, int direction);
    }
}
