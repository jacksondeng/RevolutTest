package jacksondeng.revoluttest.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


@BindingAdapter("bind:imageUrl")
fun loadImage(view: ImageView, imageUrl: String) {
    Glide.with(view.context)
        .asDrawable()
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .load(imageUrl)
        .into(view)
}