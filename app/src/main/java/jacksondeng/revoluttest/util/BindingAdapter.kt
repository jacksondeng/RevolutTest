package jacksondeng.revoluttest.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import jacksondeng.revoluttest.R

@BindingAdapter("bind:imageUrl")
fun loadImage(view: ImageView, imageUrl: String) {
    Glide.with(view.context)
        .asBitmap()
        .error(R.drawable.ic_monetization_on_black_24dp)
        .apply(RequestOptions.circleCropTransform())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .load(imageUrl)
        .into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {

            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                view.setImageBitmap(resource)
            }

        })
}

@BindingAdapter("bind:showRate")
fun showRate(view: TextView, rate: Double) {
    if (rate == Double.POSITIVE_INFINITY) {
        view.text = view.context.getString(R.string.string_caculation_overflowed)
    } else {
        view.text = rate.toString()
    }
}