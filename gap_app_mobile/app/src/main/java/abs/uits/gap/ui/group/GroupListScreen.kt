package abs.uits.gap.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

    val bluePrimary = Color(0xFF1976D2)
    val lightGray = Color(0xFFF0F4F8)

    LaunchedEffect(createState) {
        if (createState is CreateGroupState.Success) {
            showCreateModal = false
            scope.launch { snackbarHostState.showSnackbar("GAP Muvaffaqiyatli yaratildi") }
            groupViewModel.resetCreateState()
        } else if (createState is CreateGroupState.Error) {
            scope.launch { snackbarHostState.showSnackbar((createState as CreateGroupState.Error).message) }
            groupViewModel.resetCreateState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = lightGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mening GAPlarim", color = bluePrimary, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateModal = true },
                containerColor = bluePrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yangi GAP yaratish")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = listState) {
                is GroupListState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = bluePrimary
                    )
                }
                is GroupListState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { groupViewModel.fetchGroups() }) {
                            Text("Qayta yuklash")
                        }
                    }
                }
                is GroupListState.Success -> {
                    if (state.groups.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Sizda hali guruhlar yo'q", color = Color.Gray, fontSize = 16.sp)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.groups) { group ->
                                GroupCard(
                                    group = group,
                                    onClick = { onNavigateToDetail(group.id) }
                                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCard(group: GroupDto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFFE3F2FD), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(group.name.take(1).uppercase(), color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(group.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tashkilotchi: ${group.creator.name}", fontSize = 12.sp, color = Color.Gray)
                }
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "${group._count.members} kishi",
                        color = Color(0xFF00796B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // We hide Pool and Amount since they are defaulted to 0 now, 
            // but we can uncomment them later if needed.
            /* 
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF0F4F8))
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Yig'im miqdori", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(formatMoney(group.contributionAmount), fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Umumiy qozon", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(formatMoney(group.totalPool), fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), fontSize = 16.sp)
                }
            }
            */
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text("Yangi GAP Ochish", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Guruh nomi") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        onCreate(name)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = !isCreating,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                if (isCreating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Tasdiqlash", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

fun formatMoney(amount: Double): String {
    return "%,.0f UZS".format(amount).replace(',', ' ')
}
