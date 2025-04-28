package com.example.janet_ahn_myruns5

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.util.*

class HistoryFragment : Fragment() {
    private lateinit var entryList: ListView
    private lateinit var entryArray: ArrayList<Entries>
    private lateinit var listAdapter: EntryListAdapter

    private lateinit var database: AppDatabase
    private lateinit var databaseDao: EntriesDao
    private lateinit var repository: EntryRepository
    private lateinit var vm: RoomViewModel
    private lateinit var viewModelFactory: EntryViewModelFactory

    private var inputPosition: Int = 0
    val inputTypeArray: Array<String> = arrayOf("Manual Entry", "GPS", "Automatic")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        var rootView: View = inflater.inflate(R.layout.fragment_history, container, false)
        entryList = rootView.findViewById(R.id.entryList)
        entryArray = ArrayList()
        listAdapter = EntryListAdapter(requireActivity(), entryArray)
        entryList.adapter = listAdapter

        database = AppDatabase.getInstance(requireActivity())
        databaseDao = database.entriesDao()
        repository = EntryRepository(databaseDao)
        viewModelFactory = EntryViewModelFactory(repository)
        vm = ViewModelProvider(requireActivity(), viewModelFactory).get(RoomViewModel::class.java)

        vm.allEntriesLiveData.observe(requireActivity(), Observer { it ->
            listAdapter.replace(it)
            listAdapter.notifyDataSetChanged()
        })

        entryList.setOnItemClickListener { _, _, position, _ ->
            vm.allEntriesLiveData.observe(viewLifecycleOwner, Observer { it ->
                inputPosition = it.get(position).inputType
            })
            if(inputPosition == 0){
                val intent = Intent(this.requireContext(), ActivityDetailsActivity::class.java)
                intent.putExtra("entryList", position)
                startActivity(intent)
            } else if(inputPosition == 1 || inputPosition == 2){
                val intent = Intent(this.requireContext(), HistoryMapActivity::class.java)
                intent.putExtra("entryPosition", position)
                startActivity(intent)
            }

        }
        return rootView
    }

    override fun onResume() {
        super.onResume()
        vm.allEntriesLiveData.observe(requireActivity(), Observer { it ->
            listAdapter.replace(it)
            listAdapter.notifyDataSetChanged()
        })
    }



}