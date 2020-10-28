package com.florian.numberslight.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.florian.numberslight.R;
import com.florian.numberslight.enums.NetworkError;
import com.florian.numberslight.adpater.NumberAdapter;
import com.florian.numberslight.databinding.FragmentNumberListBinding;
import com.florian.numberslight.model.INumber;
import com.florian.numberslight.viewModel.NumberDetailsViewModel;
import com.florian.numberslight.viewModel.NumberListViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NumberListFragment extends Fragment implements NumberAdapter.OnItemClickedListener {

    public NumberListFragment() {
    }

    private FragmentNumberListBinding binding = null;

    private NumberAdapter adapter = new NumberAdapter(new ArrayList<>(), this);

    private Context context;

    private NumberListViewModel viewModel;
    private NumberDetailsViewModel numberViewModel;

    private NavController navController = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
        viewModel = new ViewModelProvider(this, new NumberListViewModel.Factory()).get(NumberListViewModel.class);
        numberViewModel = new ViewModelProvider(requireActivity(), new NumberDetailsViewModel.Factory()).get(NumberDetailsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNumberListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fragment fragment = getParentFragment();

        if (fragment instanceof NavHostFragment) {
            navController = ((NavHostFragment) fragment).getNavController();
        }

        context = binding.getRoot().getContext();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        viewModel.getList().observe(getViewLifecycleOwner(), numberSummaries -> {
            adapter.setList(numberSummaries);
            binding.swiperefresh.setRefreshing(false);
        });

        viewModel.getNetworkError().observe(getViewLifecycleOwner(), (error) -> {
            binding.swiperefresh.setRefreshing(false);
            if (error == NetworkError.GENERAL_ERROR) {
                Snackbar.make(binding.getRoot(), R.string.network_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.try_again, (v) -> viewModel.refreshList())
                        .show();
            } else if (error == NetworkError.NO_DATA) {
                Snackbar.make(binding.getRoot(), R.string.no_data_received, Snackbar.LENGTH_LONG)
                        .setAction(R.string.try_again, (v) -> viewModel.refreshList())
                        .show();
            }
        });

        if (navController == null) {
            adapter.setKeepSelection(navController == null);
            numberViewModel.getNumber().observe(getViewLifecycleOwner(), number -> adapter.setSelectedItem(number));
        }

        viewModel.refreshList();

        binding.swiperefresh.setOnRefreshListener(() -> viewModel.refreshList());
    }

    @Override
    public void onItemClicked(@NotNull INumber item) {
        numberViewModel.loadNumber(item);
        if (navController != null) {
            navController.navigate(R.id.action_numberListFragment_to_numberDetailsFragment);
        }
    }
}
