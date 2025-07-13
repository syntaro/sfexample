package fsexample.fsexample.watchable;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fsexample.fsexample.R;

public class MXWatchableGroupView extends RecyclerView {
    public MXWatchableGroupView(Context context, MXWatchableGroup group) {
        super(context);
        _group = group;
        _adapter = new MXWatchableGroupAdapter();
        _adapter.setWatchableGroup(_group);
        setColumnCount(4);
        setAdapter(_adapter);
        _adapter.setOnSelectedWatchable(this::hookOnSelectedWatchable);
    }

    MXWatchableGroupAdapter _adapter;
    MXWatchableGroup _group;
    public MXWatchableGroup getWatchableGroup() {
        return _group;
    }

    public void bindListener(MXWatchableGroupAdapter.OnSelectedWatchable listener) {
        _adapter.setOnSelectedWatchable(listener);
    }

    public void unbind() {
        if (_adapter != null) {
            _adapter.setWatchableGroup(null);
        }
    }

    protected void hookOnSelectedWatchable(MXWatchableGroupAdapter adapter, MXWatchable clicked) {
        _adapter.setSelection(clicked);
        if (clicked.isDisableEditor()) {
            return;
        }
        View view = clicked.createCustomEditor(getContext());
        if (view == null) {
            return;
        }
        AlertDialog.Builder builder =  new AlertDialog.Builder(getContext());
        if (clicked.getNameRes() != 0) {
            builder.setTitle(clicked.getNameRes());
        }
        else {
            builder.setTitle(clicked.getNameText());
        }
        builder.setView(view);

        clicked.saveRollbackPoint();
        builder.setPositiveButton(R.string.button_general_ok,(dialogInterface, i) -> {
        });
        builder.setNegativeButton(R.string.button_general_cancel, (dialogInterfface, i) -> {
            clicked.doRollback();
        });
        builder.setOnDismissListener(dialogInterface -> {
        });
        AlertDialog dialog = builder.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    int _columnCount = 1;
    public void setColumnCount(int count) {
        _columnCount = count;
        /*
        LayoutManager man1 = new LinearLayoutManager(getContext());
        setLayoutManager(man1);*/
        GridLayoutManager grid0 = new GridLayoutManager(getContext(), count, GridLayoutManager.VERTICAL, false);
        setLayoutManager(grid0);
    }
    private boolean _overwriteMeasure = false;

    public void setNoscrollMyself(boolean overwriteMeasure) {
        _overwriteMeasure = overwriteMeasure;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (_overwriteMeasure) {
            int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public MXWatchableGroupAdapter getAdapter() {
        return  (MXWatchableGroupAdapter)super.getAdapter();
    }
    public int getColumnCount() {
        return _columnCount;
    }
}
