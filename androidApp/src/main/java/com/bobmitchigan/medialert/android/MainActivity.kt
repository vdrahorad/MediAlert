package com.bobmitchigan.medialert.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bobmitchigan.medialert.android.ui.CreateMedicineScreen
import com.bobmitchigan.medialert.android.ui.MedicineDetailScreen
import com.bobmitchigan.medialert.domain.InitialDestination
import com.bobmitchigan.medialert.viewModel.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition(::showSplash)
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val nextDestination by viewModel.nextDestination.collectAsState()

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    nextDestination?.let { dest ->
                        NavHost(
                            navController = navController,
                            startDestination = dest.destination()
                        ) {
                            composable(InitialDestination.CreateMedicine.destination()) {
                                CreateMedicineScreen()
                            }
                            composable(
                                InitialDestination.SingleMedicine.detailDestination(),
                                arguments = listOf(navArgument("medicineId") {
                                    nullable = true
                                    type = NavType.StringType;defaultValue =
                                    (dest as? InitialDestination.SingleMedicine)?.medicineId
                                })
                            ) {
                                MedicineDetailScreen(
                                    it.arguments?.getString("medicineId")?.toIntOrNull()
                                )
                            }
                            composable(InitialDestination.MedicineList.destination()) {
                                Text("Medicine list")
                            }
                        }
                    }
                }
            }
        }
    }

    private val showSplash: Boolean
        get() = viewModel.nextDestination.value == null

}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
