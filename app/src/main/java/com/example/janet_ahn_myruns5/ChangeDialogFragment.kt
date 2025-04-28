package com.example.janet_ahn_myruns5

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class ChangeDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var ret: Dialog

        val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_dialog, null)
        val list = arrayOf(getString(R.string.open_camera),getString(R.string.select_gallery))
        val bundle = arguments
        val dialogId = bundle?.getInt("change button")
        
        if (dialogId == 1) {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(view)
            builder.setTitle(getString(R.string.pick_title))
            ret = builder.create()
        }

        val adapter = ArrayAdapter(this.requireContext(),
            R.layout.listview_item, list)

        val listView: ListView = view.findViewById(R.id.listView)
        listView.adapter = adapter
        listView.divider = null
        listView.dividerHeight = 0

        listView.setOnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                (activity as ProfileActivity).onCameraClick()
                dismiss()
            } else if (position == 1) {
                (activity as ProfileActivity).onGalleryClick()
                dismiss()
            }

        }
        return ret
    }

    override fun onClick(dialog: DialogInterface, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            Toast.makeText(activity, "camera clicked", Toast.LENGTH_LONG).show()
        } else if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "gallery clicked", Toast.LENGTH_LONG).show()
        }
    }
}