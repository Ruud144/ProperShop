package ru.propershop.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;

import java.util.HashMap;

import ru.propershop.Model.Users;
import ru.propershop.R;

public class ForgotActivity extends AppCompatActivity {

    Button resetBtn;
    EditText phoneInput, secretInput, passwordInput;
    ToggleSwitch toogle;

    private ProgressDialog loadingBar;

    private String parentDbName = "Users";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        resetBtn = (Button) findViewById(R.id.reset_btn);
        phoneInput = (EditText) findViewById(R.id.phone_input);
        secretInput = (EditText) findViewById(R.id.secret_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        toogle = (ToggleSwitch) findViewById(R.id.toogle);
        loadingBar = new ProgressDialog(this);
        toogle.setCheckedPosition(0);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetAccount();
            }
        });

        toogle.setOnChangeListener(new ToggleSwitch.OnChangeListener() {
            @Override
            public void onToggleSwitchChanged(int position) {
                if (position == 0) parentDbName = "Users";
                else parentDbName = "Admins";
            }
        });
    }

    private void ResetAccount() {
        String phone = phoneInput.getText().toString();
        String secret = secretInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(secret)) {
            Toast.makeText(this, "Введите секретное слово", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите новый пароль", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Сброс пароля");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(phone, secret, password);
        }
    }

    private void ValidateUser(final String phone, final String secret, String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()) {
                    Users usersData = snapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getSecret().equals(secret)) {
                            updateData(usersData, RootRef, password);
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(ForgotActivity.this, "Секретное слово неверно", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(ForgotActivity.this, "Аккаунт с номером " + phone + " не существует", Toast.LENGTH_SHORT).show();
                    Intent registerIntent = new Intent(ForgotActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateData(Users usersData, DatabaseReference RootRef, String password) {
        HashMap<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("phone", usersData.getPhone());
        userDataMap.put("name", usersData.getName());
        userDataMap.put("secret", usersData.getSecret());
        userDataMap.put("password", password);
        RootRef.child(parentDbName).child(usersData.getPhone()).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    Toast.makeText(ForgotActivity.this, "Сброс пароля прошла успешно", Toast.LENGTH_SHORT).show();

                    Intent loginIntent = new Intent(ForgotActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(ForgotActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}