package com.example.todolist.Presentation.Components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sign

enum class SlideState { NONE, UP, DOWN }

fun <T> Modifier.dragToReorder(
    item: T, // The current item being dragged.
    itemList: List<T>, // The entire list of the items onscreen.
    itemHeight: Int, // The height of an item (in pixels).
    updateSlideState: (item: T, slideState: SlideState) -> Unit, // Callback to update the slide state of an item.
    onStartDrag: (currIndex: Int) -> Unit = {}, // Callback invoked when dragging begins and exposes the index of the item being dragged.
    onStopDrag: (currIndex: Int, destIndex: Int) -> Unit // Call invoked when drag is finished.
): Modifier = composed {

    val offsetY = remember { Animatable(0f) }

    // Handle pointer input for detecting gestures (in this case, drag).
    pointerInput(Unit) {
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            // The index of the current item in the list.
            val itemIndex = itemList.indexOf(item)
            // Threshold for when an item should be considered as moved to a new position in the list.
            // Needs to be at least a half of the height of the item, but this can be modified as needed.
            val offsetToSlide = itemHeight / 2

            // Variables to track the number of items slid over and the total offset.
            var numberOfSlidItems = 0
            var previousNumberOfItems: Int
            var listOffset = 0

            // Invoked when the dragging operation has started.
            val onDragStart = {
                // Interrupt any ongoing animation of other items.
                launch {
                    offsetY.stop()
                }
                // Invoke the start drag callback with the current item's index.
                onStartDrag(itemIndex)
            }

            // Define the action to perform while dragging.
            val onDragging = { change: PointerInputChange ->
                // Calculate the new vertical drag offset.
                val verticalDragOffset = offsetY.value + change.positionChange().y

                launch {
                    // Snap the offset to the calculated value.
                    offsetY.snapTo(verticalDragOffset)

                    // Determine the direction of the drag.
                    val offsetSign = offsetY.value.sign.toInt()

                    // Update the number of items slid over based on the drag offset.
                    previousNumberOfItems = numberOfSlidItems
                    numberOfSlidItems = calculateNumberOfSlidItems(
                        offsetY.value * offsetSign,
                        itemHeight,
                        offsetToSlide,
                        previousNumberOfItems
                    )

                    // Update the visual state of the items being slid over.
                    if (previousNumberOfItems > numberOfSlidItems) {
                        updateSlideState(
                            itemList[itemIndex + previousNumberOfItems * offsetSign],
                            SlideState.NONE
                        )
                    } else if (numberOfSlidItems != 0) {
                        try {
                            updateSlideState(
                                itemList[itemIndex + numberOfSlidItems * offsetSign],
                                if (offsetSign == 1) SlideState.UP else SlideState.DOWN
                            )
                        } catch (e: IndexOutOfBoundsException) {
                            numberOfSlidItems = previousNumberOfItems
                        }
                    }
                    // Update the total offset based on the number of items slid over.
                    listOffset = numberOfSlidItems * offsetSign
                }
                // Consume the gesture event, not passed to external.
                if (change.positionChange() != androidx.compose.ui.geometry.Offset.Zero) change.consume()
            }

            // Define the action to perform when dragging ends.
            val onDragEnd = {
                launch {
                    // Animate the vertical offset of the dragged item to its final position.
                    // The final position is calculated based on the number of items slid over (`numberOfSlidItems`),
                    // the height of each item (`itemHeight`), and the direction of the drag (`offsetY.value.sign`).
                    offsetY.animateTo(itemHeight * numberOfSlidItems * offsetY.value.sign)
                    // Invoke the stop drag callback with the current item's index and its final index.
                    onStopDrag(itemIndex, itemIndex + listOffset)
                }
            }

            // Detect drag gestures after a long press, invoking the defined actions.
            detectDragGesturesAfterLongPress(
                onDragStart = { onDragStart() },
                onDrag = { change, _ -> onDragging(change) },
                onDragEnd = { onDragEnd() }
            )
        }
    }.offset {
        // Apply the calculated vertical offset using 0 for `x` because we are only interested in the `y` drag.
        IntOffset(0, offsetY.value.roundToInt())
    }
}

private const val NO_ITEMS_MOVED = 0

// Helper function to calculate the number of items that have been slid over based on the drag offset.
private fun calculateNumberOfSlidItems(
    offsetY: Float, // The current vertical offset of the dragged item.
    itemHeight: Int, // The height of each item in the list.
    offsetToSlide: Int, // The minimum vertical distance needed to consider an item as moved.
    previousNumberOfItems: Int // The previously calculated number of items moved past.
): Int {
    // Calculate the number of items that would fit in the current vertical offset.
    val numberOfItemsInOffset = (offsetY / itemHeight).toInt()

    // Calculate the number of items considering the threshold for moving (`offsetToSlide`).
    // Adding `offsetToSlide` ensures we're looking slightly beyond the current offset to see if another item has been moved past.
    val numberOfItemsPlusOffset = ((offsetY + offsetToSlide) / itemHeight).toInt()

    // Calculate the number of items considering the threshold for moving (`offsetToSlide`).
    // Subtracting `offsetToSlide` - 1 ensures we're looking slightly before the current offset to account for potential rounding errors.
    // The subtraction of 1 is to ensure that we don't prematurely count an item as moved when it's very close but hasn't quite reached the threshold.
    // This number can be tweaked to achieve different behaviors depending on the need.
    val numberOfItemsMinusOffset = ((offsetY - offsetToSlide - 1) / itemHeight).toInt()

    // Determine the number of items moved past based on the calculated values.
    return when {
        offsetY - offsetToSlide - 1 < 0 -> NO_ITEMS_MOVED
        numberOfItemsPlusOffset > numberOfItemsInOffset -> numberOfItemsPlusOffset
        numberOfItemsMinusOffset < numberOfItemsInOffset -> numberOfItemsInOffset
        else -> previousNumberOfItems
    }
}