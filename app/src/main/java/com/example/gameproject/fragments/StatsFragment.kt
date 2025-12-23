// StatsFragment.kt
package com.example.gameproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.gameproject.R
import com.example.gameproject.data.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val listView = view.findViewById<android.widget.ListView>(R.id.lv_stats)
        val adapter = android.widget.ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_list_item_1
        )
        listView.adapter = adapter

        lifecycleScope.launch {
            db.gameResultDao().getAllResults().collect { results ->
                adapter.clear()                 // clear before add new data
                results.forEachIndexed { index, result ->
                    val seconds = result.completionTime / 1000
                    val millis = result.completionTime % 1000

                    val date = SimpleDateFormat("dd.MM", Locale.getDefault())
                        .format(Date(result.date))  // Convert from Long to Date

                    adapter.add("Игра ${index + 1}: ${seconds}.${millis}сек ($date)")
                }
            }
        }

        view.findViewById<View>(R.id.btn_back_to_menu).setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, MenuFragment())
            }
        }

        view.findViewById<View>(R.id.btn_reset_the_stats).setOnClickListener {
            lifecycleScope.launch {
                db.gameResultDao().deleteAll()
            }
        }
    }
}