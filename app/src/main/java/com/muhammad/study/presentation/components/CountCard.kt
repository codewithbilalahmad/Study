package com.muhammad.study.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.muhammad.study.presentation.theme.gradient4
import com.muhammad.study.presentation.theme.gradient6

@Composable
fun CountCard(modifier: Modifier = Modifier, headingText: String, count: String) {
    AnimatedBorder(modifier = modifier, shape = RoundedCornerShape(10.dp), brush = Brush.verticalGradient(
        gradient6
    ), content = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = headingText,
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = count, modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center)
            )
        }
    })
}

@Composable
fun AnimatedBorder(
    modifier: Modifier = Modifier,shape : Shape,content : @Composable () -> Unit,
    brush: Brush = Brush.linearGradient(colors = gradient4),
) {
    val infiniteTransition = rememberInfiniteTransition("rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = LinearEasing
            )
        ), label = "angle"
    )
    Surface(modifier = modifier, shape = shape){
        Surface(modifier = modifier.clipToBounds().fillMaxWidth().padding(4.dp).drawWithContent{
            rotate(rotation){
                drawCircle(brush = brush, radius = size.width, blendMode = BlendMode.SrcIn)
            }
            drawContent()
        }, shape = shape){
            Box(modifier = Modifier.fillMaxSize()){
                content()
            }
        }
    }
}