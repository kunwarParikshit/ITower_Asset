import android.content.Context
import android.graphics.Typeface
import android.view.View
import java.io.File
import java.util.*

object FontManager {
    private val fontNames = ArrayList<String>()
    private val typeFaceStore = HashMap<String, Typeface>()

    //Returns the font typeface based on the string passed receiverId it using the typeface store hashmap.
    fun getTypeface(fontName: String): Typeface? {
        var fontName = fontName
        val index = fontNames.indexOf(fontName)
        if (index == -1) {
            fontNames.add(fontName)
        } else {
            fontName = fontNames[index]
        }
        return typeFaceStore[fontName]

    }

    //Sets the typeface or font based on the view and name of typeface passed
    fun setTypeFace(context: Context, view: View, fontName: String) {
        var fontName = fontName
        if (view !is android.widget.TextView) {
            return
        }
        val index = fontNames.indexOf(fontName)
        if (index == -1) {
            fontNames.add(fontName)
        } else {
            fontName = fontNames[index]
        }
        var typeface = typeFaceStore[fontName]
        if (typeface == null) {
            typeface = findTypeface(context, fontName)
            typeFaceStore[fontName] = typeface
        }
        if (typeface != null) {
            view.typeface = typeface
        } else {
            view.typeface = Typeface.DEFAULT
        }
    }

    private fun findTypeface(context: Context,
                             typeface: String): Typeface {
        val assets = context.assets
        return Typeface.createFromAsset(assets, "font" + File.separator + typeface)
    }
}
