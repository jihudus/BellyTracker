package ru.kurant.bellytracker.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.kurant.bellytracker.R;
import ru.kurant.bellytracker.controller.share.StringPatterns;
import ru.kurant.bellytracker.model.Drink;

public class DrinkListRecyclerViewAdapter extends RecyclerView.Adapter<DrinkListRecyclerViewAdapter.DrinkListViewHolder> {

    private DrinkList currentDrinks;

    private ClickExistingDrink clickExistingDrink;

    private int selectedItem;

    public DrinkListRecyclerViewAdapter(DrinkList currentDrinks,
                                        ClickExistingDrink clickExistingDrink) {
        this.currentDrinks = currentDrinks;
        this.clickExistingDrink = clickExistingDrink;
    }

    @NonNull
    @Override
    public DrinkListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View drinkListItemLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_drinklist_main, parent, false);
        return new DrinkListViewHolder(drinkListItemLayout, clickExistingDrink);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkListViewHolder holder, int position) {
        setDrinkFields (holder, currentDrinks.getDrinkList().get(position));
        holder.id = position;
    }

    private void setDrinkFields(DrinkListViewHolder holder, Drink drink) {

        holder.timeFieldView.setText(StringPatterns.convertTimeForUser(drink.getDrinkTime()).split(" ")[1]);
        holder.dateFieldView.setText(StringPatterns.convertTimeForUser(drink.getDrinkTime()).split(" ")[0]);
        holder.typeFieldView.setText(currentDrinks.getDrinkTypeById(drink.getDrinkType()).getDrinkName());
        holder.volumeFieldView.setText(Double.toString(drink.getVolume()));
    }

    @Override
    public int getItemCount() {
        return currentDrinks == null ? 0 : currentDrinks.getDrinkList().size();
    }

    public int getSelectedItem () {
        return selectedItem;
    }

    /*  ClickListener interface that rules ViewHolder's onClick methods. Fragment should realize methods.

    *   Интерфейс управления кликом на элементе списка. Реализуется во фрагменте. */

    public interface ClickExistingDrink {
        void clickDrink ();
        void longClickDrink ();
    }

    public class DrinkListViewHolder extends RecyclerView.ViewHolder {

        TextView dateFieldView;
        TextView timeFieldView;
        TextView typeFieldView;
        TextView volumeFieldView;

        /*  All fields are inside LinearLayout that will be clickable
        *   Кликать будем на этот объект, все текстовые поля содержатся в нем */
        View layoutView;

        int id;

        public DrinkListViewHolder(@NonNull final View itemView, ClickExistingDrink clickExistingDrink) {
            super(itemView);
            dateFieldView = itemView.findViewById(R.id.dateField);
            timeFieldView = itemView.findViewById(R.id.timeField);
            typeFieldView = itemView.findViewById(R.id.typeField);
            volumeFieldView = itemView.findViewById(R.id.volumeField);
            layoutView = itemView.findViewById(R.id.drinkListItemView);
            layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItem = id;
                    clickExistingDrink.clickDrink();
                }
            });
            layoutView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    selectedItem = id;
                    clickExistingDrink.longClickDrink();
                    return true;
                }
            });
        }
    }
}
