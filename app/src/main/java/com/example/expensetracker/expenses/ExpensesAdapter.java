package com.example.expensetracker.expenses;

import static com.example.expensetracker.Utils.getFormattedAmount;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.database.Expense;

public class ExpensesAdapter extends ListAdapter<Expense, ExpensesAdapter.ExpenseViewHolder> {

    private OnExpenseItemClickListener listener;

    protected ExpensesAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expense_item, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = getItem(position);
        if (expense == null) return;
        holder.textViewType.setText(expense.getType());
        holder.textViewAmount.setText("Amount: " + getFormattedAmount(expense.getAmount()));
        holder.textViewDate.setText(expense.getDate());
    }

    private static final DiffUtil.ItemCallback<Expense> DIFF_CALLBACK = new DiffUtil.ItemCallback<Expense>() {
        @Override
        public boolean areItemsTheSame(Expense oldItem, Expense newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Expense oldItem, Expense newItem) {
            return oldItem.getId() == newItem.getId() && oldItem.getType().equals(newItem.getType()) && oldItem.getAmount() == newItem.getAmount();
        }
    };

    class ExpenseViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewType;
        private TextView textViewAmount;
        private TextView textViewDate;


        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewType = itemView.findViewById(R.id.textViewType);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }

    public interface OnExpenseItemClickListener {
        void onExpenseItemClick(Expense expense);
    }

    public void setOnItemClickListener(OnExpenseItemClickListener listener) {
        this.listener = listener;
    }
}