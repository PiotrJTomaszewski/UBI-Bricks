package pl.pjt.ubi_bricks

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.processNextEventInCurrentThread

class SettingsActivity : AppCompatActivity() {

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(settingsToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportFragmentManager.beginTransaction().replace(R.id.settingsFragment, SettingsFragment())
            .commit()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            val urlPref = preferenceManager.findPreference<EditTextPreference>("urlPrefix")
            if (urlPref != null) {
                urlPref.summary = urlPref.text
                urlPref.onPreferenceChangeListener = TextChangeListener()
            }
        }

        class TextChangeListener : Preference.OnPreferenceChangeListener {
            override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
                if (preference != null) {
                    preference.summary = newValue.toString()
                }
                return true
            }

        }
    }
}