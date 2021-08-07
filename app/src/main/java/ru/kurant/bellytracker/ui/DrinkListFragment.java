package ru.kurant.bellytracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import ru.kurant.bellytracker.R;
import ru.kurant.bellytracker.controller.DrinkList;
import ru.kurant.bellytracker.controller.DrinkListRecyclerViewAdapter;

public class DrinkListFragment extends Fragment {

    private RecyclerView drinksRecyclerView;
    private FloatingActionButton addNewDrinkBtn;

    private DrinkListRecyclerViewAdapter drinkListRVAdapter;

    private DrinkList drinkList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drink_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drinkList = new DrinkList(getContext());

        initViews(view);
        attachRecyclerViewAdapter(view);
    }

    private void initViews(View view) {

        drinksRecyclerView = view.findViewById(R.id.drink_list_recycler);
        addNewDrinkBtn = view.findViewById(R.id.addNewDrink);
        addNewDrinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDrinkFragment(view);
            }
        });
    }

    private void attachRecyclerViewAdapter(View view) {
        drinksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        drinkListRVAdapter = new DrinkListRecyclerViewAdapter(drinkList,
                new DrinkListRecyclerViewAdapter
                        .ClickExistingDrink() {
                    @Override
                    public void clickDrink() {
                        openEditDrinkFragment(view, drinkList.getDrinkFromList(drinkListRVAdapter.getSelectedItem()).getId());
                    }

                    @Override
                    public void longClickDrink() {
                        PopupMenu popupMenu = new PopupMenu(getContext(),
                                drinksRecyclerView.getChildAt(drinkListRVAdapter.getSelectedItem()));
                        popupMenu.inflate(R.menu.context_menu_main);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.context_new_drink:
                                        openAddDrinkFragment(view);
                                        break;
                                    case R.id.context_delete_drink:
                                        drinkList.removeDrink(drinkList.getDrinkFromList(drinkListRVAdapter.getSelectedItem()));
                                        attachRecyclerViewAdapter(view);
                                        break;
                                    case R.id.context_edit_drink:
                                        openEditDrinkFragment(view, drinkList.getDrinkFromList(drinkListRVAdapter.getSelectedItem()).getId());
                                        break;
                                    case R.id.context_edit_beverage:


                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " +
                                                drinkListRVAdapter.getSelectedItem());
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
        drinksRecyclerView.setAdapter(drinkListRVAdapter);
    }

    private void openAddDrinkFragment(View view) {
        Navigation.findNavController(view).navigate(DrinkListFragmentDirections.actionAddDrink());
    }

    private void openEditDrinkFragment(View view, int id) {
        Navigation.findNavController(view).navigate(DrinkListFragmentDirections.actionAddDrink().setDrinkIdArg(id));
    }
}