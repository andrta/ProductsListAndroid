package com.tamboo.productslistandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tamboo.productslistandroid.ui.MainApp // Importa la nostra MainApp
import com.tamboo.productslistandroid.ui.theme.ProductsListAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProductsListAndroidTheme {
                MainApp()
            }
        }
    }
}
