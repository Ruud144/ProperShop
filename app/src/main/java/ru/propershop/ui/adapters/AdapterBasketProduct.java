package ru.propershop.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.propershop.Utils.CartDatabase;
import ru.propershop.Model.Products;
import ru.propershop.R;

public class AdapterBasketProduct extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Products> list = new ArrayList<>();
    private CartDatabase cartDb;
    private BasketItemListener listener;

    public AdapterBasketProduct(Context context, ArrayList<Products> list, CartDatabase cartDb, BasketItemListener listener) {
        this.context = context;
        this.list = list;
        this.cartDb = cartDb;
        this.listener = listener;
    }

    public void submitList(ArrayList<Products> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_basket_product, parent, false);
        return new BasketProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BasketProductViewHolder hold = (BasketProductViewHolder) holder;
        Products model = list.get(position);

        hold.productName.setText(model.getPname());
        hold.productDescription.setText(model.getDescription());
        hold.productPrice.setText("Цена: " + model.getPrice() + " руб.");
        Picasso.get().load(model.getImage()).into(hold.productImage);

        hold.deleteBtn.setOnClickListener(view -> {
            cartDb.removeItem(model);
            list.remove(position);
            listener.onRemoveClicked(model);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class BasketProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productDescription;
        ImageView productImage;
        Button deleteBtn;

        public BasketProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productNameTextView);
            productPrice = itemView.findViewById(R.id.productPriceTextView);
            productDescription = itemView.findViewById(R.id.productDescriptionTextView);
            productImage = itemView.findViewById(R.id.productImageView);
            deleteBtn = itemView.findViewById(R.id.removeButton);
        }
    }

    public interface BasketItemListener {
        void onRemoveClicked(Products product);
    }
}
