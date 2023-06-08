package ru.propershop.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.propershop.Utils.CartDatabase;
import ru.propershop.Model.Products;
import ru.propershop.R;
import ru.propershop.ui.Users.OrderActivity;

public class AdapterGridProduct extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Products> list = new ArrayList<>();
    private CartDatabase cartDb;

    public AdapterGridProduct(Context context, ArrayList<Products> list, CartDatabase cartDb) {
        this.context = context;
        this.list = list;
        this.cartDb = cartDb;
    }

    public void submitList(ArrayList<Products> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_items_layout, parent, false);
        return new GridProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GridProductViewHolder hold = (GridProductViewHolder) holder;
        Products model = list.get(position);

        hold.basketBtn.setEnabled(model.getInStock());
        hold.buyBtn.setEnabled(model.getInStock());

        if (model.getInStock()) {
            if (cartDb.isProductExists(model)) {
                hold.basketBtn.setText("Удалить из корзины");
            } else {
                hold.basketBtn.setText("В корзину");
            }
        }

        hold.productName.setText(model.getPname());
        hold.productDescription.setText(model.getDescription());
        hold.productPrice.setText("Цена: " + model.getPrice() + " руб.");
        Picasso.get().load(model.getImage()).into(hold.productImage);

        hold.basketBtn.setOnClickListener(view -> {
            if (!cartDb.isProductExists(model)) {
                cartDb.addItem(model);
                Snackbar.make(view, "Товар добавлен в корзину", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                hold.basketBtn.setText("Удалить из корзины");
            } else {
                cartDb.removeItem(model);
                hold.basketBtn.setText("В корзину");
            }
            notifyDataSetChanged();
        });
        hold.buyBtn.setOnClickListener(view -> {
            ArrayList<Products> products = new ArrayList<>();
            products.add(model);
            Intent intent = new Intent(context, OrderActivity.class);
            intent.putExtra("products", products);
            intent.putExtra("paid", model.getPrice());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class GridProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productDescription;
        ImageView productImage;
        LinearLayout itemV;
        Button basketBtn, buyBtn;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            itemV = itemView.findViewById(R.id.itemV);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productDescription = itemView.findViewById(R.id.product_description);
            productImage = itemView.findViewById(R.id.product_image);
            basketBtn = itemView.findViewById(R.id.basket_btn);
            buyBtn = itemView.findViewById(R.id.buy_btn);
        }
    }
}
