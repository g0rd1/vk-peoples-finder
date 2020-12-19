package ru.g0rd1.peoplesfinder.base.error

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import dagger.android.support.DaggerDialogFragment
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.databinding.FragmentErrorBinding
import javax.inject.Inject

class ErrorDialogFragment : DaggerDialogFragment(), Error.View {

    private var binding: FragmentErrorBinding? = null

    private var retry: (() -> Unit)? = null

    @Inject
    lateinit var presenter: Error.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStyle(STYLE_NO_FRAME, R.style.AppTheme)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = FragmentErrorBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.message?.text = arguments?.getString(MESSAGE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.setView(this)
        presenter.setRetry(retry)
        binding?.retryButton?.setOnClickListener { presenter.onRetryButtonClick() }
        binding?.message?.setOnClickListener { presenter.onNotRetryButtonClick() }
        binding?.root?.setOnClickListener { presenter.onNotRetryButtonClick() }
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun showRetryButton() {
        binding?.retryButton?.visibility = View.VISIBLE
    }

    override fun hideRetryButton() {
        binding?.retryButton?.visibility = View.INVISIBLE
    }

    override fun setRetry(retry: () -> Unit) {
        this.retry = retry
    }

    companion object {

        private const val MESSAGE = "message"

        fun create(message: String): ErrorDialogFragment {
            return ErrorDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(MESSAGE, message)
                }
            }
        }
    }
}