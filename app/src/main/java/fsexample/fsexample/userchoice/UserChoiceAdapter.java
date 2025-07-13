package fsexample.fsexample.userchoice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import fsexample.fsexample.R;
import fsexample.fsexample.common.Constant;
import fsexample.fsexample.databinding.ViewUserChoiceElementBinding;


public class UserChoiceAdapter extends ListAdapter<UserChoiceElement, UserChoiceAdapter.UserchoiceViewHolder> {
    ArrayList<UserChoiceElement> _list;

    public static final String PAYLOAD_UPDATE_STATE = "ForceUpdate";

    public UserChoiceAdapter() {
        super(new DiffUtil.ItemCallback<UserChoiceElement>() {
            /* for enable Drag & Drop, This logic fine */
            @Override
            public boolean areItemsTheSame(@NonNull UserChoiceElement oldItem, @NonNull UserChoiceElement newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull UserChoiceElement oldItem, @NonNull UserChoiceElement newItem) {
                return oldItem == newItem;
            }
        });
        _list = new ArrayList<>();
    }

    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN /*|ItemTouchHelper.START | ItemTouchHelper.END*/, 0
    ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //askAndRemove(viewHolder.getAdapterPosition());
        }
    });


    public void setData(@NonNull ArrayList<UserChoiceElement> list) {
        _list = list;
        submitList(new ArrayList<>(list));
    }

    public void swap(int from, int to) {
        _list.add(to, _list.remove(from));
        submitList(new ArrayList<>(_list));
    }

    public void askAndRemove(Context context, int target) {
        if (context != null) {
            AlertDialog.Builder builder =  new AlertDialog.Builder(context);
            UserChoiceElement e = _list.get(target);
            builder.setMessage("Remove " + e.getNameText());
            builder.setPositiveButton(R.string.button_remove, (dialog, which) -> {
                _list.remove(target);
                submitList(new ArrayList<>(_list));
                setUserChoiceResult(null);
            });
            builder.setNegativeButton(R.string.button_keep, (dialog, which) -> {
            });
        } else {
            _list.remove(target);
            submitList(new ArrayList<>(_list));
        }
    }

    public static class UserchoiceViewHolder extends RecyclerView.ViewHolder {
        ViewUserChoiceElementBinding _viewBinding;

        public UserchoiceViewHolder(ViewUserChoiceElementBinding binding) {
            super(binding.getRoot());
            _viewBinding = binding;
        }

        public ViewUserChoiceElementBinding getBinding() {
            return _viewBinding;
        }
    }

    @Override
    public UserchoiceViewHolder onCreateViewHolder(@NotNull ViewGroup var1, int viewType) {
        ViewUserChoiceElementBinding _willBind = ViewUserChoiceElementBinding.inflate(LayoutInflater.from(var1.getContext()), null, false);
        return new UserchoiceViewHolder(_willBind);
    }

    /*
        public long getItemId(int position) {
            return position;
        }
    */

    public void setChoicedStyle(UserchoiceViewHolder holder2, boolean focus, boolean choiced) {
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

        /*
        binding.textViewItem2.setTextColor(Color.LTGRAY);
        binding.textViewItem.setTextColor(Color.BLACK);
        if (choiced) {
            binding.textViewItem.setTextColor(0xFFB30037);
        }
        else {
            binding.textViewItem.setTextColor(Color.BLACK);
        }*/

    }

    @Override
    public int getItemViewType(int position) {
        UserChoiceElement item = getItem(position);
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
    public void onBindViewHolder(@NonNull UserchoiceViewHolder holder, int position) {
        try {
            //Log.e(TAG, "onBindViewHolder " + position);
            ViewUserChoiceElementBinding bindingView = holder._viewBinding;

            try {
                UserChoiceElement elem = getItem(position);

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
                        setUserChoiceResult(elem);
                        setChoicedStyle(holder, false, true);
                        view.performClick();
                        if (_onUserChoiceListener != null) {
                            _onUserChoiceListener.onUserChoice(this, elem);
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
                subLabel2 = elem.getSubLabelText();

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
        if (obj == null) {
            return -1;
        }
        for (int i = 0; i < _list.size(); ++i) {
            UserChoiceElement v1 = _list.get(i);
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

    public interface OnChoiceUserListener {
        void onUserChoice(UserChoiceAdapter adapter, UserChoiceElement clicked);
    }

    protected OnChoiceUserListener _onUserChoiceListener = null;

    public void setOnUserChoiceListener(OnChoiceUserListener listener) {
        _onUserChoiceListener = listener;
    }

    public UserChoiceElement getUserChoiceResult() {
        return _focus;
    }

    UserChoiceElement _focus = null;

    public void setUserChoiceResult(UserChoiceElement focus) {
        UserChoiceElement oldFocus = _focus;
        _focus = focus;
        if (focus != oldFocus) {
            new Handler(Looper.getMainLooper()).postAtFrontOfQueue(() -> {
                notifyItemRangeChanged(0, getItemCount());
            });
        }
    }

    public void setUserChoiceResult(int newY) {
        if (newY < 0) {
            setUserChoiceResult(null);
        }
        else {
            setUserChoiceResult(_list.get(newY));
        }
    }


    public void attachDandDhelper(RecyclerView attached) {
        helper.attachToRecyclerView(attached);
    }
}
