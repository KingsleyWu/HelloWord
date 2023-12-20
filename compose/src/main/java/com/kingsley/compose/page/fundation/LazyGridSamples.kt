/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kingsley.compose.page.fundation


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun LazyVerticalGridSample() {
    val itemsList = (0..5).toList()
    val itemsIndexedList = listOf("A", "B", "C")

    val itemModifier = Modifier.border(1.dp, Color.Blue).height(80.dp).wrapContentSize()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3)
    ) {
        items(itemsList) {
            Text("Item is $it", itemModifier)
        }
        item {
            Text("Single item", itemModifier)
        }
        itemsIndexed(itemsIndexedList) { index, item ->
            Text("Item at index $index is $item", itemModifier)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LazyVerticalGridSpanSample() {
    val sections = (0 until 25).toList().chunked(5)
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        sections.forEachIndexed { index, items ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "This is section $index",
                    Modifier.border(1.dp, Color.Gray).height(80.dp).wrapContentSize()
                )
            }
            items(
                items,
                // not required as it is the default
                span = { GridItemSpan(1) }
            ) {
                Text(
                    "Item $it",
                    Modifier.border(1.dp, Color.Blue).height(80.dp).wrapContentSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LazyHorizontalGridSample() {
    val itemsList = (0..5).toList()
    val itemsIndexedList = listOf("A", "B", "C")

    val itemModifier = Modifier.border(1.dp, Color.Blue).width(80.dp).wrapContentSize()

    LazyHorizontalGrid(
        rows = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(itemsList) {
            Text("Item is $it", itemModifier)
        }

        item {
            Text("Single item", itemModifier)
        }

        itemsIndexed(itemsIndexedList) { index, item ->
            Text("Item at index $index is $item", itemModifier)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LazyHorizontalGridSpanSample() {
    val sections = (0 until 25).toList().chunked(5)
    LazyHorizontalGrid(
        rows = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        sections.forEachIndexed { index, items ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "This is section $index",
                    Modifier.border(1.dp, Color.Gray).width(80.dp).wrapContentSize()
                )
            }
            items(
                items,
                // not required as it is the default
                span = { GridItemSpan(1) }
            ) {
                Text(
                    "Item $it",
                    Modifier.border(1.dp, Color.Blue).width(80.dp).wrapContentSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UsingGridScrollPositionForSideEffectSample() {
    val gridState = rememberLazyGridState()
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .collect {
                // use the new index
            }
    }
}

@Preview(showBackground = true)
@Composable
fun UsingGridScrollPositionInCompositionSample() {
    val gridState = rememberLazyGridState()
    val isAtTop by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0
        }
    }
    if (!isAtTop) {
        ScrollToTopButton(gridState)
    }
}

@Preview(showBackground = true)
@Composable
fun UsingGridLayoutInfoForSideEffectSample() {
    val gridState = rememberLazyGridState()
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.totalItemsCount }
            .collect {
                // use the new items count
            }
    }
}

@Composable
private fun ScrollToTopButton(@Suppress("UNUSED_PARAMETER") gridState: LazyGridState) {
}
