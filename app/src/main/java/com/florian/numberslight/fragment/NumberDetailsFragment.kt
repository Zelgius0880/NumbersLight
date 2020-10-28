package com.florian.numberslight.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.florian.numberslight.BuildConfig
import com.florian.numberslight.R
import com.florian.numberslight.databinding.FragmentNumberDetailsBinding
import com.florian.numberslight.enums.NetworkError
import com.florian.numberslight.viewModel.NumberDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso


class NumberDetailsFragment : Fragment() {

    private var _binding: FragmentNumberDetailsBinding? = null
    private val binding: FragmentNumberDetailsBinding
        get() = _binding!!

    private val viewModel: NumberDetailsViewModel by activityViewModels{NumberDetailsViewModel.Factory()}

    private val navController by lazy { (parentFragment as? NavHostFragment)?.navController }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNumberDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.placeholder.setText(R.string.loading)
            }

            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            binding.placeholder.visibility = if (it) View.VISIBLE else View.GONE
            binding.group.visibility = if (!it) View.VISIBLE else View.GONE

        }

        viewModel.number.observe(viewLifecycleOwner) {
            binding.refresh.visibility = View.VISIBLE

            Picasso.with(context).apply {
                isLoggingEnabled = BuildConfig.LOG_NETWORK
            }
                .load(it.image)
                .into(binding.image)


            binding.text.text = it.text
            binding.name.text = it.name
        }

        if (navController != null) { // single fragment display
            binding.placeholder.visibility = View.GONE
        } else {
            binding.refresh.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }

        binding.refresh.setOnClickListener {
            viewModel.refresh()
        }

        viewModel.networkError.observe(viewLifecycleOwner) {
            when(it) {
                NetworkError.GENERAL_ERROR -> Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.try_again) {viewModel.refresh()}
                    .show()

                NetworkError.NO_DATA -> Snackbar.make(binding.root, R.string.no_data_received, Snackbar.LENGTH_LONG)
                    .setAction(R.string.try_again) {viewModel.refresh()}
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
