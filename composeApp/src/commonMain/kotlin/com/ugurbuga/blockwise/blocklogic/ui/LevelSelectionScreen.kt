package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.toRules
import org.jetbrains.compose.resources.stringResource

import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.difficulty
import blockwise.composeapp.generated.resources.difficulty_easy
import blockwise.composeapp.generated.resources.difficulty_hard
import blockwise.composeapp.generated.resources.difficulty_normal
import blockwise.composeapp.generated.resources.grid_size
import blockwise.composeapp.generated.resources.grid_size_option
import blockwise.composeapp.generated.resources.level_selection_title
import blockwise.composeapp.generated.resources.rules_rule_1_title
import blockwise.composeapp.generated.resources.rules_rule_1_desc_disabled
import blockwise.composeapp.generated.resources.rules_rule_1_desc_enabled
import blockwise.composeapp.generated.resources.rules_rule_2_title
import blockwise.composeapp.generated.resources.rules_rule_2_desc_disabled
import blockwise.composeapp.generated.resources.rules_rule_2_desc_enabled
import blockwise.composeapp.generated.resources.play
import blockwise.composeapp.generated.resources.rules

@Composable
fun LevelSelectionScreen(
    selectedSize: GridSize,
    selectedDifficulty: Difficulty,
    onSizeSelected: (GridSize) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onOpenRules: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rules = selectedDifficulty.toRules()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(stringResource(Res.string.level_selection_title), style = MaterialTheme.typography.titleLarge)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GridSizeMenu(
                selected = selectedSize,
                onSelected = onSizeSelected,
            )

            DifficultyMenu(
                selected = selectedDifficulty,
                onSelected = onDifficultySelected,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(Res.string.rules_rule_1_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (rules.maxSameColorPerRow == null) {
                    stringResource(Res.string.rules_rule_1_desc_disabled)
                } else {
                    stringResource(Res.string.rules_rule_1_desc_enabled, rules.maxSameColorPerRow)
                },
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(stringResource(Res.string.rules_rule_2_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (rules.maxSameColorPerCol == null) {
                    stringResource(Res.string.rules_rule_2_desc_disabled)
                } else {
                    stringResource(Res.string.rules_rule_2_desc_enabled, rules.maxSameColorPerCol)
                },
                style = MaterialTheme.typography.bodyMedium,
            )

            Button(onClick = onOpenRules) {
                Text(stringResource(Res.string.rules))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.play))
        }
    }
}

@Composable
private fun GridSizeMenu(
    selected: GridSize,
    onSelected: (GridSize) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(stringResource(Res.string.grid_size))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.grid_size_option, selected.value))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf(8, 10, 12).forEach { size ->
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.grid_size_option, size)) },
                    onClick = {
                        expanded = false
                        onSelected(GridSize(size))
                    },
                )
            }
        }
    }
}

@Composable
private fun DifficultyMenu(
    selected: Difficulty,
    onSelected: (Difficulty) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(stringResource(Res.string.difficulty))
            Spacer(Modifier.width(8.dp))
            Text(
                when (selected) {
                    Difficulty.Easy -> stringResource(Res.string.difficulty_easy)
                    Difficulty.Normal -> stringResource(Res.string.difficulty_normal)
                    Difficulty.Hard -> stringResource(Res.string.difficulty_hard)
                }
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.difficulty_easy)) },
                onClick = {
                    expanded = false
                    onSelected(Difficulty.Easy)
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.difficulty_normal)) },
                onClick = {
                    expanded = false
                    onSelected(Difficulty.Normal)
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.difficulty_hard)) },
                onClick = {
                    expanded = false
                    onSelected(Difficulty.Hard)
                },
            )
        }
    }
}
