package com.example.photos.ui.main.support

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.photos.MainActivity
import com.example.photos.ui.main.AdditionalViewModel
import com.example.photos.ui.main.InfoFragment
import com.example.photos.ui.main.support.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.powermock.core.classloader.annotations.PrepareForTest

@RunWith(MockitoJUnitRunner::class)
@PrepareForTest(InfoFragment::class)
class IntegrationTestVMRepositoryInfoFragment {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    val idOfPlace = "N3409118776"
    val placeOfInterest = PlaceOfInterest(
        idOfPlace,
        "Истребитель Миг-15",
        address = Address("проспект Дзержинского", "81/1", "", "", "", ""),
        "",
        "",
        "",
        "",
        sources = Sources("", listOf("", "")),
        "",
        infoAboutPlace = InfoAboutPlace(
            "", "", "Идет сбор данных. Присоединяйтесь! https://api.opentripmap.com \n", "", ""
        ),
        "",
        "",
        preview = Preview("", 0, 0),
        textAboutPlace = TextAboutPlace("", ""),
        pointLocation = PointLocation(82.99053955078125, 55.063621520996094)
    )

    @Mock
    val mockRepository = mock<Repository>(Repository::class.java)

    @Test
    fun checkGettingInfoAboutPlaceByViewModel() {
        val viewModel = AdditionalViewModel(mockRepository)
        runBlocking {
            Mockito.`when`(mockRepository.getInformation(idOfPlace)).thenReturn(placeOfInterest)
            viewModel.loadInfoAboutPlace(idOfPlace, Dispatchers.Unconfined)
        }
        val actual = viewModel.infoAboutPlace.nameOfPlace
        val expected = placeOfInterest.nameOfPlace
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun checkInfoFragmentGetInfoFromViewModel() {
        val viewModel = AdditionalViewModel(mockRepository)
        val infoFragment = InfoFragment()

        runBlocking {
            Mockito.`when`(mockRepository.getInformation(idOfPlace)).thenReturn(placeOfInterest)
            viewModel.loadInfoAboutPlace(idOfPlace, Dispatchers.Unconfined)
            val actual = infoFragment.checkInfoAboutPlace(placeOfInterest.xid)
            val expected = placeOfInterest.xid
            Assert.assertEquals(expected, actual)
        }

    }
    @AfterEach
    fun afterEach(){
        Mockito.reset(mockRepository)
    }

}


