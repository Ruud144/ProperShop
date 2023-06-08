package ru.propershop.ui.Users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ru.propershop.Utils.CartDatabase;
import ru.propershop.Model.Products;
import ru.propershop.R;
import ru.propershop.ui.adapters.AdapterBasketProduct;

public class BasketActivity extends AppCompatActivity implements AdapterBasketProduct.BasketItemListener {

    private RecyclerView recyclerView;

    private TextView totalAmount, closeBtn;
    private Button placeOrderBtn;
    private AdapterBasketProduct adapter;
    private ArrayList<Products> list;
    private CartDatabase cartDb;

    private int total = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        totalAmount = findViewById(R.id.totalAmountTextView);
        closeBtn = findViewById(R.id.close_text_view);
        placeOrderBtn = findViewById(R.id.place_order_btn);
        recyclerView = findViewById(R.id.listView);
        initAdapter();
        if (list.isEmpty())
            placeOrderBtn.setEnabled(false);
        else
            placeOrderBtn.setEnabled(true);
        placeOrderBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("products", list);
            intent.putExtra("paid", total);
            startActivity(intent);
        });
        closeBtn.setOnClickListener(view -> {

            onBackPressed();
        });

    }

    private void setPrice() {
        total = 0;
        for (Products product : list) {
            total = total + product.getPrice();
        }
        totalAmount.setText("Итого: " + total + " руб.");
    }

    private void initAdapter() {
        cartDb = new CartDatabase(this);
        list = (ArrayList<Products>) cartDb.getCartItems();
        setPrice();
        adapter = new AdapterBasketProduct(this, list, cartDb, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRemoveClicked(Products product) {
        setPrice();
    }
}