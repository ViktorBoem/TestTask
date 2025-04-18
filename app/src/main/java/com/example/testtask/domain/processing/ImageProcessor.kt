package com.example.testtask.domain.processing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import androidx.core.graphics.get

class ImageProcessor {
    private val smoothingWindow = ArrayList<Float>()
    private val smoothingWindowSize = 5

    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(
            nv21,
            ImageFormat.NV21,
            imageProxy.width,
            imageProxy.height,
            null
        )

        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            Rect(0, 0, imageProxy.width, imageProxy.height),
            100,
            out
        )

        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun calculateAverageRedIntensity(bitmap: Bitmap): Float {
        val width = bitmap.width
        val height = bitmap.height

        val centerWidth = width / 2
        val centerHeight = height / 2
        val sampleRadius = min(width, height) / 4

        var redSum = 0
        var count = 0

        for (y in centerHeight - sampleRadius until centerHeight + sampleRadius) {
            for (x in centerWidth - sampleRadius until centerWidth + sampleRadius) {
                if (y >= 0 && y < height && x >= 0 && x < width) {
                    val pixel = bitmap[x, y]
                    redSum += Color.red(pixel)
                    count++
                }
            }
        }

        val rawRedAvg = redSum.toFloat() / count

        smoothingWindow.add(rawRedAvg)
        if (smoothingWindow.size > smoothingWindowSize) {
            smoothingWindow.removeAt(0)
        }

        return smoothingWindow.average().toFloat()
    }

    private fun min(a: Int, b: Int): Int {
        return if (a < b) a else b
    }
}