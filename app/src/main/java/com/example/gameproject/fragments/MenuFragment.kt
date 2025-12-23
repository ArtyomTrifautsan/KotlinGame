package com.example.gameproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.gameproject.R

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btn_start).setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, GameFragment())
                addToBackStack(null)        // for backing
            }
        }

        view.findViewById<View>(R.id.btn_stats).setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, StatsFragment())
                addToBackStack(null)
            }
        }

        view.findViewById<View>(R.id.btn_exit).setOnClickListener {
            requireActivity().finish()
        }
    }
}