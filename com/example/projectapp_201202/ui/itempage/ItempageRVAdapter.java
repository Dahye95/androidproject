package com.example.projectapp_201202.ui.itempage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.misc.AsyncTask;
import com.example.projectapp_201202.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ItempageRVAdapter extends RecyclerView.Adapter<ItempageRVAdapter.ViewHolder> {

    private ArrayList<ItempageItemData> iData = null ;
    private OnItemClickListener mListener = null ; // 리스너 객체 참조를 저장하는 변수

    int deviceWidth, deviceHeight, viewChoice;

    // 생성자(데이터 리스트 객체를 전달받음.)
    ItempageRVAdapter(ArrayList<ItempageItemData> list) {
        this.iData = list ;
    }

    ItempageRVAdapter(ArrayList<ItempageItemData> list, int deviceWidth, int deviceHeight, int viewChoice) {
        this.iData = list ;
        this.deviceWidth = deviceWidth;
        this.deviceHeight = deviceHeight;
        this.viewChoice = viewChoice;
    }

    // ========== viewpager 이미지 클릭 리스너 정의 ==========
    // 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }
    // =====================================================

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ItempageRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.itempage_recycler_item, parent, false) ;
        ItempageRVAdapter.ViewHolder vh = new ItempageRVAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ItempageRVAdapter.ViewHolder holder, int position) {

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    String tempString = iData.get(position).getImageUrl();
                    URL url = new URL(tempString);

                    HttpURLConnection conn = null;
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    return bitmap;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Bitmap bitmap = null;
        try {
            bitmap = (Bitmap)asyncTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(viewChoice == 1) {
            // ItempageActivity에 보여질 이미지파일 resize and crop
            bitmap = cropLeftRight_Rate(bitmap, deviceWidth, deviceHeight);
        } else if (viewChoice == 2) {
            // 클릭시 imageFragment에 보여질 이미지파일 리사이징
            bitmap = resizeBitmap(bitmap, deviceWidth, deviceHeight);
        }

        ItempageRVAdapter.ViewHolder vholder = (ItempageRVAdapter.ViewHolder) holder;
        vholder.onBind(bitmap);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return iData.size();
    }

    // ========== Bitmap을 받아 섬네일 크기로 크롭하는 것.(상품이미지->섬네일) ==========
    public static Bitmap cropLeftRight_Rate(Bitmap bitmap, int width, int height){
        int oriWidth = bitmap.getWidth();   //원본 너비
        int oriHeight = bitmap.getHeight(); //원본 높이
        int newWidth, newHeight;

        // 비율에 맞춰서(viewpager size) 변경
        if(oriWidth <= oriHeight) {
            newWidth = width;
            newHeight = width * oriHeight / oriWidth;
        } else {
            newWidth = width * oriWidth / oriHeight;
            newHeight = width;
        }

        // 이미지파일 리사이징
        Bitmap resize = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        Bitmap newbitmap = cropCenterBitmap(resize,width,width);// 상하or좌우 크롭

        return newbitmap;
    }

    // ========== image.center 기준으로 상하좌우 크롭 ==========
    public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
        if (src == null)
            return null;

        //기본 크기
        int width = src.getWidth();
        int height = src.getHeight();

        //높이, 너비 중 하나의 옵션만 사용할 때
        if (w == -1) w = width;
        if (h == -1) h = height;

        if (width < w && height < h)
            return src;

        int x = 0;
        int y = 0;

        if (width > w) x = (width - w) / 2;
        if (height > h) y = (height - h) / 2;

        int cw = w; // crop width
        int ch = h; // crop height

        if (w > width) cw = width;
        if (h > height) ch = height;

        return Bitmap.createBitmap(src, x, y, cw, ch);//x,y 좌표부터 cw, ch크기의 비트맵을 생성.
    }

    // ========== Bitmap을 받아 Viewpager 클릭시 보여줄 이미지 사이즈 조절 ==========
    public Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        int oriWidth = bitmap.getWidth();   //원본 너비
        int oriHeight = bitmap.getHeight(); //원본 높이
        int deWidth, deHeight;

        //비율에 맞춰서(device size) 변경
        if(oriWidth <= oriHeight) {
            deWidth = height * oriWidth / oriHeight;
            deHeight = height;
        } else {
            deWidth = width;
            deHeight = width * oriHeight / oriWidth;
        }
        //Log.d("TAG", "cropLeftRight_Rate: w=" + deWidth + " h=" + deHeight);
        Bitmap resize = Bitmap.createScaledBitmap(bitmap, deWidth, deHeight, true);
        return resize;
    }

    // ========== 아이템 뷰를 저장하는 뷰홀더 이너 클래스. ==========
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_image;
        private RelativeLayout rl_layout;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            iv_image = itemView.findViewById(R.id.ip_iv_Image);
            rl_layout = itemView.findViewById(R.id.ip_lo_RelativeLayout);
            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

        public void onBind(Bitmap image){
            iv_image.setImageBitmap(image);
        }
    }// ViewHolder class
}// Adapter class
