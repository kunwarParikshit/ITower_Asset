import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.google.android.material.snackbar.Snackbar
import infozech.itower.R

object SnackbarUtils {
    private var loadingSnackbar: Snackbar? = null

    @SuppressLint("RestrictedApi")
    fun showLoading(activity: Activity, message: String = "Loading...") {
        if (loadingSnackbar != null) return // Prevent multiple Snackbars

        // Get the root view from the Activity's decor view
        val rootView: View = activity.window.decorView.findViewById(android.R.id.content)

        // Create the Snackbar
        loadingSnackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_INDEFINITE)

        // Customize the Snackbar to include a ProgressBar
        val snackbarLayout = loadingSnackbar!!.view as Snackbar.SnackbarLayout
        snackbarLayout.removeAllViews() // Clear default layout
        snackbarLayout.setPadding(0, 0, 0, 0) // Remove default padding
        snackbarLayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT // Full width

        // Adjust layout parameters to include margins
        val layoutParams = snackbarLayout.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT // Full width
        layoutParams.setMargins(200, 0, 200, 500) // Left, Top, Right, Bottom margins
        snackbarLayout.layoutParams = layoutParams

        snackbarLayout.foregroundGravity = Gravity.CENTER

        // Inflate the custom layout
        val customView = LayoutInflater.from(activity).inflate(R.layout.snackbar_loading, snackbarLayout, false)

        // Add the custom view to the Snackbar
        snackbarLayout.addView(customView)

        // Get the ProgressBar and start animation
        val progressBar = customView.findViewById<ProgressBar>(R.id.progressBar)
        animateProgressBar(progressBar)

        loadingSnackbar!!.show()
    }

    // Animation logic for the progress bar
    private fun animateProgressBar(progressBar: ProgressBar) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animator.duration = 2000 // 2 second for a full cycle
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.start()
    }

    // Hide the loading Snackbar
    fun hideLoading() {
        loadingSnackbar?.dismiss()
        loadingSnackbar = null
    }
}
