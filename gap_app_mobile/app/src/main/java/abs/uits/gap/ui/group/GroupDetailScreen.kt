package abs.uits.gap.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    viewModel: GroupDetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.detailState.collectAsState()
    
    // iOS System Colors
    val iosBlue = Color(0xFF007AFF)
    val iosBg = Color(0xFFF2F2F7)
    val iosGray = Color(0xFF8E8E93)

    Scaffold(
        containerColor = iosBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    val titleText = if (state is GroupDetailState.Success) {
                        (state as GroupDetailState.Success).group.name
                    } else {
                        "Guruh"
                    }
                    Text(titleText, color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 17.sp) 
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp), tint = iosBlue)
                            Text("Orqaga", color = iosBlue, fontSize = 17.sp)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add member logic */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = iosBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentState = state) {
                is GroupDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = iosBlue
                    )
                }
                is GroupDetailState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(currentState.message, color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { viewModel.fetchGroupDetail() }) {
                            Text("Qayta urinish", color = iosBlue)
                        }
                    }
                }
                is GroupDetailState.Success -> {
                    val group = currentState.group

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "A'ZOLAR (${group.members.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = iosGray,
                                modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                            )
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column {
                                    group.members.forEachIndexed { index, member ->
                                        MemberItem(member = member)
                                        if (index < group.members.size - 1) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(start = 56.dp),
                                                color = Color(0xFFC6C6C8),
                                                thickness = 0.5.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemberItem(member: abs.uits.gap.core.network.MemberDto) {
    val iosGray = Color(0xFF8E8E93)
    val iosBlue = Color(0xFF007AFF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFE5E5EA), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = iosGray, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(member.user.name, fontWeight = FontWeight.Normal, fontSize = 17.sp, color = Color.Black)
            Text(member.user.phone, color = iosGray, fontSize = 13.sp)
        }
        if (member.role == "admin") {
            Text(
                "Admin",
                color = iosBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
