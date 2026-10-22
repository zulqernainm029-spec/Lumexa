package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.ConnectLiveViewModel
import com.example.ui.Event
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.PremiumGradientBrush
import com.example.ui.components.CosmosBackgroundBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCalendarScreen(viewModel: ConnectLiveViewModel) {
    val events by viewModel.events.collectAsState()
    val rsvps by viewModel.eventRsvps.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedEventIdForDetail by remember { mutableStateOf<String?>(null) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    // Mock interactive calendar dates for late August 2026
    val days = listOf(
        CalendarDay("Mon", "24", false),
        CalendarDay("Tue", "25", false),
        CalendarDay("Wed", "26", true), // today!
        CalendarDay("Thu", "27", false),
        CalendarDay("Fri", "28", false),
        CalendarDay("Sat", "29", false),
        CalendarDay("Sun", "30", false)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBackgroundBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C071A))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("main") },
                    modifier = Modifier.testTag("calendar_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = Color(0xFF8E2DE2),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Event Calendar Hub",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "August 2026 • Discover Live Meetups",
                        fontSize = 11.sp,
                        color = Color.White.copy(0.5f)
                    )
                }

                IconButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(0.05f), CircleShape)
                        .testTag("calendar_add_event_trigger")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event", tint = Color.White)
                }
            }

            // Scrollable Calendar Strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0C21))
                    .padding(vertical = 12.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(days) { day ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (day.isToday) Color(0xFF8E2DE2).copy(0.2f) else Color.Transparent)
                                .border(1.dp, if (day.isToday) Color(0xFF8E2DE2) else Color.Transparent, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .clickable {
                                    alertMessage = "Showing agenda schedule for August ${day.date}, 2026!"
                                }
                        ) {
                            Text(day.weekday, color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(day.date, color = if (day.isToday) Color(0xFFD0BCFF) else Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // Title section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Socials & Rooms",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "${events.size} Events Scheduled",
                    color = Color.White.copy(0.4f),
                    fontSize = 11.sp
                )
            }

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, tint = Color.White.copy(0.2f), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No live events scheduled.", color = Color.White.copy(0.4f), fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("calendar_events_list"),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(events) { event ->
                        val rsvpStatus = rsvps[event.id] ?: "RSVP Now"
                        EventItemCard(
                            event = event,
                            rsvpStatus = rsvpStatus,
                            onClick = { selectedEventIdForDetail = event.id }
                        )
                    }
                }
            }
        }

        // Event Creation Wizard Dialog
        if (showCreateDialog) {
            var title by remember { mutableStateOf("") }
            var desc by remember { mutableStateOf("") }
            var date by remember { mutableStateOf("Aug 26, 2026 • 7:00 PM") }
            var location by remember { mutableStateOf("Lumi Space Room Alpha") }
            var isOnline by remember { mutableStateOf(true) }
            var ticketPrice by remember { mutableStateOf("Free") }

            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                containerColor = Color(0xFF0F0C21),
                titleContentColor = Color.White,
                textContentColor = Color.White,
                title = { Text("Event Creation Wizard", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Event Title") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("wizard_input_title")
                        )
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Short Description") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("wizard_input_desc")
                        )
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date & Time") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location details") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Online Virtual Room", color = Color.White, fontSize = 12.sp)
                            Switch(
                                checked = isOnline,
                                onCheckedChange = { isOnline = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF8E2DE2))
                            )
                        }

                        OutlinedTextField(
                            value = ticketPrice,
                            onValueChange = { ticketPrice = it },
                            label = { Text("Ticket Price (e.g., Free, 10.00)") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val price = ticketPrice.replace("$", "").trim().toDoubleOrNull() ?: 0.0
                                viewModel.createEvent(title, desc, date, location, isOnline, price)
                                showCreateDialog = false
                                alertMessage = "Event successfully scheduled! Notifications scheduled for RSVPs."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.testTag("wizard_submit_btn")
                    ) {
                        Text("Launch Event")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel", color = Color.White.copy(0.6f))
                    }
                }
            )
        }

        // Event Detail & RSVP Sheet Dialog
        selectedEventIdForDetail?.let { evId ->
            val event = events.find { it.id == evId }
            if (event != null) {
                val currentRsvp = rsvps[event.id] ?: "No Response"

                AlertDialog(
                    onDismissRequest = { selectedEventIdForDetail = null },
                    containerColor = Color(0xFF0C071A),
                    titleContentColor = Color.White,
                    textContentColor = Color.White,
                    title = { Text(event.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = event.bannerUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text("Date & Time", fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF), fontSize = 11.sp)
                            Text(event.date, color = Color.White, fontSize = 13.sp)

                            Text("Location Details", fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF), fontSize = 11.sp)
                            Text(
                                text = if (event.isOnline) "🌐 Online Video Room: ${event.location}" else "📍 ${event.location} (Lat: 37.77, Lng: -122.41)",
                                color = Color.White,
                                fontSize = 13.sp
                            )

                            Text("Description", fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF), fontSize = 11.sp)
                            Text(event.desc, color = Color.White.copy(0.7f), fontSize = 12.sp, lineHeight = 16.sp)

                            Text("Attendance Tracker", fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF), fontSize = 11.sp)
                            Text("👥 ${event.attendeesCount} users attending • Ticket: ${if (event.ticketPrice == 0.0) "Free" else "$${event.ticketPrice}"}", color = Color.White, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(8.dp))

                            Divider(color = Color.White.copy(0.1f))

                            Spacer(modifier = Modifier.height(4.dp))

                            Text("Update RSVP Status", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Attending", "Maybe", "Declined").forEach { status ->
                                    val isSelected = currentRsvp == status
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) Color(0xFF8E2DE2).copy(0.2f) else Color.White.copy(0.04f))
                                            .border(1.dp, if (isSelected) Color(0xFF8E2DE2) else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable {
                                                viewModel.rsvpToEvent(event.id, status)
                                                alertMessage = "Reminders configured. You selected: $status!"
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(status, color = if (isSelected) Color(0xFFD0BCFF) else Color.White.copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Set reminder button
                            Button(
                                onClick = {
                                    alertMessage = "🔔 Instant push alert & system calendar invitation scheduled for August 26!"
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("schedule_alert_reminder"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF).copy(0.2f))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Dynamic Smart Alarm Reminder", color = Color(0xFF00E5FF), fontSize = 11.sp)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { selectedEventIdForDetail = null }) {
                            Text("Close", color = Color(0xFFD0BCFF))
                        }
                    }
                )
            }
        }

        // Universal Toast Alert Dialog Box
        alertMessage?.let { alert ->
            AlertDialog(
                onDismissRequest = { alertMessage = null },
                containerColor = Color(0xFF0F0C21),
                titleContentColor = Color.White,
                textContentColor = Color.White,
                title = { Text("Agenda Scheduler Alert", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
                text = { Text(alert, fontSize = 13.sp) },
                confirmButton = {
                    TextButton(onClick = { alertMessage = null }) {
                        Text("Okay", color = Color(0xFFD0BCFF))
                    }
                }
            )
        }
    }
}

