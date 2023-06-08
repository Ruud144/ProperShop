package ru.propershop.ui.Users;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import ru.propershop.Utils.CartDatabase;
import ru.propershop.Model.Products;
import ru.propershop.Prevalent.Prevalent;
import ru.propershop.R;
import ru.propershop.ui.LoginActivity;
import ru.propershop.ui.adapters.AdapterGridProduct;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    //    private FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter;
    private AdapterGridProduct adapter;
    DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ImageView viewRecycler, moreBtn;
    ArrayList<Products> productList;
    private boolean isList = true;

    private CartDatabase cartDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        ProductsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList = new ArrayList<>();

                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Products product = productSnapshot.getValue(Products.class);
                    productList.add(product);
                }
                initAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Обработка ошибки, если не удалось получить данные
                productList = new ArrayList<>();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Меню");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Здесь будет переход в корзину", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent loginIntent = new Intent(HomeActivity.this, BasketActivity.class);
                startActivity(loginIntent);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        userNameTextView.setText(Prevalent.currentOnlineUser.getName());
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        moreBtn = findViewById(R.id.more_btn);
        viewRecycler = findViewById(R.id.view_recycler);
        viewRecycler.setOnClickListener(view -> {
            isList = !isList;
            if (!isList) {
                viewRecycler.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_list));
                GridLayoutManager layoutManager1 = new GridLayoutManager(HomeActivity.this, 2);
                recyclerView.setLayoutManager(layoutManager1);
            } else {
                viewRecycler.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_grid));
                recyclerView.setLayoutManager(layoutManager);
            }
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        });
        moreBtn.setOnClickListener(view -> {
            showPopupMenu(view);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        initAdapter(options);
    }

    private void initAdapter() {
        cartDb = new CartDatabase(this);
        adapter = new AdapterGridProduct(HomeActivity.this, productList, cartDb);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

//    private void initAdapter(FirebaseRecyclerOptions<Products> options) {
//        adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull @NotNull ProductViewHolder holder, int i, @NonNull @NotNull Products model) {
//                holder.txtProductName.setText(model.getPname());
//                holder.txtProductDescription.setText(model.getDescription());
//                holder.txtProductPrice.setText("Стоимость = " + model.getPrice() + " рублей");
//                Picasso.get().load(model.getImage()).into(holder.imageView);
//            }
//
//            @NonNull
//            @NotNull
//            @Override
//            public ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
//                ProductViewHolder holder = new ProductViewHolder(view);
//                return holder;
//            }
//        };
//
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_cart) {
            Intent loginIntent = new Intent(HomeActivity.this, BasketActivity.class);
            startActivity(loginIntent);
        } else if (id == R.id.nav_orders) {
            Intent loginIntent = new Intent(HomeActivity.this, OrderListActivity.class);
            loginIntent.putExtra("logicExit", "close");
            startActivity(loginIntent);
        } else if (id == R.id.nav_categories) {
        } else if (id == R.id.nav_settings) {
            Intent loginIntent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(loginIntent);
        } else if (id == R.id.nav_logout) {
            Paper.book().destroy();
            Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    Comparator<Products> priceAscendingComparator = new Comparator<Products>() {
        @Override
        public int compare(Products p1, Products p2) {
            return Double.compare(p1.getPrice(), p2.getPrice());
        }
    };

    Comparator<Products> priceDescendingComparator = new Comparator<Products>() {
        @Override
        public int compare(Products p1, Products p2) {
            return Double.compare(p2.getPrice(), p1.getPrice());
        }
    };

    Comparator<Products> nameAscendingComparator = new Comparator<Products>() {
        @Override
        public int compare(Products p1, Products p2) {
            return p1.getPname().compareToIgnoreCase(p2.getPname());
        }
    };

    Comparator<Products> nameDescendingComparator = new Comparator<Products>() {
        @Override
        public int compare(Products p1, Products p2) {
            return p2.getPname().compareToIgnoreCase(p1.getPname());
        }
    };

    Comparator<Products> stockAscendingComparator = new Comparator<Products>() {
        @Override
        public int compare(Products p1, Products p2) {
            return Boolean.compare(p2.getInStock(), p1.getInStock());
        }
    };

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_more, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.price_plus) {
                Collections.sort(productList, priceAscendingComparator);
                adapter.notifyDataSetChanged();
                return true;
            }
            if (itemId == R.id.price_minus) {
                Collections.sort(productList, priceDescendingComparator);
                adapter.notifyDataSetChanged();
                return true;
            }
            if (itemId == R.id.name_plus) {
                Collections.sort(productList, nameAscendingComparator);
                adapter.notifyDataSetChanged();
                return true;
            }
            if (itemId == R.id.name_minus) {
                Collections.sort(productList, nameDescendingComparator);
                adapter.notifyDataSetChanged();
                return true;
            }
            if (itemId == R.id.in_stock) {
                Collections.sort(productList, stockAscendingComparator);
                adapter.notifyDataSetChanged();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
}