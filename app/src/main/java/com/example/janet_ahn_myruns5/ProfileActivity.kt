package com.example.janet_ahn_myruns5

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class ProfileActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var tempImageUri: Uri

    private val imageName = "profile_img.jpg"
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    var firstImageBitmap: Bitmap? = null
    private var line: String? = "..."
    private lateinit var myViewModel: MyViewModel
    private val TEXTVIEW_KEY = "textview_key"

    lateinit var nameEdit: EditText
    lateinit var emailEdit: EditText
    lateinit var phoneEdit: EditText
    lateinit var femaleOption: RadioButton
    lateinit var maleOption: RadioButton
    lateinit var classEdit: EditText
    lateinit var majorEdit: EditText

    lateinit var sharedNameValue: String
    lateinit var sharedEmailValue: String
    var sharedPhoneValue: Int = 0
    var sharedFemaleValue: Boolean = false
    var sharedMaleValue: Boolean = false
    lateinit var sharedClassValue: String
    lateinit var sharedMajorValue: String
    lateinit var sharedImagePref: String

    private lateinit var saveButton: Button
    lateinit var sp: SharedPreferences

    private val myPREFERENCES = "MyPrefs"
    private val name = "nameKey"
    private val email = "emailKey"
    private val phone = "phoneKey"
    private val female = "femaleKey"
    private val male = "maleKey"
    private val classKey = "classKey"
    private val major = "majorKey"
    private val imagePref = "imagePreference"

    private lateinit var imageFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imageView = findViewById(R.id.imageProfile)
        savedPreferences()
        Util.checkPermissions(this)

        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        myViewModel.userImage.observe(this) {
            firstImageBitmap = it
            imageView.setImageBitmap(it)
        }

        imageFile = File(getExternalFilesDir(null), imageName)
        tempImageUri = FileProvider.getUriForFile(this, "com.example.janet_ahn_myruns5", imageFile)

        if(imageFile.exists()) {
            val imageBitmap = Util.getBitmap(this, tempImageUri)
            imageView.setImageBitmap(imageBitmap)
        }

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK){
                if(it.data?.data == null) {
                    val bitmapImage = Util.getBitmap(this, tempImageUri)
                    imageView.setImageBitmap((bitmapImage))
                } else {
                    val bitmapImage = Util.getBitmap(this, it.data?.data!!)

                    // write to file
                    val outputStream = FileOutputStream(imageFile)
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                    imageView.setImageBitmap(bitmapImage)
                }
            }
        }

        if(savedInstanceState != null)
            line = savedInstanceState.getString(TEXTVIEW_KEY);
    }

    private fun savedPreferences() {
        nameEdit = findViewById(R.id.editName)
        emailEdit = findViewById(R.id.editEmail)
        phoneEdit = findViewById(R.id.editPhone)
        femaleOption = findViewById(R.id.femaleOption)
        maleOption = findViewById(R.id.maleOption)
        classEdit = findViewById(R.id.editClass)
        majorEdit = findViewById(R.id.editMajor)
        saveButton = findViewById(R.id.saveButton)
        sp = getSharedPreferences(myPREFERENCES, Context.MODE_PRIVATE)


        sharedImagePref = sp.getString(imagePref, "").toString()
        var imageBit: Bitmap
        if(!sharedImagePref.equals("")) {
            imageBit = stringToBitmap(sharedImagePref)
            imageView.setImageBitmap(imageBit)
        }
        sharedNameValue = sp.getString(name, "").toString()
        sharedEmailValue = sp.getString(email, "").toString()
        sharedPhoneValue = sp.getInt(phone, 0)
        sharedFemaleValue = sp.getBoolean(female, false)
        sharedMaleValue = sp.getBoolean(male, false)
        sharedClassValue = sp.getString(classKey, "").toString()
        sharedMajorValue = sp.getString(major, "").toString()

        nameEdit.setText(sharedNameValue).toString()
        emailEdit.setText(sharedEmailValue).toString()
        if (sharedPhoneValue == 0) {
            phoneEdit.setText("").toString()
        } else {
            phoneEdit.setText("$sharedPhoneValue").toString()
        }
        femaleOption.isChecked = sharedFemaleValue
        maleOption.isChecked = sharedMaleValue
        classEdit.setText(sharedClassValue).toString()
        majorEdit.setText(sharedMajorValue).toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXTVIEW_KEY, line)
    }

    fun onChangeClick(view: View) {
        val myDialog = ChangeDialogFragment()
        val bundle = Bundle()
        bundle.putInt("change button", 1)
        myDialog.arguments = bundle
        myDialog.show(supportFragmentManager, "pick image dialog")
    }

    fun onCameraClick() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
        cameraResult.launch(intent)
    }

    fun onGalleryClick() {
        val gallery = Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        gallery.type = "image/*"
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
        cameraResult.launch(gallery)
    }

    private fun save(view: View){
        imageView.invalidate()
        val drawable = imageView.drawable.toBitmap()
        val editor = sp.edit()
        val ph = phoneEdit.text.toString()
        editor.putString(name, nameEdit.text.toString())
        editor.putString(email, emailEdit.text.toString())
        if (ph != "") {
            val ph_int = ph.toInt()
            editor.putInt(phone, ph_int)
        }
        editor.putBoolean(female, femaleOption.isChecked)
        editor.putBoolean(male, maleOption.isChecked)
        editor.putString(classKey, classEdit.text.toString())
        editor.putString(major,majorEdit.text.toString())
        editor.putString(imagePref, bitmapToString(drawable))
        Toast.makeText(this@ProfileActivity, "Saved", Toast.LENGTH_SHORT).show()
        editor.apply()
        editor.commit()
    }

    private fun cancel(view: View) {
        val outputStream = FileOutputStream(imageFile)
        stringToBitmap(sharedImagePref).compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        imageView.setImageBitmap(stringToBitmap(sharedImagePref))

        val editor = sp.edit()
        editor.putString(imagePref, sharedImagePref)
        editor.putString(name, sharedNameValue)
        editor.putString(email, sharedEmailValue)
        editor.putInt(phone, sharedPhoneValue)
        editor.putBoolean(female, sharedFemaleValue)
        editor.putBoolean(male, sharedMaleValue)
        editor.putString(classKey, sharedClassValue)
        editor.putString(major, sharedMajorValue)
        Toast.makeText(this@ProfileActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        editor.apply()
        editor.commit()
    }

    fun onSaveClick(view: View){
        save(view)
        this@ProfileActivity.finish()
    }

    fun onCancelClick(view: View){
        cancel(view)
        this@ProfileActivity.finish()
    }

//     change bitmap to string for shared preference storing
    private fun bitmapToString(image: Bitmap): String? {
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byte: ByteArray = outputStream.toByteArray()
        return Base64.encodeToString(byte, Base64.DEFAULT)
    }

    // change string back to bitmap for usage
    private fun stringToBitmap(input: String?): Bitmap {
        val revertString = Base64.decode(input, 0)
        return if (BitmapFactory.decodeByteArray(revertString, 0, revertString.size) == null) {
            BitmapFactory.decodeResource(resources, R.drawable.profile_img);
        } else {
            BitmapFactory.decodeByteArray(revertString, 0, revertString.size)
        }

    }
}