package view.ui

import java.awt.Color

enum class ColorEnum {
    Papaya,
    Wheat,
    Olivine,
    EngOrange,
    ;

    fun toRgbValue() : Color =
        when(this){
            Papaya -> Color(254,238,212)
            Wheat -> Color(232,209,165)
            Olivine -> Color(157,189,137)
            EngOrange -> Color(204,20,0)
        }
}