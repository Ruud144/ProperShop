package ru.propershop.ui.Users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import ru.propershop.Model.Order;
import ru.propershop.Model.Products;
import ru.propershop.Prevalent.Prevalent;
import ru.propershop.R;
import ru.propershop.Utils.CartDatabase;
import ru.propershop.Utils.OrderDatabase;
import ru.propershop.ui.adapters.ProductsOrderAdapter;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Spinner spinnerDeliveryMethod;

    private EditText nameEdit, phoneEdit, addressEdit, cardNumberEdit, cardHolderEdit, expiryDateEdit, cvvEdit;
    private Button payBtn;
    private TextView icClose;
    private ProductsOrderAdapter adapter;
    private ArrayList<Products> productList;
    private ProgressDialog loadingBar;

    private CartDatabase cartDb;
    private OrderDatabase orderDb;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        loadingBar = new ProgressDialog(this);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        icClose = findViewById(R.id.close_text_view);
        nameEdit = findViewById(R.id.editTextName);
        phoneEdit = findViewById(R.id.editTextPhone);
        addressEdit = findViewById(R.id.editTextAddress);
        cardNumberEdit = findViewById(R.id.editTextCardNumber);
        cardHolderEdit = findViewById(R.id.editTextCardHolder);
        expiryDateEdit = findViewById(R.id.editTextExpiryDate);
        cvvEdit = findViewById(R.id.editTextCVV);
        spinnerDeliveryMethod = findViewById(R.id.spinnerDeliveryMethod);
        payBtn = findViewById(R.id.place_order_btn);

        cartDb = new CartDatabase(this);
        orderDb = new OrderDatabase(this);

        productList = (ArrayList<Products>) getIntent().getExtras().getSerializable("products");
        int paid = getIntent().getExtras().getInt("paid");
        payBtn.setText("Оплатить (" + paid + " руб.)");
        adapter = new ProductsOrderAdapter(productList);
        recyclerView.setAdapter(adapter);


        setupDeliveryMethodSpinner();
        userInfoDisplay();

        icClose.setOnClickListener(view -> onBackPressed());

        payBtn.setOnClickListener(view -> {
            if (!isValidate()) return;

            loadingBar.setTitle("Оплата продукта");
            loadingBar.setMessage("Подождите, выполняется оплата...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Order order = new Order();
            String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
            order.setId(generateUniqueId());
            order.setDate(date);
            order.setStatus("В процессе");
            orderDb.addOrder(order);

            new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    loadingBar.dismiss();
                    showSuccess();
                }
            }.start();
        });
    }

    public static int generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str = "" + idOne;
        int uid = str.hashCode();
        String filterStr = "" + uid;
        str = filterStr.replaceAll("-", "");
        return Integer.parseInt(str);
    }

    private void showSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setTitle("Успешная покупка");
        builder.setMessage("Ваша покупка была успешно завершена.");
        builder.setPositiveButton("ОК", (dialog, which) -> {
            dialog.dismiss();
            cartDb.deleteAllItems();

            Intent loginIntent = new Intent(OrderActivity.this, OrderListActivity.class);
            loginIntent.putExtra("logicExit", "return");
            startActivity(loginIntent);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Boolean isValidate() {
        if (nameEdit.getText().toString().isEmpty()) {
            onMessage("Введите имя пользователя");
            return false;
        }
        if (phoneEdit.getText().toString().isEmpty()) {
            onMessage("Введите номер телефона");
            return false;
        }
        if (addressEdit.getText().toString().isEmpty()) {
            onMessage("Введите адрес");
            return false;
        }
        if (cardNumberEdit.getText().toString().isEmpty()) {
            onMessage("Введите номер карты");
            return false;
        }
        if (cardHolderEdit.getText().toString().isEmpty()) {
            onMessage("Введите ФИО держателя");
            return false;
        }
        if (expiryDateEdit.getText().toString().isEmpty()) {
            onMessage("Введите Срок");
            return false;
        }
        if (cvvEdit.getText().toString().isEmpty()) {
            onMessage("Введите CVV");
            return false;
        }
        return true;
    }

    private void onMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setupDeliveryMethodSpinner() {
        String[] deliveryMethods = getResources().getStringArray(R.array.delivery_methods);
        ArrayList<String> deliveryMethodList = new ArrayList<>(Arrays.asList(deliveryMethods));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, deliveryMethodList);

        spinnerDeliveryMethod.setAdapter(adapter);
        spinnerDeliveryMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMethod = (String) parent.getItemAtPosition(position);

                if (Objects.equals(selectedMethod, deliveryMethodList.get(0))) {
                    addressEdit.setVisibility(View.GONE);
                } else {
                    addressEdit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Делаем что-то, если не выбран ни один элемент
            }
        });
    }

    private void userInfoDisplay() {
        String phone = Prevalent.currentOnlineUser.getPhone();
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("name").exists()) {
                        String name = snapshot.child("name").getValue().toString();
                        nameEdit.setText(name);
                    }
                    if (snapshot.child("phone").exists()) {
                        String phone = snapshot.child("phone").getValue().toString();
                        phoneEdit.setText(phone);
                    }
                    if (snapshot.child("address").exists()) {
                        String address = snapshot.child("address").getValue().toString();
                        addressEdit.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

}