package io.github.claudio_santos.weathr.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.claudio_santos.weathr.R
import io.github.claudio_santos.weathr.domain.model.Location

@Composable
fun SearchCityDialog(
    onSearch: (String) -> Unit,
    searchResults: List<Location>,
    onSelectCity: (Location) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.search_city_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        onSearch(it)
                    },
                    label = { Text(stringResource(R.string.city_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(searchResults) { location ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectCity(location) }
                                .padding(vertical = 8.dp)
                        ) {
                            Column {
                                Text(location.name)
                                Text(
                                    text = location.country,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
