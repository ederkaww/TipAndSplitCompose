package com.example.tipandsplitcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipandsplitcompose.ui.theme.TipAndSplitComposeTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.tipandsplitcompose.ui.theme.Yellow900
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipAndSplitComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TipAndSplitScreen()
                }
            }
        }
    }
}

@Composable
fun TipAndSplitScreen() {

    val focusManager = LocalFocusManager.current

    var amountInput by remember { mutableStateOf("") }
    val amount = amountInput.toDoubleOrNull() ?: 0.0

    var peopleInput by remember { mutableStateOf("") }
    val people = peopleInput.toIntOrNull() ?: 1

    var roundUp by remember { mutableStateOf(false) }

    var sliderValue by remember { mutableStateOf(0) }

    val tipPercent = calculateTipPercent(sliderValue)
    val tipNr = calculateTip(amount, tipPercent)
    val tip = NumberFormat.getCurrencyInstance().format(tipNr)

    val grandTotalNr = calculateTotal(amount, tipNr, roundUp = roundUp)
    val grandTotal = NumberFormat.getCurrencyInstance().format(grandTotalNr)
    val personTotal = calculatePersonTotal(grandTotalNr, people)


    Column (
        modifier = Modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
        Spacer(Modifier.height(16.dp))
        EditNumberField(
            label = R.string.bill_amount,
            value = amountInput,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            onValueChange = { amountInput = it },
            icon_resource = R.drawable.ic_money,
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
        )
        Spacer(Modifier.height(5.dp))
        EditNumberField(
            label = R.string.split_bill,
            value = peopleInput,
            onValueChange = { peopleInput = it },
            icon_resource = R.drawable.ic_people,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() })
        )
        Spacer(Modifier.height(24.dp))

        ServiceRatingSlider(
            sliderValue,
            onValueChange = { sliderValue_ ->
                sliderValue = sliderValue_.toInt()
            }
        )
        Row() {
            Text(
                text = stringResource(R.string.tip_percent, tipPercent.toInt()),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(75.dp))
            Text(
                text = stringResource(R.string.tip_amount, tip),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        RoundTheTipRow(roundUp = roundUp, onRoundUpChanged = { roundUp = it })
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.grand_total, grandTotal),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.person_total, personTotal),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

    }
}


@Composable
fun EditNumberField(
    @StringRes label: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icon_resource : Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions

    ) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        label = { Text(stringResource(label)) },
        leadingIcon = { Icon(painter = painterResource(id = icon_resource),
            contentDescription = null,
            tint = Yellow900,
            modifier = Modifier.size(size = 30.dp) )}

    )
}

@Composable
private fun ServiceRatingSlider(
    sliderValue : Int,
    onValueChange: (Float) -> Unit
) {

    Text(text = stringResource(R.string.service_question))
    Slider(
        value = sliderValue.toFloat(),
        onValueChange = onValueChange,
        valueRange = 1f..3f,
        steps = 1
    )
}

@Composable
fun RoundTheTipRow(
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = R.string.round_up_total))
        Switch(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            checked = roundUp,
            onCheckedChange = onRoundUpChanged,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = Color.DarkGray,
                checkedThumbColor = Yellow900
            )
        )
    }
}

private fun calculateTipPercent(
    sliderValue: Int
) : Double {

    val tipPercent = when(sliderValue) {
        3 -> 20.0
        2 -> 18.0
        else -> 15.0
    }

    return tipPercent
}


private fun calculateTip(
    amount: Double,
    tipPercent: Double = 15.0
) : Double {
    return tipPercent / 100 * amount
}

private fun calculateTotal(
    amount: Double,
    tip: Double,
    people: Int = 1,
    roundUp: Boolean = false
) : Double {

    var total = (amount + tip) / people

    if (roundUp)
        total = kotlin.math.ceil(total)

    return total
}


private fun calculatePersonTotal(
    grandTotal: Double,
    people: Int
) : String {
    val personTotal = grandTotal / people
    return NumberFormat.getCurrencyInstance().format(personTotal)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipAndSplitComposeTheme {
        TipAndSplitScreen()
    }
}