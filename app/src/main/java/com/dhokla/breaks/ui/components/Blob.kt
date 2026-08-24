package com.dhokla.breaks.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import com.dhokla.breaks.ui.theme.LocalCalm
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private class BlobGeometry(
    val xs: FloatArray,
    val ys: FloatArray,
    val path: Path
)

private fun generateBlob(random: Random, points: Int, minR: Float, maxR: Float): BlobGeometry {
    val step = (2.0 * PI / points).toFloat()
    val radii = FloatArray(points) { minR + random.nextFloat() * (maxR - minR) }
    val smoothed = FloatArray(points)
    for (i in 0 until points) {
        val prev = radii[(i - 1 + points) % points]
        val next = radii[(i + 1) % points]
        smoothed[i] = (prev + 2f * radii[i] + next) / 4f
    }
    System.arraycopy(smoothed, 0, radii, 0, points)
    val xs = FloatArray(points)
    val ys = FloatArray(points)
    for (i in 0 until points) {
        val angle = i * step + (random.nextFloat() - 0.5f) * step * 0.6f
        xs[i] = 0.5f + cos(angle) * radii[i] * 0.5f
        ys[i] = 0.5f + sin(angle) * radii[i] * 0.5f
    }
    val path = Path()
    path.moveTo(xs[0], ys[0])
    for (i in 0 until points) {
        val p0 = Offset(xs[(i - 1 + points) % points], ys[(i - 1 + points) % points])
        val p1 = Offset(xs[i], ys[i])
        val p2 = Offset(xs[(i + 1) % points], ys[(i + 1) % points])
        val p3 = Offset(xs[(i + 2) % points], ys[(i + 2) % points])
        val c1 = p1 + ((p2 - p0) * (1f / 6f))
        val c2 = p2 - ((p3 - p1) * (1f / 6f))
        path.cubicTo(c1.x, c1.y, c2.x, c2.y, p2.x, p2.y)
    }
    path.close()
    return BlobGeometry(xs, ys, path)
}

private fun DrawScope.drawBlobScaled(geometry: BlobGeometry, brush: Brush, rotationDegrees: Float) {
    rotate(degrees = rotationDegrees) {
        scale(scaleX = size.width, scaleY = size.height, pivot = Offset.Zero) {
            drawPath(path = geometry.path, brush = brush)
        }
    }
}

@Composable
fun Blob(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val calm = LocalCalm.current
    val geometry = remember {
        generateBlob(
            random = Random(Random.nextInt()),
            points = 7 + Random.nextInt(4),
            minR = 0.74f,
            maxR = 1.0f
        )
    }
    val transition = rememberInfiniteTransition(label = "blob")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 120_000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(modifier = modifier.aspectRatio(1f)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    drawBlobScaled(
                        geometry = geometry,
                        brush = Brush.verticalGradient(
                            listOf(calm.blobSurfaceA, calm.blobSurfaceB)
                        ),
                        rotationDegrees = rotation
                    )
                }
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(0.52f),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
