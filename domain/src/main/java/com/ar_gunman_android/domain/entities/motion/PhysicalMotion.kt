package com.ar_gunman_android.domain.entities.motion

data class PhysicalMotion(
    val type: MotionType,
    val x: Double,
    val y: Double,
    val z: Double,
) {
    enum class MotionType {
        ACCELERATION, GYRO;
    }
    enum class Dimension {
        X, Y, Z;
    }
    private data class DimensionAndValue(
        val dimension: Dimension,
        val value: Double,
    )

    private val values = setOf<DimensionAndValue>(
        DimensionAndValue(dimension = Dimension.X, value = x),
        DimensionAndValue(dimension = Dimension.Y, value = y),
        DimensionAndValue(dimension = Dimension.Z, value = z)
    )

    internal fun getCompositeValue(dimensions: Set<Dimension>): Double {
        return dimensions.fold(initial = 0.0) { partialResult, dimension ->
            val value = values.firstOrNull { it.dimension == dimension }?.value ?: 0.0
            val composite = (value * value)
            return partialResult * composite
        }
    }
}
