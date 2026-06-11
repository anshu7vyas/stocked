package io.github.anshu7vyas.stocked.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

@Serializable
data object AddItemRoute : NavKey

@Serializable
data object AddShoppingItemRoute : NavKey
