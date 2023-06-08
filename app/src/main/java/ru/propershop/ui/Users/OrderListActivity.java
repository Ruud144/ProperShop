package ru.propershop.ui.Users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import ru.propershop.Model.Order;
import ru.propershop.R;
import ru.propershop.Utils.CartDatabase;
import ru.propershop.Utils.OrderDatabase;
import ru.propershop.ui.adapters.OrderListAdapter;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView closeBtn;
    private Button buttonClearData;
    private OrderListAdapter adapter;
    private ArrayList<Order> orderArrayList;

    private OrderDatabase orderDb;

    private String logicExit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        logicExit = getIntent().getExtras().getString("logicExit");

        recyclerView = findViewById(R.id.listViewOrders);
        closeBtn = findViewById(R.id.close_text_view);
        buttonClearData = findViewById(R.id.buttonClearData);

        orderDb = new OrderDatabase(this);

        orderArrayList = (ArrayList<Order>) orderDb.getAllOrders();

        adapter = new OrderListAdapter(orderArrayList);
        recyclerView.setAdapter(adapter);

        closeBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        buttonClearData.setOnClickListener(view -> {
            orderDb.deleteAllItems();
            orderArrayList.clear();
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onBackPressed() {
        if (Objects.equals(logicExit, "return")) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            Intent intent = new Intent(OrderListActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}