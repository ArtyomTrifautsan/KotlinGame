package com.example.gameproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.gameproject.R
import kotlin.random.Random

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

        // Генерируем случайные данные для демонстрации
        val statsList = mutableListOf<String>()
        for (i in 1..10) {
            val time = Random.nextDouble(30.0, 300.0) // случайное время от 30 до 300 секунд
            val minutes = (time / 60).toInt()
            val seconds = (time % 60).toInt()
            statsList.add("Игра #$i: $minutes мин $seconds сек")
        }

        // Настраиваем ListView
        val listView = view.findViewById<android.widget.ListView>(R.id.lv_stats)
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            statsList
        )
        listView.adapter = adapter

        // Кнопка возврата
        view.findViewById<View>(R.id.btn_back_to_menu).setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, MenuFragment())
            }
        }
    }
}