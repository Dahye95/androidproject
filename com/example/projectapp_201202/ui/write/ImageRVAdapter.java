package com.example.projectapp_201202.ui.write;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectapp_201202.R;

import java.util.ArrayList;

public class ImageRVAdapter extends RecyclerView.Adapter<ImageRVAdapter.ViewHolder> {

    // ========== fragment에서 사용할 리스너를 위한 interface 정의 ==========
    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }
    // ===================================================================

    String TAG = "ImageRVAdqpter";
    public ArrayList<Uri> mData = null ;

    // ========== 생성자(데이터 리스트 객체를 전달받음) ==========
    ImageRVAdapter(ArrayList<Uri> list) {
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.write_recycler_item, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = mData.get(position);
        holder.imageView.setImageURI(uri);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addItem(Uri uri){
        //items에 Person객체 추가
        mData.add(uri);
        // 추가후 Adapter에 데이터가 변경된것을 알림
        notifyDataSetChanged();
    }

    public int getNum() {
        return  mData.size();
    }

    // ========== 내부 클래스 ViewHolder ==========
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ImageButton imageCancel;

        ViewHolder(View hView) {
            super(hView);
            imageView = hView.findViewById(R.id.write_iv_item);
            imageCancel = hView.findViewById(R.id.write_iv_imageCancel);
            imageCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick : itemcancel : remove : " + getAdapterPosition());
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        // 데이터 리스트로부터 아이템 데이터 참조.
                        mData.remove(pos);
                        notifyDataSetChanged();
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
            /*hView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick : 1");
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 데이터 리스트로부터 아이템 데이터 참조.
                        *//*mData.remove(getAdapterPosition());
                        notifyDataSetChanged();*//*
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });*/
        }
    }
}
