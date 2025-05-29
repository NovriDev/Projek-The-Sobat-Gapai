package com.example.the_sobat_gapai.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.the_sobat_gapai.Model.Gift;
import com.example.the_sobat_gapai.R;

import java.util.List;

public class GiftAdapter extends ArrayAdapter<Gift> {

    public GiftAdapter(Context context, List<Gift> giftList) {
        super(context, 0, giftList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gift, parent, false);
        }

        // Ambil data gift
        Gift gift = getItem(position);

        // Temukan view di layout
        ImageView giftImage = convertView.findViewById(R.id.giftImage);
        TextView giftName = convertView.findViewById(R.id.giftName);
        TextView giftPrice = convertView.findViewById(R.id.giftPrice);

        // Set data ke view
        if (gift != null) {
            giftImage.setImageResource(gift.getImageResId()); // Set gambar dari resource
            giftName.setText(gift.getName()); // Set nama gift
            giftPrice.setText(gift.getPrice()); // Set harga gift
        }

        return convertView;
    }
}