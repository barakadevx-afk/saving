package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun shimmerBrush(
    showShimmer: Boolean = true,
    targetValue: Float = 1000f
): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color(0xFFCBD5E1).copy(alpha = 0.35f),
            Color(0xFFF1F5F9).copy(alpha = 0.85f),
            Color(0xFFCBD5E1).copy(alpha = 0.35f)
        )

        val transition = rememberInfiniteTransition(label = "ShimmerTransition")
        val translateAnimation by transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ShimmerAnimation"
        )

        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation, y = translateAnimation)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    width: Dp? = null,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val brush = shimmerBrush()
    val boxModifier = modifier
        .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
        .height(height)
        .clip(shape)
        .background(brush)

    Box(modifier = boxModifier)
}

// 1. Bento Hero Card Skeleton
@Composable
fun BentoHeroBalanceCardSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bento_hero_skeleton"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoHeroCardBg),
        border = BorderStroke(1.dp, BentoBorder)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ShimmerBox(modifier = Modifier.size(18.dp), shape = CircleShape)
                    Spacer(modifier = Modifier.width(8.dp))
                    ShimmerBox(width = 120.dp, height = 14.dp)
                }
                ShimmerBox(width = 70.dp, height = 22.dp, shape = RoundedCornerShape(12.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Balance Shimmer
            ShimmerBox(width = 200.dp, height = 36.dp, shape = RoundedCornerShape(10.dp))

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons Row Shimmer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier.weight(1f),
                    height = 46.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                ShimmerBox(
                    modifier = Modifier.weight(1f),
                    height = 46.dp,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

// 2. Bento Stats Grid Skeleton (2x2 Grid)
@Composable
fun BentoStatsGridSkeleton() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.testTag("bento_stats_skeleton")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BentoStatCardSkeleton(modifier = Modifier.weight(1f))
            BentoStatCardSkeleton(modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BentoStatCardSkeleton(modifier = Modifier.weight(1f))
            BentoStatCardSkeleton(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun BentoStatCardSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = BorderStroke(1.dp, BentoBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(width = 80.dp, height = 12.dp)
                ShimmerBox(modifier = Modifier.size(28.dp), shape = CircleShape)
            }

            Spacer(modifier = Modifier.height(12.dp))

            ShimmerBox(width = 110.dp, height = 20.dp, shape = RoundedCornerShape(6.dp))

            Spacer(modifier = Modifier.height(6.dp))

            ShimmerBox(width = 75.dp, height = 10.dp)
        }
    }
}

// 3. Bento Chart Skeleton Card
@Composable
fun BentoEarningsChartCardSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bento_chart_skeleton"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = BorderStroke(1.dp, BentoBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    ShimmerBox(width = 150.dp, height = 16.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    ShimmerBox(width = 100.dp, height = 11.dp)
                }
                ShimmerBox(width = 60.dp, height = 24.dp, shape = RoundedCornerShape(12.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Shimmer Bar Chart Skeleton Representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(60.dp, 85.dp, 45.dp, 110.dp, 75.dp, 120.dp, 95.dp)
                heights.forEach { h ->
                    ShimmerBox(
                        width = 28.dp,
                        height = h,
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // X-Axis Label Placeholders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) {
                    ShimmerBox(width = 24.dp, height = 10.dp)
                }
            }
        }
    }
}

// 4. Bento Active Cycle Card Skeleton
@Composable
fun BentoActiveCycleCardSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bento_cycle_skeleton"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = BorderStroke(1.dp, BentoBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ShimmerBox(modifier = Modifier.size(36.dp), shape = CircleShape)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        ShimmerBox(width = 110.dp, height = 14.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        ShimmerBox(width = 80.dp, height = 10.dp)
                    }
                }
                ShimmerBox(width = 85.dp, height = 24.dp, shape = RoundedCornerShape(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar Shimmer
            ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 12.dp, shape = RoundedCornerShape(6.dp))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ShimmerBox(width = 90.dp, height = 12.dp)
                ShimmerBox(width = 90.dp, height = 12.dp)
            }
        }
    }
}

// 5. Full Bento Dashboard Skeleton View
@Composable
fun BentoDashboardSkeletonScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("dashboard_skeleton_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                ShimmerBox(width = 180.dp, height = 22.dp)
                Spacer(modifier = Modifier.height(6.dp))
                ShimmerBox(width = 130.dp, height = 13.dp)
            }
            ShimmerBox(width = 80.dp, height = 28.dp, shape = RoundedCornerShape(16.dp))
        }

        BentoHeroBalanceCardSkeleton()
        BentoStatsGridSkeleton()
        BentoEarningsChartCardSkeleton()
        BentoActiveCycleCardSkeleton()
    }
}
