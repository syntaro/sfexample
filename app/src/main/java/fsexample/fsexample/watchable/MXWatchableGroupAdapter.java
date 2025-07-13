package fsexample.fsexample.watchable;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import fsexample.fsexample.MainActivity;
import fsexample.fsexample.R;
import fsexample.fsexample.common.Constant;
import fsexample.fsexample.databinding.ViewUserChoiceElementBinding;


public class MXWatchableGroupAdapter extends ListAdapter<MXWatchable, MXWatchableGroupAdapter.MyViewHodler> {
    MXWatchableGroup _list;

    public static final String PAYLOAD_UPDATE_STATE = "ForceUpdate";

    public MXWatchableGroupAdapter() {
        super(new DiffUtil.ItemCallback<MXWatchable>() {
            /* for enable Drag & Drop, This logic fine */
            @Override
            public boolean areItemsTheSame(@NonNull MXWatchable oldItem, @NonNull MXWatchable newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull MXWatchable oldItem, @NonNull MXWatchable newItem) {
                return oldItem == newItem;
            }
        });
        _list = null;
    }

    MXWatchableListener myListener = new MXWatchableListener() {
        @Override
        public void OnWatchableValueChanged(MXWatchableGroup paramSet, MXWatchable param) {
            for (int x = 0; x < _list.size(); ++ x) {
                MXWatchable seek = _list.get(x);
                if (seek instanceof  MXWatchableCategory) {
                    MXWatchableGroup insideGroup = ((MXWatchableCategory) seek).getValue();
                    for (MXWatchable seek2 : insideGroup) {
                        if (seek2 == param) {
                            postItemChange(x, 1);
                            return;
                        }
                    }
                }
                else if (seek == param) {
                    postItemChange(x, 1);
                }
            }
        }
    };

    protected void postItemChange(int x, int length) {
        new Handler(Looper.getMainLooper()).post(() -> {
           notifyItemChanged(x, length);
        });
    }
    public void setWatchableGroup(@NonNull MXWatchableGroup list) {
        if (_list != list) {
            if (_list != null) {
                _list.getListenerList().removeListener(myListener);
            }
        }
        _list = list;
        if (list != null) {
            list.getListenerList().addListner(myListener);
            submitList(new ArrayList<>(list));
        }
        else {
            submitList(new ArrayList<>());
        }
    }

    public MXWatchableGroup getWatchableGroup() {
        return _list;
    }

    public static class MyViewHodler extends RecyclerView.ViewHolder {
        ViewUserChoiceElementBinding _viewBinding;

        public MyViewHodler(ViewUserChoiceElementBinding binding) {
            super(binding.getRoot());
            _viewBinding = binding;
        }

        public ViewUserChoiceElementBinding getBinding() {
            return _viewBinding;
        }
    }

    @Override
    public MyViewHodler onCreateViewHolder(@NotNull ViewGroup var1, int viewType) {
        ViewUserChoiceElementBinding _willBind = ViewUserChoiceElementBinding.inflate(LayoutInflater.from(var1.getContext()), null, false);
        return new MyViewHodler(_willBind);
    }

    public void setChoicedStyle(MyViewHodler holder2, boolean focus, boolean choiced) {
        ViewUserChoiceElementBinding binding = holder2._viewBinding;
        View target = holder2.itemView;
        if (focus) {
            target.setBackgroundResource(R.drawable.rounded_corner_list_cell_reserved);
        } else {
            if (choiced) {
                target.setBackgroundResource(R.drawable.rounded_corner_list_cell_focused);
            } else {
                target.setBackgroundResource(R.drawable.rounded_corner_list_cell);
            }
        }
        target.setFocusable(true);
        target.setFocusableInTouchMode(true);
    }

