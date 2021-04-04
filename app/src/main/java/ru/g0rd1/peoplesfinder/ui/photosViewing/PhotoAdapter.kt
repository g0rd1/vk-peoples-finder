package ru.g0rd1.peoplesfinder.ui.photosViewing

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.g0rd1.peoplesfinder.ui.photosViewing.photo.PhotoFragment

class PhotoAdapter(
    fragmentActivity: FragmentActivity,
    private val photoUrls: List<String>
): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = photoUrls.size

    override fun createFragment(position: Int): Fragment = PhotoFragment.create(photoUrls[position])

}