package xing.dev.alarm_app.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.switchmaterial.SwitchMaterial

@BindingAdapter("imageTint")
fun imageTint(imageView: ImageView, color: Int) {
    imageView.setColorFilter(color)
}

@BindingAdapter("trackTint")
fun trackTint(switchMaterial: SwitchMaterial, color: Int) {
    switchMaterial.trackDrawable.setTint(color)
}