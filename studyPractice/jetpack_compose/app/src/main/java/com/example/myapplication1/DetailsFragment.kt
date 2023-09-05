package com.example.myapplication1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication1.ui.theme.Grey900

class DetailsFragment : Fragment() {
    private val viewModel by viewModels<MainViewModel>()
    private var idCharacter = 1
    private var image = ""
    private var  listOfEpisodes = listOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        idCharacter = arguments?.getInt("id")!!
        image = arguments?.getString("url")!!
        listOfEpisodes = arguments?.getStringArray("episodes")!!.toList()
        viewModel.loadCharacterInfo(idCharacter)
        val listOfNumbersEpisode = mutableListOf<Int>()
        listOfEpisodes.onEach { stringUrl ->
            listOfNumbersEpisode.add(stringUrl.takeLastWhile { it.isDigit() }.toInt())
        }
        viewModel.loadEpisodeInfo(listOfNumbersEpisode)

        return ComposeView(requireContext()).apply {
            setContent {
                setBackgroundColor(resources.getColor(R.color.grey_900))
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(Grey900)
                        .verticalScroll(rememberScrollState())
                )
                {
                    Button(
                        onClick = {
                            parentFragmentManager.commit {
                                replace(R.id.container, CharactersFragment())
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .size(60.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Grey900),
                        ) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_button_back),
                                contentDescription = stringResource(id = R.string.back),
                                modifier = Modifier
                                    .padding(start = 20.dp)
                                    .fillMaxHeight(1f),
                            )
                            Spacer(Modifier.weight(0.1f))
                            TextMediumSize(text = stringResource(id = R.string.details))
                            }
                    }
                    BigImageWithPreview(
                        data = image,
                    )
                    val characterInfo = viewModel.character.collectAsStateWithLifecycle()
                    TextBigSize(text = characterInfo.value.name)
                    LineGradient()
                    Spacer(Modifier.height(10.dp))
                    TextSmallSize(text = stringResource(R.string.live_status))
                    val circleColor = when (characterInfo.value.status) {
                        "Alive" -> com.example.myapplication1.ui.theme.Green300
                        "Dead" -> com.example.myapplication1.ui.theme.Red300
                        else -> com.example.myapplication1.ui.theme.Grey300
                    }
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        Circle(circleColor)
                        TextMediumSize(text = characterInfo.value.status)
                    }
                    Spacer(Modifier.height(10.dp))
                    TextSmallSize(text = stringResource(id = R.string.location))
                    TextMediumSize(text = characterInfo.value.location.name)
                    Spacer(Modifier.height(10.dp))
                    TextSmallSize(text = stringResource(id = R.string.species_and_gender))
                    TextMediumSize(
                        text = stringResource(
                            id = R.string.species_and_gender_value,
                            characterInfo.value.species, characterInfo.value.gender
                        )
                    )
                    Spacer(Modifier.height(10.dp))
                    TextSmallSize(text = stringResource(id = R.string.first_seen_in))
                    TextMediumSize(text = characterInfo.value.origin.name)
                    Spacer(Modifier.height(10.dp))
                    TextBigSize(text = stringResource(id = R.string.episodes))
                    val episodes = viewModel.episodeInfo.collectAsStateWithLifecycle()
                    if (episodes.value.isNotEmpty()) {
                        episodes.value.onEach {
                            EpisodeView(episode = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BigImageWithPreview(data: Any) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(R.string.description),
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(10.dp)
            .size(200.dp)

    )
}

@Composable
fun LineGradient() {
    val gradientBrush = Brush.linearGradient(
        0f to MaterialTheme.colors.background,
        0.8f to Color.Transparent,
        1f to Color.Transparent,
    )
    Column(
        modifier = Modifier
            .padding(top = 3.dp)
            .wrapContentSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(1f)
                .size(2.dp)
                .clip(RectangleShape)
                .background(gradientBrush)
        )
    }
}

@Composable
fun TextBigSize(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(start = 20.dp, bottom = 10.dp, top = 10.dp),
        fontSize = 22.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Start
    )
}

@Composable
fun TextSmallSize(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(start = 20.dp),
        fontSize = 14.sp,
        color = Color.Gray,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Start
    )
}
@Composable
fun TextMediumSize(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(start = 20.dp),
        fontSize = 16.sp,
        color = Color.White,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Start
    )
}



