package com.isl.assetManagement.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import infozech.itower.R

class CustomToastMsg {
    companion object {


        // Create the showCustomToast function as a static method
        fun showCustomToast(context: Context, message: String) {
            // Create a new LinearLayout
            val toastLayout = LinearLayout(context)
            toastLayout.orientation = LinearLayout.HORIZONTAL
            toastLayout.setPadding(50, 50, 50, 50)  // Set padding around the toast
            toastLayout.setBackgroundColor(Color.TRANSPARENT)

            // Create a shape with rounded corners programmatically
           /* val radius = 16f // Set the radius of the rounded corners
            val radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius) // All corners rounded equally
            val rectF = RectF(100f, 100f, 100f, 100f) // Required for RoundRectShape, we can leave it as is
            val roundRectShape = RoundRectShape(radii, rectF, null)
            val shapeDrawable = ShapeDrawable(roundRectShape)
            shapeDrawable.paint.color = Color.WHITE // Set the background color to white

            // Set the shape as the background for the toast layout
            toastLayout.background = shapeDrawable*/

            // Create a TextView for the toast message
            val toastTextView = TextView(context)
            toastTextView.text = message
            toastTextView.setTextColor(context.resources.getColor(R.color.white))
            toastTextView.textSize = 15f  // Set the text size
            toastTextView.setPadding(30, 20, 30, 20)  // Padding inside the TextView

            val background = GradientDrawable()
            background.setColor(context.resources.getColor(R.color.transparent))
            background.cornerRadius = 16f // Rounded corners
            toastTextView.background = background


            // Optional: Create an ImageView if you want an icon next to the text
            val toastImageView = ImageView(context)
            toastImageView.setImageResource(android.R.drawable.ic_dialog_info)  // Replace with your custom drawable icon
            toastImageView.setPadding(0, 0, 10, 0)  // Padding between the icon and text

            // Add the ImageView and TextView to the LinearLayout
            //toastLayout.addView(toastImageView)
            toastLayout.addView(toastTextView)

            // Create a Toast and set its custom layout
            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG  // Toast duration
            toast.view = toastLayout  // Set the custom layout as the view for the Toast

            // Position the Toast on the screen (optional)
            toast.setGravity(Gravity.BOTTOM, 0, 150)  // You can adjust the position of the toast

            // Show the Toast
            toast.show()
        }
    }
}
