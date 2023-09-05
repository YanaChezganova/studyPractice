package com.example.myapplication1.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(topStart=10.dp,
        topEnd=0.dp,
        bottomEnd=0.dp,
        bottomStart=10.dp
    ),
    large = RoundedCornerShape(topStart=10.dp,
        topEnd=10.dp,
        bottomEnd=10.dp,
        bottomStart=10.dp
    ),

)
val CustomShape = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(topStart=0.dp,
        topEnd=10.dp,
        bottomEnd=10.dp,
        bottomStart=0.dp
    ),
    large = RoundedCornerShape(0.dp ),
)
