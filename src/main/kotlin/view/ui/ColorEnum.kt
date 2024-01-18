package view.ui

import java.awt.Color

/**
 * this class creates an enum to select the color from our chosen color-palette
 */
enum class ColorEnum {
    Papaya,
    Wheat,
    Olivine,
    EngOrange,
    ;

    /**
     * a function to return a Color value for the corresponding value in the enum.
     */
    fun toRgbValue() : Color =
        when(this){
            Papaya -> Color(254,238,212)
            Wheat -> Color(232,209,165)
            Olivine -> Color(157,189,137)
            EngOrange -> Color(204,20,0)
        }
}