package com.example.janet_ahn_myruns5

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment


open class StartFragment : Fragment() {
    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView: View = inflater.inflate(R.layout.fragment_start, container, false)

        spinner1 = rootView.findViewById(R.id.spinner1);
        val inputList = resources.getStringArray(R.array.InputTypes);
        val adapter: ArrayAdapter<String> = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, inputList)
        spinner1.adapter = adapter

        spinner2 = rootView.findViewById(R.id.spinner2);
        val activityList = resources.getStringArray(R.array.ActivityTypes);
        val adapter2: ArrayAdapter<String> = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, activityList)
        spinner2.adapter = adapter2
        return rootView
    }

    fun onActivitySaveClick(view: View) {
        val spinner1Pos = spinner1.selectedItemPosition
        val spinner2Pos = spinner2.selectedItemPosition
        if (spinner1Pos == 0) {
            val intent = Intent(this.requireContext(), ManualEntryActivity::class.java)
            intent.putExtra("position1", spinner1Pos)
            intent.putExtra("position2", spinner2Pos)
            startActivity(intent)
        } else if (spinner1Pos == 1) {
            val intent = Intent(this.requireContext(), MapActivity::class.java)
            intent.putExtra("position1", spinner1Pos)
            intent.putExtra("position2", spinner2Pos)
            startActivity(intent)
        } else if (spinner1Pos == 2){
            val intent = Intent(this.requireContext(), MapActivity::class.java)
            intent.putExtra("position1", spinner1Pos)
            intent.putExtra("position2", spinner2Pos)
            startActivity(intent)
        } else {
            throw IllegalStateException("spinner not active")
        }
    }
}