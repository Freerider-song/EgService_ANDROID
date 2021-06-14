package com.enernet.eg;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ListViewInfinite extends ListView implements OnScrollListener {

    public Context m_Context;
    public View m_ViewFooter;
    public boolean m_bLoading;
    public boolean m_bNoMoreData=false;
    public ListenerInfinite m_ListenerInfinite;
    public BaseAdapter m_BaseAdapter;

    public ListViewInfinite(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnScrollListener(this);

        this.m_Context = context;
    }

    public ListViewInfinite(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(this);

        this.m_Context = context;
    }

    public ListViewInfinite(Context context) {
        super(context);
        this.setOnScrollListener(this);

        this.m_Context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {

        if (getAdapter() == null) return;
        if (getAdapter().getCount() == 0) return;

   //     Log.i("ListViewInfinite", "onScroll : firstVisibleItemIndex="+firstVisibleItem
   //             +", visibleItemCount="+visibleItemCount+", totalItemCount="+totalItemCount);

        int l = visibleItemCount + firstVisibleItem;

        if (l >= totalItemCount && !m_bLoading && !m_bNoMoreData) {

            this.addFooterView(m_ViewFooter);
            m_bLoading = true;

            m_ListenerInfinite.onNeedLoadData();
        }
    }


    //	1
    public void setLoadingView(int resId) {
        m_ViewFooter = ((LayoutInflater)m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resId, null);		//		footer = (View)inflater.inflate(resId, null);
        this.addFooterView(m_ViewFooter);
    }

    //	2
    public void setListener(ListenerInfinite listener)
    {
        this.m_ListenerInfinite = listener;
    }

    //	3
    public void setAdapter(BaseAdapter adapter) {
        super.setAdapter(adapter);
        this.m_BaseAdapter = adapter;
        this.removeFooterView(m_ViewFooter);
    }

    public void onDataAppended() {
        removeFooterView(m_ViewFooter);
        m_bLoading = false;
        m_BaseAdapter.notifyDataSetChanged();
    }

    //	interface
    public interface ListenerInfinite {
        void onNeedLoadData();
    }

}
