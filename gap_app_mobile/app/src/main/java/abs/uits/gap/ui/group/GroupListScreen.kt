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
import androidx.compose.material.icons.filled.Chat
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

    // Premium Theme Colors
    val themeRust = Color(0xFFB24F2C)
    val themeBeige = Color(0xFFF2EEE4)
    val themeSecondaryText = Color(0xFF757575)

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
        containerColor = themeBeige,
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        "Mening GAPlarim", 
                        color = Color(0xFF424242), 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 32.sp
                    )
                },
                actions = {
                    IconButton(onClick = { showCreateModal = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = themeRust)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = themeBeige,
                    scrolledContainerColor = themeBeige.copy(alpha = 0.95f)
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = listState) {
                is GroupListState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = themeRust
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
                            Text("Qayta yuklash", color = themeRust)
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
                                Icons.Default.Chat, 
                                contentDescription = null, 
                                modifier = Modifier.size(80.dp), 
                                tint = themeRust.copy(alpha = 0.1f)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Hali GAPlar yo'q", color = themeSecondaryText, fontSize = 17.sp)
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
    val themeRust = Color(0xFFB24F2C)
    val themeSecondaryText = Color(0xFF757575)

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
                .size(44.dp)
                .background(themeRust.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                group.name.take(1).uppercase(),
                color = themeRust,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                group.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF424242)
            )
            Text(
                "Tashkilotchi: ${group.creator.name}",
                fontSize = 13.sp,
                color = themeSecondaryText
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${group._count.members}",
                color = themeSecondaryText,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = themeRust.copy(alpha = 0.3f)
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
    val themeRust = Color(0xFFB24F2C)
    val themeBeige = Color(0xFFF2EEE4)
    val themeSecondaryText = Color(0xFF757575)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = themeBeige,
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
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bekor qilish",
                    color = themeSecondaryText,
                    fontSize = 17.sp,
                    modifier = Modifier.clickable { onDismiss() }
                )
                Text(
                    text = "Yangi GAP",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )
                if (isCreating) {
                    CircularProgressIndicator(color = themeRust, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Yaratish",
                        color = if (name.isNotBlank()) themeRust else themeSecondaryText.copy(alpha = 0.4f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(enabled = name.isNotBlank()) { onCreate(name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Premium Input Row
            Surface(
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Guruh nomi", color = themeSecondaryText.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = themeRust
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 17.sp, color = Color(0xFF424242))
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
