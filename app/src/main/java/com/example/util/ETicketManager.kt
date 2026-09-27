package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.TripBooking
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RouteDetails(
    val fromCity: String,
    val fromCode: String,
    val fromStation: String,
    val toCity: String,
    val toCode: String,
    val toStation: String,
    val departureTime: String,
    val arrivalTime: String,
    val serviceNumber: String,
    val seatOrClass: String
)

object ETicketManager {

    fun parseRouteDetails(booking: TripBooking): RouteDetails {
        val title = booking.destinationTitle.lowercase()
        return when {
            title.contains("bom") || title.contains("mumbai") -> {
                RouteDetails(
                    fromCity = "New Delhi",
                    fromCode = "DEL",
                    fromStation = "Indira Gandhi Int'l Airport (T3)",
                    toCity = "Mumbai",
                    toCode = "BOM",
                    toStation = "Chhatrapati Shivaji Maharaj Int'l (T2)",
                    departureTime = "08:30 AM",
                    arrivalTime = "10:45 AM (2h 15m)",
                    serviceNumber = "Akasa Air QP-1342",
                    seatOrClass = "Economy • Seats 14A, 14B"
                )
            }
            title.contains("del") || title.contains("delhi") -> {
                RouteDetails(
                    fromCity = "Mumbai",
                    fromCode = "BOM",
                    fromStation = "CSM Int'l Airport (T2)",
                    toCity = "New Delhi",
                    toCode = "DEL",
                    toStation = "Indira Gandhi Int'l Airport (T3)",
                    departureTime = "07:15 AM",
                    arrivalTime = "09:30 AM (2h 15m)",
                    serviceNumber = "IndiGo 6E-2051",
                    seatOrClass = "Economy • Seat 18C"
                )
            }
            title.contains("blr") || title.contains("bangalore") || title.contains("bengaluru") -> {
                RouteDetails(
                    fromCity = "New Delhi",
                    fromCode = "DEL",
                    fromStation = "Indira Gandhi Int'l Airport (T3)",
                    toCity = "Bengaluru",
                    toCode = "BLR",
                    toStation = "Kempegowda Int'l Airport (T1)",
                    departureTime = "09:00 AM",
                    arrivalTime = "11:45 AM (2h 45m)",
                    serviceNumber = "Air India AI-506",
                    seatOrClass = "Economy • Seat 21D"
                )
            }
            title.contains("train") || title.contains("express") -> {
                RouteDetails(
                    fromCity = "New Delhi",
                    fromCode = "NDLS",
                    fromStation = "New Delhi Railway Station (Platform 2)",
                    toCity = "Bhubaneswar",
                    toCode = "BBS",
                    toStation = "Bhubaneswar Railway Station (Platform 1)",
                    departureTime = "06:15 AM",
                    arrivalTime = "08:30 PM (14h 15m)",
                    serviceNumber = "Purushottam Superfast Exp (12802)",
                    seatOrClass = "AC 3-Tier (Coach B2, Berths 21, 22)"
                )
            }
            title.contains("goa") -> {
                RouteDetails(
                    fromCity = "Origin City",
                    fromCode = "HOME",
                    fromStation = "Direct Pickup / Airport Transfer",
                    toCity = "Goa",
                    toCode = "GOI",
                    toStation = "Mopa Int'l / Dabolim (North & South Goa)",
                    departureTime = "09:00 AM",
                    arrivalTime = "11:30 AM",
                    serviceNumber = "WonderLight Holiday Escapes WL-GOA",
                    seatOrClass = "Premium Package • Deluxe Beach Villa"
                )
            }
            title.contains("kashmir") -> {
                RouteDetails(
                    fromCity = "Origin City",
                    fromCode = "HOME",
                    fromStation = "Direct Pickup / Airport Transfer",
                    toCity = "Srinagar, Kashmir",
                    toCode = "SXR",
                    toStation = "Sheikh Ul-Alam Int'l Airport / Dal Lake",
                    departureTime = "07:30 AM",
                    arrivalTime = "10:15 AM",
                    serviceNumber = "WonderLight Paradise Escapes WL-KMR",
                    seatOrClass = "Deluxe Houseboat & Mountain Resort"
                )
            }
            title.contains("kerala") -> {
                RouteDetails(
                    fromCity = "Origin City",
                    fromCode = "HOME",
                    fromStation = "Direct Pickup / Airport Transfer",
                    toCity = "Kochi, Kerala",
                    toCode = "COK",
                    toStation = "Cochin Int'l Airport (Nedumbassery)",
                    departureTime = "08:00 AM",
                    arrivalTime = "11:15 AM",
                    serviceNumber = "WonderLight Backwaters Tour WL-KRL",
                    seatOrClass = "Luxury Houseboat & Plantation Stay"
                )
            }
            title.contains("cab") || title.contains("taxi") -> {
                RouteDetails(
                    fromCity = "Pickup Point",
                    fromCode = "ORIGIN",
                    fromStation = "Doorstep / Hotel Pickup",
                    toCity = "Drop Location",
                    toCode = "DEST",
                    toStation = "City Center / Destination",
                    departureTime = "08:00 AM",
                    arrivalTime = "On Demand",
                    serviceNumber = "WonderLight City Express Cab",
                    seatOrClass = "AC Sedan / SUV (Dedicated Chauffeur)"
                )
            }
            else -> {
                RouteDetails(
                    fromCity = "Departure City",
                    fromCode = "FROM",
                    fromStation = "Boarding / Departure Terminal",
                    toCity = booking.destinationTitle.substringBefore(" •").substringBefore(" -"),
                    toCode = "TO",
                    toStation = "Arrival / Destination Center",
                    departureTime = "Scheduled Departure",
                    arrivalTime = "Scheduled Arrival",
                    serviceNumber = "WonderLight Travel Service ${booking.bookingId}",
                    seatOrClass = "${booking.adults} Adults • ${booking.duration}"
                )
            }
        }
    }

