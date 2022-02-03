package com.libertosforever.telegram.utilits

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.libertosforever.telegram.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_LONG).show()
}

fun AppCompatActivity.replaceActivity(activity: AppCompatActivity) {
    val intent = Intent(this, activity :: class.java)
    startActivity(intent)
    this.finish()
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, addStack: Boolean = true) {
        supportFragmentManager   // Implement later on the acitivities back stack
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_container, fragment)
            .commit()
}

fun Fragment.replaceFragment(fragment: Fragment) {
    this.activity?.supportFragmentManager
        ?.beginTransaction()
        ?.addToBackStack(null)
        ?.replace(R.id.data_container, fragment)
        ?.commit()
}

fun hideKeyboard() {
    val imm: InputMethodManager =
        APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

fun ImageView.downloadAndSetImage(url: String) {
    Picasso.get()
        .load(url)
        .fit()
        .placeholder(R.drawable.default_photo)
        .into(this)

}