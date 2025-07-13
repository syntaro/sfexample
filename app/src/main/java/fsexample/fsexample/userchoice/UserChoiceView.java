package fsexample.fsexample.userchoice;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class UserChoiceView extends RecyclerView {
    public UserChoiceView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public UserChoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public UserChoiceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    UserChoiceElement _rootElement;
    UserChoiceElement _folder;

    public UserChoiceElement getUserChoiceResult() {
        return getAdapter().getUserChoiceResult();
    }

    public void setRootElement(UserChoiceElement rootElement) {
        _rootElement = rootElement;
        getAdapter().notifyDataSetChanged();
    }

    public void setData(ArrayList<UserChoiceElement> list) {
        getAdapter().setData(list);
    }

    private boolean _overwriteMeasure = false;

    public void setNoscrollMyself(boolean overwriteMeasure) {
        _overwriteMeasure = overwriteMeasure;
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setLayoutManager(new LinearLayoutManager(context));
        UserChoiceAdapter _myAdapter = new UserChoiceAdapter();
        setAdapter(_myAdapter);
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

    public void setUserChoiceResult(UserChoiceElement focus) {
        getAdapter().setUserChoiceResult(focus);
    }

    public void setUserChoiceResult(int focus) {
        getAdapter().setUserChoiceResult(focus);
    }

    public UserChoiceAdapter getAdapter() {
        return (UserChoiceAdapter) super.getAdapter();
    }

    public void repaintAdapter() {
        new Handler(Looper.getMainLooper()).post(() -> {
            UserChoiceAdapter a = getAdapter();
            setAdapter(null);
            setAdapter(a);
            a.notifyItemRangeChanged(0, a.getItemCount());
            postInvalidate();
        });
    }
}
