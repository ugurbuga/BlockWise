package com.ugurbuga.blockwise.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.serialization.Serializable

internal class AppRootComponent(
    componentContext: ComponentContext = DefaultComponentContext(LifecycleRegistry()),
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()
    private var nextGameSessionKey = 0

    val childStack: Value<ChildStack<Config, Child>> = childStack(
        serializer = Config.serializer(),
        source = navigation,
        initialConfiguration = Config.LevelSelection,
        handleBackButton = false,
        childFactory = ::createChild,
    )

    val canPop: Boolean
        get() = childStack.value.backStack.isNotEmpty()

    fun onBack() {
        if (canPop) {
            navigation.pop()
        }
    }

    fun openRules() {
        navigation.bringToFront(Config.Rules)
    }

    fun openScores() {
        navigation.bringToFront(Config.Scores)
    }

    fun openSettings() {
        navigation.bringToFront(Config.Settings)
    }

    fun openShapesPreview() {
        navigation.bringToFront(Config.ShapesPreview)
    }

    fun openGame() {
        nextGameSessionKey += 1
        navigation.push(Config.Game(sessionKey = nextGameSessionKey))
    }

    fun returnToRoot() {
        navigation.replaceAll(Config.LevelSelection)
    }

    private fun createChild(config: Config, componentContext: ComponentContext): Child {
        return when (config) {
            Config.LevelSelection -> Child.LevelSelection
            is Config.Game -> Child.Game(sessionKey = config.sessionKey)
            Config.Rules -> Child.Rules
            Config.Scores -> Child.Scores
            Config.Settings -> Child.Settings
            Config.ShapesPreview -> Child.ShapesPreview
        }
    }

    sealed interface Child {
        data object LevelSelection : Child
        data class Game(val sessionKey: Int) : Child
        data object Rules : Child
        data object Scores : Child
        data object Settings : Child
        data object ShapesPreview : Child
    }

    @Serializable
    internal sealed interface Config {
        @Serializable
        data object LevelSelection : Config

        @Serializable
        data class Game(val sessionKey: Int) : Config {
            companion object
        }

        @Serializable
        data object Rules : Config

        @Serializable
        data object Scores : Config

        @Serializable
        data object Settings : Config

        @Serializable
        data object ShapesPreview : Config
    }
}

