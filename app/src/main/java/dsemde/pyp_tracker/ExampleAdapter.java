package dsemde.pyp_tracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private static List<ExampleItem> mExampleList;
    private static List<ExampleItem> exampleListFull;
    private OnItemClickListener mListener;

    private Context mCtx;
    private MainActivityFragment fragment;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    static class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView1;
        ImageView mDeleteImage;


        ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mDeleteImage = itemView.findViewById(R.id.image_delete);

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }

                    }

                }
            });
        }
    }

    ExampleAdapter(List<ExampleItem> exampleList, Context mCtx, MainActivityFragment fragment){
        mExampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
        this.mCtx = mCtx;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ExampleViewHolder holder, final int position) {
        ExampleItem currentItem = mExampleList.get(position);

        if (currentItem.getText2() == 1.0){
            holder.mTextView1.setText(currentItem.getText2() + " step");
        } else {
            holder.mTextView1.setText(currentItem.getText2() + " steps");
        }

        holder.mDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(mCtx, holder.mDeleteImage);
                popup.inflate(R.menu.menu_main);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                fragment.editItem(position);
                                break;
                            case R.id.menu_delete:
                                fragment.removeItem(position);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    void filterList(List<ExampleItem> filteredList){
        mExampleList = filteredList;
        notifyDataSetChanged();
    }


}
