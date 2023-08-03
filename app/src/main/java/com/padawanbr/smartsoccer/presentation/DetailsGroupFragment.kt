package com.padawanbr.smartsoccer.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.padawanbr.smartsoccer.R
import com.padawanbr.smartsoccer.databinding.FragmentDetailsGroupBinding
import com.padawanbr.smartsoccer.presentation.common.ViewAnimation.init
import com.padawanbr.smartsoccer.presentation.common.ViewAnimation.rotateFab
import com.padawanbr.smartsoccer.presentation.common.ViewAnimation.showIn
import com.padawanbr.smartsoccer.presentation.common.ViewAnimation.showOut
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailsGroupFragment : Fragment() {

    private var _binding: FragmentDetailsGroupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsGroupViewModel by viewModels()

    private val args by navArgs<DetailsGroupFragmentArgs>()

    var isRotate: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDetailsGroupBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.detailsGroupViewArgs?.nome?.let { setToolbarTitle(it) }

        var groupId = args.detailsGroupViewArgs?.id
        viewModel.getGroupById(groupId)

        initFabs()
        configureFabMoreOptions()
        fabAddSoccerPlayerOnClick()

        observeUiState()
    }

    private fun observeUiState() {
        viewModel.state.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                DetailsGroupViewModel.UiState.Error -> {
                    Toast.makeText(
                        context,
                        "DetailsGroupViewModel.UiState.Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                DetailsGroupViewModel.UiState.Loading -> {
                    Toast.makeText(
                        context,
                        "CreateGroupViewModel.UiState.Loading",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is DetailsGroupViewModel.UiState.Success -> {
                    binding.textViewGroupTeamName.text = uiState.grupo.nome
                    binding.textViewGroupDate.text = "dd/mm/yyyy"
                    binding.textViewGroupLocal.text = "-"

                    binding.textViewGameInformationTypeOfCourt.text =
                        uiState.grupo.configuracaoEsporte.tipoEsporte.modalidade
                    val quantidadeMinimaPorTime =
                        uiState.grupo.configuracaoEsporte.quantidadeMinimaPorTime

                    binding.textViewGameInformationConfiguration.text = context?.getString(
                        R.string.game_information_configuration,
                        quantidadeMinimaPorTime.toString(),
                        quantidadeMinimaPorTime.toString()
                    )
                    binding.textViewGameInformationSinglePrice.text = context?.getString(
                        R.string.game_information_single_price,
                        30.00.toString()
                    )

                    binding.textViewGameInformationMonthlyPrice.text = context?.getString(
                        R.string.game_information_monthly_price,
                        80.00.toString()
                    )

                    binding.textViewGameInformationRateAge.text = context?.getString(
                        R.string.game_information_rate_age,
                        20.toString(),
                        60.toString()
                    )

                    val jogadoresDisponiveis = uiState.grupo.jogadoresDisponiveis
                    val jogadoresNoDM = uiState.grupo.jogadoresNoDM

                    binding.textViewSoccerPlayesAvalible.text = jogadoresDisponiveis.toString()
                    binding.textViewSoccerPlayesDm.text = jogadoresNoDM.toString()

                    binding.textViewSoccerPlayesTeamAverage.text =
                        uiState.grupo.mediaJogadores.toString()

                    binding.textViewSoccerPlayesMonthlyWorkers.text = (jogadoresNoDM?.let {
                        jogadoresDisponiveis?.plus(
                            it
                        )
                    }).toString()
                }
            }
        }
    }

    private fun configureFabMoreOptions() {
        binding.fabMoreOptions.setOnClickListener {
            isRotate = rotateFab(it, !isRotate)

            if (isRotate) {
                showFabs()
            } else {
                hideFabs()
            }
        }
    }

    private fun fabAddSoccerPlayerOnClick() {
        binding.fabAddSoccerPlayer.setOnClickListener {
            isRotate = rotateFab(it, !isRotate)
            hideFabs()
            val directions =
                DetailsGroupFragmentDirections.actionDetailsGroupFragmentToSoccerPlayerFragment()
            if (args.detailsGroupViewArgs != null) {
                directions.groupId = args.detailsGroupViewArgs!!.id
            }
            findNavController().navigate(directions)
        }
    }

    private fun initFabs() {
        init(binding.fabAddSoccerPlayer)
        init(binding.textViewAddSoccerPlayer)
        init(binding.fabSeparateTimes)
        init(binding.textViewSeparateTimes)
    }

    private fun setToolbarTitle(title: String) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

    private fun showFabs() {
        showIn(binding.fabAddSoccerPlayer)
        showIn(binding.textViewAddSoccerPlayer)
        showIn(binding.fabSeparateTimes)
        showIn(binding.textViewSeparateTimes)
    }

    private fun hideFabs() {
        showOut(binding.fabAddSoccerPlayer)
        showOut(binding.textViewAddSoccerPlayer)
        showOut(binding.fabSeparateTimes)
        showOut(binding.textViewSeparateTimes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}