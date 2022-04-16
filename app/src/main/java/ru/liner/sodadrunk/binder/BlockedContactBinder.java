package ru.liner.sodadrunk.binder;

import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ru.liner.sodadrunk.R;
import ru.liner.sodadrunk.activity.BlockingContactConfigureActivity;
import ru.liner.sodadrunk.genericadapter.Binder;
import ru.liner.sodadrunk.model.BlockedContact;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 15.04.2022, пятница
 **/
public class BlockedContactBinder extends Binder<BlockedContact> {
    private TextView blockedName;

    @Override
    public void init() {
        blockedName = find(R.id.blocked_holder_appName);
    }

    @Override
    public void bind(@NonNull BlockedContact model) {
        blockedName.setText(String.format("%s:%s", model.name, model.number));
        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), BlockingContactConfigureActivity.class);
            intent.putExtra("blockedContact", model);
            intent.putExtra("edit_mode", true);
            itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getDragDirections() {
        return 0;
    }

    @Override
    public int getSwipeDirections() {
        return 0;
    }

    @Override
    public void onCentered(BlockedContact model) {

    }

    @Override
    public void onNotCentered(BlockedContact model) {

    }
}