@Composable
fun EventItemCard(event: Event, rsvpStatus: String, onClick: () -> Unit) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("event_item_card_${event.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = event.bannerUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White.copy(0.4f), modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(event.date, color = Color.White.copy(0.5f), fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White.copy(0.4f), modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (event.isOnline) "Lumi Video Hub" else event.location,
                        color = Color.White.copy(0.5f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (rsvpStatus) {
                                "Attending" -> Color(0xFF4CAF50).copy(0.15f)
                                "Maybe" -> Color(0xFFFFB300).copy(0.15f)
                                "Declined" -> Color(0xFFFF5252).copy(0.15f)
                                else -> Color(0xFF8E2DE2).copy(0.15f)
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = when (rsvpStatus) {
                                "Attending" -> Color(0xFF4CAF50)
                                "Maybe" -> Color(0xFFFFB300)
                                "Declined" -> Color(0xFFFF5252)
                                else -> Color(0xFF8E2DE2)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = rsvpStatus,
                        color = when (rsvpStatus) {
                            "Attending" -> Color(0xFF4CAF50)
                            "Maybe" -> Color(0xFFFFB300)
                            "Declined" -> Color(0xFFFF5252)
                            else -> Color(0xFFD0BCFF)
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "👥 ${event.attendeesCount} attending",
                    color = Color.White.copy(0.3f),
                    fontSize = 9.sp
                )
            }
        }
    }
}

private data class CalendarDay(
    val weekday: String,
    val date: String,
    val isToday: Boolean
)
