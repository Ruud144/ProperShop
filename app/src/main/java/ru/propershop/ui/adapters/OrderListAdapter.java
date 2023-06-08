package ru.propershop.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.propershop.Model.Order;
import ru.propershop.Model.Products;
import ru.propershop.R;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder> {

    private ArrayList<Order> ordersList;

    public OrderListAdapter(ArrayList<Order> ordersList) {
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_list, parent, false);
        return new OrderListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder holder, int position) {
        Order order = ordersList.get(position);
        holder.textViewOrderNumber.setText("Заказ #" + order.getId());
        holder.textViewOrderDate.setText("Дата: " + order.getDate());
        holder.textViewOrderStatus.setText("Статус: " + order.getStatus());
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class OrderListViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderNumber, textViewOrderDate, textViewOrderStatus;

        public OrderListViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderNumber = itemView.findViewById(R.id.textViewOrderNumber);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
            textViewOrderStatus = itemView.findViewById(R.id.textViewOrderStatus);
        }
    }
}
