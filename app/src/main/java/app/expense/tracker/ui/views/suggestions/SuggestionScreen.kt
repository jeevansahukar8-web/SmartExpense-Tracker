package app.expense.tracker.ui.views.suggestions

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.expense.presentation.viewModels.SuggestionListViewModel
import app.expense.presentation.viewStates.SuggestionListState
import app.expense.tracker.R
import app.expense.tracker.ui.theme.GlassmorphismSurface
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SuggestionsScreen(
    onAddSuggestion: (suggestionId: Long) -> Unit,
    viewModel: SuggestionListViewModel = hiltViewModel()
) {
    val suggestionListState =
        viewModel.getSuggestionListState().collectAsState(initial = SuggestionListState()).value
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.suggestions),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        val smsPermissionState = rememberPermissionState(
            Manifest.permission.READ_SMS
        )

        when (smsPermissionState.status) {
            PermissionStatus.Granted -> {
                if (suggestionListState.dateSuggestionsMap.isEmpty()) {
                    ShowEmptySuggestion()
                } else {
                    ShowSuggestions(
                        suggestionListState = suggestionListState,
                        onAddSuggestion = onAddSuggestion,
                        onDeleteSuggestion = { suggestionId ->
                            coroutineScope.launch {
                                viewModel.deleteSuggestion(suggestionId)
                            }
                        }
                    )
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.sms_permission_needed),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.padding(16.dp))
                    Button(
                        onClick = { smsPermissionState.launchPermissionRequest() },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.request_permission))
                    }
                }
            }
        }
    }
}

@Composable
fun ShowEmptySuggestion() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.empty_suggestions_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ShowSuggestions(
    suggestionListState: SuggestionListState,
    onAddSuggestion: (suggestionId: Long) -> Unit,
    onDeleteSuggestion: (suggestionId: Long) -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    LazyColumn(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(suggestionListState.dateSuggestionsMap.size) { pos ->
            val dateString = suggestionListState.dateSuggestionsMap.keys.toList()[pos]
            val suggestionItems = suggestionListState.dateSuggestionsMap[dateString]

            Text(
                text = dateString,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            suggestionItems?.forEach { suggestionItem ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
                            ),
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = GlassmorphismSurface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = suggestionItem.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = suggestionItem.amount,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onAddSuggestion(suggestionItem.id)
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(
                                    text = stringResource(R.string.add_to_expense),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            TextButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onDeleteSuggestion(suggestionItem.id)
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(
                                    text = stringResource(R.string.ignore),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
