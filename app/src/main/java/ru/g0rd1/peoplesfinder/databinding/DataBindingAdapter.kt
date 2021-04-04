package ru.g0rd1.peoplesfinder.databinding

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BindingRecyclerViewAdapter
import ru.g0rd1.peoplesfinder.common.AppSpinnerAdapter


object DataBindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "placeholderImage", "errorImage"], requireAll = false)
    fun setImageUrl(
        imageView: ImageView,
        url: String?,
        placeholderImage: Int?,
        errorImage: Int?,
    ) {
        Picasso.get().load(url).apply {
            if (placeholderImage != null) {
                this.placeholder(placeholderImage)
            } else {
                this.placeholder(R.drawable.drawable_background)
            }
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
        colorRes: Int,
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
    fun <T> RecyclerView.setItems(items: List<T>?) {
        if (items == null) return
        @Suppress("UNCHECKED_CAST")
        (this.adapter as BindingRecyclerViewAdapter<*, T>).setItems(items)
    }

    // @JvmStatic
    // @BindingAdapter("onItemClick")
    // fun <T> RecyclerView.onItemClick(onItemClick: (item: T) -> Unit) {
    //     (this.adapter as BindingRecyclerViewAdapter<*, T>).setOnItemClickListener(onItemClick)
    // }

    @JvmStatic
    @BindingAdapter("visible")
    fun setVisible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("onRefreshListener")
    fun setOnRefreshListener(
        swipeRefreshLayout: SwipeRefreshLayout,
        onRefreshListener: (() -> Unit)?,
    ) {
        swipeRefreshLayout.setOnRefreshListener {
            onRefreshListener?.invoke()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    @JvmStatic
    @BindingAdapter("items")
    fun Spinner.setItems(items: List<String>?) {
        val adapter = this.adapter
        if (adapter is AppSpinnerAdapter) {
            adapter.clear()
            adapter.addAll(items ?: listOf())
            adapter.notifyDataSetChanged()
        } else {
            /* преобразование в mutable list необходимо, чтобы можно было использовать
            *  adapter.clear(). Иначе ловим Exception */
            this.adapter = AppSpinnerAdapter(context, items?.toMutableList() ?: mutableListOf())
        }
    }

    @JvmStatic
    @BindingAdapter("positionChangeListener")
    fun ViewPager2.setPositionChangeListener(positionChangeListener: (position: Int) -> Unit) {
        this.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    positionChangeListener(position)
                }
            }
        )
    }

    // @JvmStatic
    // @BindingAdapter("items")
    // fun AutoCompleteTextView.setItems(items: List<Any>?) {
    //     if (items != null) {
    //         val arrayAdapter = ArrayAdapter(context, R.layout.simple_spinner_item, items)
    //         setAdapter(arrayAdapter)
    //     }
    // }

}