package com.pjas.tripplan.Classes.Database.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pjas.tripplan.Classes.Database.Model.Expense
import com.pjas.tripplan.Classes.Variable.GlobalVariables
import com.pjas.tripplan.R

class TripExpenseRecyclerViewAdapter (private val expensesList: MutableList<Expense>, private val context: Context) : RecyclerView.Adapter<TripExpenseRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripExpenseRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.expense_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return expensesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expensesList[position]

        holder!!.description.text = expense.description
        holder!!.cost.text = expense.cost.toString()
        holder!!.type.text = expense.type
        holder!!.shared.text = expense.shared.toString()


        holder.delete.setOnClickListener {
            deleteExpense(expense)
        }
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var description: TextView
        internal var cost: TextView
        internal var type: TextView
        internal var shared: TextView

        internal var delete: Button

        init {
            description = view.findViewById(R.id.tv_ExpenseDescriptionL)
            cost = view.findViewById(R.id.tv_ExpenseCostL)
            type = view.findViewById(R.id.tv_ExpenseTypeL)
            shared = view.findViewById(R.id.tv_ExpenseSharedL)

            delete = view.findViewById(R.id.b_DeleteExpense)
        }
    }

    private fun deleteExpense(expense: Expense) {
        GlobalVariables.expensesList.remove(expense)
        notifyDataSetChanged()
    }
}