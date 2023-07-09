package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculator.components.inputField
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import com.example.tipcalculator.util.calculateTotalPerson
import com.example.tipcalculator.util.calculateTotalTip
import com.example.tipcalculator.widget.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    TipCalculatorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }

}


@Composable
fun TopHeader(totalPerPerson:Double = 134.0){
    Surface(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFF546e7a)


    ) {
        Column(modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per person",
            style = MaterialTheme.typography.h4)
            Text(text = "$$total",
            style = MaterialTheme.typography.h3,
            fontWeight = FontWeight.Bold)
        }

    }
}

@Composable
fun MainContent(){
    val split = remember {

        mutableStateOf(1)

    }
    val tipAmountState = remember{
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
   BillForm(tipAmountState = tipAmountState,
       totalPerPersonState = totalPerPersonState,
       Split = split )


}

@Preview
@Composable
fun DefaultPreview() {
    TipCalculatorTheme {
        MyApp {
            MainContent()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,

    Split :MutableState<Int>,
    tipAmountState:MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange : (String)->Unit = {}
){
    val totalBillState = remember{

        mutableStateOf("")

    }
    val validState = remember(totalBillState.value){

        totalBillState.value.trim().isNotEmpty()

    }
    val keyboardController = LocalSoftwareKeyboardController.current



    val sliderPositionState = remember {

        mutableStateOf(0f)

    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()


    Column{
        TopHeader(totalPerPersonState.value)
        Surface(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)

        ) {
            Column(modifier =Modifier.padding(3.dp)
                , horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top) {
                inputField(
                    valueState = totalBillState,
                    labelId = "enter bill",
                    isSingleLine = true,
                    enabled = true,
                    onAction = KeyboardActions{
                        if(!validState) {
                            return@KeyboardActions
                        }

                        onValChange(totalBillState.value.trim())

                        keyboardController?.hide()
                        tipAmountState.value = calculateTotalTip(totalBill=totalBillState.value.toDouble(), tipPercentage =tipPercentage)
                        totalPerPersonState.value = calculateTotalPerson(totalBill = totalBillState.value.toDouble(),
                            splitBy = Split.value,
                            tipPercentage = tipPercentage)
                    }
                )
            if (validState){
                Row(modifier = Modifier.padding(2.dp)
                    , horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split",
                        modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        ))
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {
                        RoundIconButton( imageVector = Icons.Default.Add,
                            onClick = {
                                Split.value++
                                totalPerPersonState.value = calculateTotalPerson(totalBill = totalBillState.value.toDouble(),
                                    splitBy = Split.value,
                                    tipPercentage = tipPercentage)
                            })
                        Text(text = "${Split.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton( imageVector = Icons.Default.Remove,
                            onClick = {
                                if (Split.value > 1){
                                    Split.value--
                                    totalPerPersonState.value = calculateTotalPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = Split.value,
                                        tipPercentage = tipPercentage)
                                }

                            })
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                    Text(text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(text = "${tipAmountState.value}")
                }
                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(12.dp))
                    Slider(value = sliderPositionState.value, onValueChange = {newVal->
                        sliderPositionState.value = newVal
                        tipAmountState.value = calculateTotalTip(totalBill=totalBillState.value.toDouble(), tipPercentage =tipPercentage)
                        totalPerPersonState.value = calculateTotalPerson(totalBill = totalBillState.value.toDouble(),
                            splitBy = Split.value,
                            tipPercentage = tipPercentage)
                    },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                        , steps = 4)

                }
            }else{
                Box(modifier =Modifier.padding(horizontal = 3.dp),
                ){

                }
            }

            }
        }
    }



}


