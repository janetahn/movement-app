package com.example.janet_ahn_myruns5

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class SettingsFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)

        var userPref: Preference? = findPreference(getString(R.string.user_key))
        if (userPref != null) {
            userPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val intent = Intent(this.requireContext(), ProfileActivity::class.java)
                startActivity(intent)
                true
            }
        }

        var website: Preference? = findPreference(getString(R.string.web_key))
        if (website != null) {
            website.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse("https://www.sfu.ca/computing.html")
                startActivity(openURL)
                true
            }
        }

    }
}