    /**
     * Generates an official WonderLight E-Ticket PDF and saves it to the Downloads directory.
     */
    fun generateETicketPdf(context: Context, booking: TripBooking): File? {
        val route = parseRouteDetails(booking)
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (595 x 842 pt)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Background
        canvas.drawColor(Color.WHITE)

        // Header Background Banner (Teal)
        rectPaint.color = Color.parseColor("#084D5C")
        canvas.drawRect(0f, 0f, 595f, 105f, rectPaint)

        // Accent strip (Orange)
        rectPaint.color = Color.parseColor("#FF6D00")
        canvas.drawRect(0f, 105f, 595f, 110f, rectPaint)

        // Header Title
        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 22f
        canvas.drawText("WonderLight Adventure", 40f, 48f, paint)

        // Header Subtitle
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f
        canvas.drawText("OFFICIAL E-TICKET & CONFIRMED BOARDING PASS", 40f, 72f, paint)

        // Right side header info
        paint.textSize = 10f
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Generated: " + SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date()), 555f, 48f, paint)
        canvas.drawText("24x7 Support: 1800-WONDER (966-337)", 555f, 68f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Booking Reference & Status Card
        val cardRect = RectF(40f, 125f, 555f, 185f)
        rectPaint.color = Color.parseColor("#F0FDF4")
        canvas.drawRoundRect(cardRect, 10f, 10f, rectPaint)
        rectPaint.style = Paint.Style.STROKE
        rectPaint.color = Color.parseColor("#86EFAC")
        rectPaint.strokeWidth = 1.5f
        canvas.drawRoundRect(cardRect, 10f, 10f, rectPaint)
        rectPaint.style = Paint.Style.FILL

        paint.color = Color.parseColor("#14532D")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        canvas.drawText("BOOKING REFERENCE / PNR", 55f, 148f, paint)

        paint.textSize = 18f
        paint.color = Color.parseColor("#084D5C")
        canvas.drawText(booking.bookingId, 55f, 172f, paint)

        // Status Badge
        paint.textAlign = Paint.Align.RIGHT
        paint.color = Color.parseColor("#15803D")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("● ${booking.status.uppercase()}", 540f, 150f, paint)

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.parseColor("#166534")
        canvas.drawText("Payment Completed & Verified", 540f, 168f, paint)
        paint.textAlign = Paint.Align.LEFT

        // FROM & TO ROUTE DETAILS BOX (Prominent flight/journey card)
        var currentY = 205f
        val routeBox = RectF(40f, currentY, 555f, currentY + 110f)
        rectPaint.color = Color.parseColor("#F8FAFC")
        canvas.drawRoundRect(routeBox, 10f, 10f, rectPaint)
        rectPaint.style = Paint.Style.STROKE
        rectPaint.color = Color.parseColor("#CBD5E1")
        rectPaint.strokeWidth = 1f
        canvas.drawRoundRect(routeBox, 10f, 10f, rectPaint)
        rectPaint.style = Paint.Style.FILL

        // FROM section
        paint.color = Color.parseColor("#64748B")
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("FROM (ORIGIN)", 55f, currentY + 22f, paint)

        paint.color = Color.parseColor("#084D5C")
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(route.fromCode, 55f, currentY + 50f, paint)

        paint.color = Color.parseColor("#1E293B")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(route.fromCity, 55f, currentY + 68f, paint)

        paint.color = Color.parseColor("#64748B")
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(route.fromStation, 55f, currentY + 82f, paint)
        canvas.drawText("Dep: ${route.departureTime}", 55f, currentY + 96f, paint)

        // Route arrow in middle
        paint.color = Color.parseColor("#0284C7")
        paint.textSize = 16f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("✈ ──────────>", 297f, currentY + 45f, paint)
        paint.textSize = 9f
        paint.color = Color.parseColor("#64748B")
        canvas.drawText(route.serviceNumber, 297f, currentY + 62f, paint)
        canvas.drawText(route.seatOrClass, 297f, currentY + 76f, paint)
        paint.textAlign = Paint.Align.LEFT

        // TO section
        paint.textAlign = Paint.Align.RIGHT
        paint.color = Color.parseColor("#64748B")
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("TO (DESTINATION)", 540f, currentY + 22f, paint)

        paint.color = Color.parseColor("#084D5C")
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(route.toCode, 540f, currentY + 50f, paint)

        paint.color = Color.parseColor("#1E293B")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(route.toCity, 540f, currentY + 68f, paint)

        paint.color = Color.parseColor("#64748B")
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(route.toStation, 540f, currentY + 82f, paint)
        canvas.drawText("Arr: ${route.arrivalTime}", 540f, currentY + 96f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Passenger Details Box
        currentY += 130f
        paint.color = Color.parseColor("#084D5C")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13f
        canvas.drawText("TRAVELLER / PASSENGER INFORMATION", 40f, currentY, paint)

        currentY += 12f
        rectPaint.color = Color.parseColor("#E2E8F0")
        rectPaint.strokeWidth = 1f
        canvas.drawLine(40f, currentY, 555f, currentY, rectPaint)

        currentY += 22f
        paint.textSize = 11f
        paint.color = Color.parseColor("#334155")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Lead Passenger:", 40f, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(if (booking.travellerName.isNotBlank()) booking.travellerName else "Verified Explorer", 160f, currentY, paint)

        currentY += 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Email Address:", 40f, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(if (booking.email.isNotBlank()) booking.email else "Not provided", 160f, currentY, paint)

        currentY += 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Phone Number:", 40f, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(if (booking.phone.isNotBlank()) booking.phone else "+91 (Verified Mobile)", 160f, currentY, paint)

        currentY += 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Total Guests:", 40f, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("${booking.adults} Adults, ${booking.children} Children", 160f, currentY, paint)

        // Payment Summary Box
        currentY += 35f
        paint.color = Color.parseColor("#084D5C")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13f
        canvas.drawText("TAX INVOICE & PAYMENT DETAILS", 40f, currentY, paint)

        currentY += 12f
        canvas.drawLine(40f, currentY, 555f, currentY, rectPaint)

        currentY += 22f
        paint.textSize = 11f
        paint.color = Color.parseColor("#334155")
        canvas.drawText("Payment Method:", 40f, currentY, paint)
        canvas.drawText(if (booking.paymentMethod.isNotBlank()) booking.paymentMethod else "Online UPI / Cards", 160f, currentY, paint)

        currentY += 18f
        canvas.drawText("Base Fare:", 40f, currentY, paint)
        canvas.drawText("INR ₹${(booking.totalAmount * 0.82).toInt()}", 160f, currentY, paint)

        currentY += 18f
        canvas.drawText("GST & Taxes (18%):", 40f, currentY, paint)
        canvas.drawText("INR ₹${(booking.totalAmount * 0.18).toInt()}", 160f, currentY, paint)

        currentY += 22f
        rectPaint.color = Color.parseColor("#F8FAFC")
        val totalRect = RectF(40f, currentY - 12f, 555f, currentY + 22f)
        canvas.drawRoundRect(totalRect, 6f, 6f, rectPaint)
        rectPaint.style = Paint.Style.STROKE
        rectPaint.color = Color.parseColor("#CBD5E1")
        canvas.drawRoundRect(totalRect, 6f, 6f, rectPaint)
        rectPaint.style = Paint.Style.FILL

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13f
        paint.color = Color.parseColor("#0F172A")
        canvas.drawText("TOTAL AMOUNT PAID:", 55f, currentY + 10f, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 15f
        paint.color = Color.parseColor("#084D5C")
        canvas.drawText("INR ₹${booking.totalAmount}", 540f, currentY + 10f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Barcode Simulation
        currentY += 55f
        rectPaint.color = Color.parseColor("#0F172A")
        val barcodeStartX = 140f
        var bx = barcodeStartX
        val barHeights = 32f
        for (i in 0..45) {
            val w = if (i % 3 == 0) 4f else if (i % 5 == 0) 1.5f else 2.5f
            canvas.drawRect(bx, currentY, bx + w, currentY + barHeights, rectPaint)
            bx += w + (if (i % 2 == 0) 3.5f else 2f)
        }

        currentY += barHeights + 12f
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#475569")
        canvas.drawText("* ${booking.bookingId} *", 297f, currentY, paint)
        paint.textAlign = Paint.Align.LEFT

        // Important Instructions Footer
        currentY += 25f
        rectPaint.color = Color.parseColor("#FFFBEB")
        val noticeRect = RectF(40f, currentY, 555f, currentY + 70f)
        canvas.drawRoundRect(noticeRect, 6f, 6f, rectPaint)
        rectPaint.style = Paint.Style.STROKE
        rectPaint.color = Color.parseColor("#FDE68A")
        canvas.drawRoundRect(noticeRect, 6f, 6f, rectPaint)
        rectPaint.style = Paint.Style.FILL

        paint.color = Color.parseColor("#92400E")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 9.5f
        canvas.drawText("IMPORTANT TRAVEL GUIDELINES:", 55f, currentY + 18f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 8.5f
        paint.color = Color.parseColor("#78350F")
        canvas.drawText("• Carry a valid Government Photo ID (Aadhaar/Passport/Voter ID) matching passenger name.", 55f, currentY + 32f, paint)
        canvas.drawText("• Airport reporting 2 hrs prior / Railway station reporting 30 mins prior to departure.", 55f, currentY + 44f, paint)
        canvas.drawText("• WonderLight Concierge & Emergency Helpline: 1800-WONDER (966-337) • support@wonderlight.com", 55f, currentY + 56f, paint)

        pdfDocument.finishPage(page)

        // Save PDF file
        val filename = "WonderLight_Ticket_${booking.bookingId}.pdf"
        var targetFile: File? = null

        try {
            val outputDir = File(context.cacheDir, "etickets")
            if (!outputDir.exists()) outputDir.mkdirs()
            targetFile = File(outputDir, filename)
            FileOutputStream(targetFile).use { out ->
                pdfDocument.writeTo(out)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/WonderLight")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { out ->
                        targetFile.inputStream().copyTo(out)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val publicDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (publicDownloads.exists() || publicDownloads.mkdirs()) {
                    val publicFile = File(publicDownloads, filename)
                    targetFile.copyTo(publicFile, overwrite = true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }

        return targetFile
    }

    fun openETicket(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open WonderLight E-Ticket"))
        } catch (e: Exception) {
            Toast.makeText(context, "Saved to Downloads: ${file.name}", Toast.LENGTH_LONG).show()
        }
    }

    fun shareETicket(context: Context, file: File, booking: TripBooking) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "WonderLight E-Ticket: ${booking.destinationTitle} (${booking.bookingId})")
                putExtra(Intent.EXTRA_TEXT, "Here is your WonderLight E-Ticket for ${booking.destinationTitle}.\nBooking ID: ${booking.bookingId}\nStatus: ${booking.status}\nTotal Paid: ₹${booking.totalAmount}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share E-Ticket"))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not share ticket: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
