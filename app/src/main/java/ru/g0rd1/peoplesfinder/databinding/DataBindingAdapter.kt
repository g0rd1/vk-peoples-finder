package ru.g0rd1.peoplesfinder.databinding

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.g0rd1.peoplesfinder.base.BindingRecyclerViewAdapter


object DataBindingAdapter {

    // TODO Поробовать убрать аннотацию @JvmStatic

    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "placeholderImage", "errorImage"], requireAll = false)
    fun setImageUrl(
        imageView: ImageView,
        url: String?,
        placeholderImage: Int?,
        errorImage: Int?
    ) {
            Picasso.get().load(url).apply {
                placeholderImage?.let { this.placeholder(it) }
                errorImage?.let { this.error(it) }
            }
                .into(imageView)
        }

        @JvmStatic
        @BindingAdapter("srcCompat")
        fun setImageDrawable(view: ImageView, drawable: Drawable) {
            view.setImageDrawable(drawable)
        }

        @JvmStatic
        @BindingAdapter("color")
        fun setProgressBarColor(
            progressBar: ProgressBar,
            colorRes: Int
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar.progressTintList = ColorStateList.valueOf(colorRes)
            } else {
                val progressDrawable = progressBar.progressDrawable.mutate()
                @Suppress("DEPRECATION")
                progressDrawable.setColorFilter(colorRes, PorterDuff.Mode.SRC_IN)
                progressBar.progressDrawable = progressDrawable
            }

        }

    @JvmStatic
    @BindingAdapter("items")
    fun <T> setItems(
        recyclerView: RecyclerView,
        items: List<T>
    ) {
        @Suppress("UNCHECKED_CAST")
        (recyclerView.adapter as BindingRecyclerViewAdapter<*, T>).setItems(items)
    }

    @JvmStatic
    @BindingAdapter("visible")
    fun setVisible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

}