    @Override
    public int getItemViewType(int position) {
        MXWatchable item = getItem(position);
        if (item == null) {
            return 1;
        }

        String subLabel1 = item.getSubLabelText();
        int subLabel2 = item.getSubLabel();
        if (subLabel1 == null && subLabel2 == 0) {
            return 2;
        }
        return 1;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull MyViewHodler holder, int position) {
        try {
            //Log.e(TAG, "onBindViewHolder " + position);
            ViewUserChoiceElementBinding bindingView = holder._viewBinding;

            try {
                MXWatchable elem = getItem(position);

                TextView t1 = bindingView.textViewItem;
                TextView t2 = bindingView.textViewItem2;

                if (true) {
                    View root = bindingView.getRoot();
                    ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    root.setLayoutParams(param);
                }

                if (elem == null) {
                    t1.setText(R.string.text_dummy);
                    t2.setText(R.string.text_dummy);
                    t2.setVisibility(TextView.INVISIBLE);
                    ViewGroup.LayoutParams param = t2.getLayoutParams();
                    param.height = 0;
                    t2.setLayoutParams(param);
                    setChoicedStyle(holder, false, false);
                    return;
                }
                View frame = bindingView.getRoot();
                frame.setOnTouchListener((view, event) -> {
                    int action = event.getActionMasked();
                    if (action == MotionEvent.ACTION_UP) {
                        setSelection(elem);
                        setChoicedStyle(holder, false, true);
                        view.performClick();
                        if (_onChoice != null) {
                            _onChoice.onUserChoice(this, elem);
                        }
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        setChoicedStyle(holder, true, false);
                    } else if (action == MotionEvent.ACTION_CANCEL) {
                        setChoicedStyle(holder, false, false);
                    }
                    return true;
                });
                int label1 = elem.getNameRes();
                String label2 = elem.getNameText();
                int subLabel1 = elem.getSubLabel();
                String subLabel2 = elem.getSubLabelText();

                boolean selected = (_focus == elem);
                if (elem == null) {
                    //nullは選択不可能
                    selected = false;
                } else {
                    if (!selected && _focus != null && elem != null) {
                        selected = _focus.equals(elem);
                    }
                }

                setChoicedStyle(holder, false, selected);
                if (label2 != null) {
                    t1.setText(label2);
                } else if (label1 != 0) {
                    t1.setText(label1);
                } else {
                    t1.setText("");
                }
                MXWatchableCategory elem2 = null;
                if (elem instanceof MXWatchableCategory) {
                    elem2 = (MXWatchableCategory) elem;
                }
                if (elem2 != null && elem2.getValue().size() > 0) {
                    /*
                    if (subLabel1 == 0 && subLabel2 == null) {
                        CharSequence seq = holder.itemView.getContext().getResources().getText(R.string.prefix_include);
                        subLabel2 = seq + " " + elem2.countChildren();
                    }*/
                } else {
                    subLabel2 = elem.getSubLabelText();
                }

                t1.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_START);
                boolean hasHeight = true;
                if (subLabel2 != null) {
                    t2.setText(subLabel2);
                } else if (subLabel1 != 0) {
                    t2.setText(subLabel1);
                } else {
                    hasHeight = false;
                    t2.setText("");
                }
                t2.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);

                if (hasHeight) {
                    ViewGroup.LayoutParams param = t2.getLayoutParams();
                    param.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    t2.setLayoutParams(param);
                } else {
                    ViewGroup.LayoutParams param = t2.getLayoutParams();
                    param.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    param.height = 1;
                    t2.setLayoutParams(param);
                }
            } catch (Throwable e) { // just for debug
                Log.e(Constant.TAG, e.getMessage(), e);
            }
        } catch (Throwable e) { // just for debug
            Log.e(Constant.TAG, e.getMessage(), e);
        }
    }

    public int indexOf(Object obj) {
        if (obj == null || _list == null) {
            return -1;
        }
        for (int i = 0; i < _list.size(); ++i) {
            MXWatchable v1 = _list.get(i);
            if (v1 == obj) {
                return i;
            }
            /*
            //Integerは上でいけるけど、Longは下の必要がある
            if (v1 != null && obj != null && v1.equals(obj)) {
                return i;
            }*/
        }
        return -1;
    }

    public interface OnSelectedWatchable {
        void onUserChoice(MXWatchableGroupAdapter adapter, MXWatchable clicked);
    }

    protected OnSelectedWatchable _onChoice = null;

    public void setOnSelectedWatchable(OnSelectedWatchable listener) {
        _onChoice = listener;
    }

    public MXWatchable getWatchableResult() {
        return _focus;
    }

    MXWatchable _focus = null;

    public void setSelection(MXWatchable focus) {
        MXWatchable oldFocus = _focus;
        _focus = focus;
        if (focus != oldFocus) {
            int x1 = indexOf(oldFocus);
            if (x1 >= 0) {
                postItemChange(x1, 1);
            }
            int x2 = indexOf(focus);
            if (x2 >= 0) {
                postItemChange(x2, 1);
            }
        }
    }

    public void setSelection(int newY) {
        if (_list != null) {
            if (newY < 0) {
                setSelection(null);
            }
            else {
                setSelection(_list.get(newY));
            }
        }
    }
}
