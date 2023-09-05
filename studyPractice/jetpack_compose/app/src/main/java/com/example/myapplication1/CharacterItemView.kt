package com.example.myapplication1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication1.ui.theme.Grey500
import com.example.myapplication1.ui.theme.Grey900
import com.example.myapplication1.ui.theme.Shapes


@Composable
fun CharacterView(character: Character, onClick:(Character) -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(0.dp, Grey900, shape = RoundedCornerShape(10.dp))
            .padding(6.dp)
            .background(Grey500, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .clickable { onClick(character) }

    ) {
        GlideImageWithPreview(
            data = character.image,
        )
        Column(modifier = Modifier
            .padding(start = 20.dp)
            .fillMaxWidth()){
            Text(text = character.name,
            fontWeight = FontWeight.Bold,
                color = com.example.myapplication1.ui.theme.White,
                fontSize = 18.sp)
            Row(horizontalArrangement = Arrangement.Start,
                modifier = Modifier

                ) {
               val circleColor = when (character.status){
                    "Alive" -> com.example.myapplication1.ui.theme.Green300
                   "Dead" -> com.example.myapplication1.ui.theme.Red300
                   else -> com.example.myapplication1.ui.theme.Grey300
                }
                Circle(circleColor)

                Text(text = stringResource(R.string.alive_or_not_type, character.status, character.species),
                    modifier = Modifier.padding(start = 6.dp),
                    fontSize = 15.sp,

                    color = com.example.myapplication1.ui.theme.White,
                )
            }
            Text(text = stringResource(R.string.location),
                fontSize = 10.sp,
                color = com.example.myapplication1.ui.theme.Grey200,
                )
           Text(text = character.location.name,
            fontSize = 15.sp,
               color = com.example.myapplication1.ui.theme.White,
            )

        }
    }
}

@Composable
fun GlideImageWithPreview(data: Any ) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(R.string.description),
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .clip(Shapes.medium)
            .size(136.dp)
    )
}
@Composable
fun Circle(color: Color){
    Column(modifier = Modifier
        .padding(top = 5.dp)
        .wrapContentSize()) {
        Box(
            modifier = Modifier
                .size(13.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}
@Composable
fun EpisodeView( episode: Episode){

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(0.dp, Grey900, shape = RoundedCornerShape(0.dp))
            .padding(6.dp)
            .background(
                Grey500, RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 10.dp,
                    bottomEnd = 10.dp,
                    bottomStart = 0.dp
                )
            )
            .fillMaxWidth()

    ) {
        Column(modifier = Modifier
            .padding(start = 20.dp)
            .fillMaxWidth()) {
            Text(text = episode.name,
                modifier = Modifier,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        Text(text = episode.air_date,
            fontSize = 10.sp,
            color = Color.White,
        )
        }
        Spacer(Modifier.weight(0.5f))
        Text(text = episode.episodeCode,
            fontSize = 10.sp,
            color = Color.White,
        )
    }
}


