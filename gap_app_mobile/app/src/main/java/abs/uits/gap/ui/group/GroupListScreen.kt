package abs.uits.gap.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import abs.uits.gap.core.network.GroupDto
import abs.uits.gap.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupListScreen(
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val listState by groupViewModel.listState.collectAsState()
    val createState by groupViewModel.createState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showCreateModal by remember { mutableStateOf(false) }

    // iOS System Colors
    val iosBlue = Color(0xFF007AFF)
    val iosBg = Color(0xFFF2F2F7)
    val iosGray = Color(0xFF8E8E93)

    LaunchedEffect(createState) {
        if (createState is CreateGroupState.Success) {
            showCreateModal = false
            scope.launch { snackbarHostState.showSnackbar("GAP Muvaffaqiyatli yaratildi ✨") }
            groupViewModel.resetCreateState()
        } else if (createState is CreateGroupState.Error) {
            scope.launch { snackbarHostState.showSnackbar((createState as CreateGroupState.Error).message) }
            groupViewModel.resetCreateState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = iosBg,
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        "Mening GAPlarim", 
                        color = Color.Black, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 34.sp
                    )
                },
                actions = {
                    IconButton(onClick = { showCreateModal = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = iosBlue)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = iosBg,
                    scrolledContainerColor = Color.White.copy(alpha = 0.95f)
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = listState) {
                is GroupListState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = iosBlue
                    )
                }
                is GroupListState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { groupViewModel.fetchGroups() }) {
                            Text("Qayta yuklash", color = iosBlue)
                        }
                    }
                }
                is GroupListState.Success -> {
                    if (state.groups.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Group, 
                                contentDescription = null, 
                                modifier = Modifier.size(64.dp), 
                                tint = iosGray.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Hali guruhlar yo'q", color = iosGray, fontSize = 17.sp)
                        }
                    } else {
                        // iOS Inset Grouped Style
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column {
                                state.groups.forEachIndexed { index, group ->
                                    GroupItem(
                                        group = group,
                                        onClick = { onNavigateToDetail(group.id) }
                                    )
                                    if (index < state.groups.size - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(start = 64.dp),
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

    if (showCreateModal) {
        CreateGroupModal(
            onDismiss = { showCreateModal = false },
            onCreate = { name ->
                groupViewModel.createGroup(name)
            },
            isCreating = createState is CreateGroupState.Loading
        )
    }
}

@Composable
fun GroupItem(group: GroupDto, onClick: () -> Unit) {
    val iosBlue = Color(0xFF007AFF)
    val iosGray = Color(0xFF8E8E93)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon / Initial
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iosBlue, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                group.name.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                group.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            Text(
                "Tashkilotchi: ${group.creator.name}",
                fontSize = 13.sp,
                color = iosGray
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${group._count.members}",
                color = iosGray,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFFC7C7CC)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupModal(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
    isCreating: Boolean
) {
    var name by remember { mutableStateOf("") }
    val iosBlue = Color(0xFF007AFF)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF2F2F7),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
        ) {
            // iOS Modal Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bekor qilish",
                    color = iosBlue,
                    fontSize = 17.sp,
                    modifier = Modifier.clickable { onDismiss() }
                )
                Text(
                    text = "Yangi guruh",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                if (isCreating) {
                    CircularProgressIndicator(color = iosBlue, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Yaratish",
                        color = if (name.isNotBlank()) iosBlue else Color(0xFF8E8E93),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(enabled = name.isNotBlank()) { onCreate(name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // iOS Input Row
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Guruh nomi", color = Color(0xFFC7C7CC)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = iosBlue
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 17.sp)
                )
            }
            HorizontalDivider(color = Color(0xFFC6C6C8), thickness = 0.5.dp)
        }
    }
}
