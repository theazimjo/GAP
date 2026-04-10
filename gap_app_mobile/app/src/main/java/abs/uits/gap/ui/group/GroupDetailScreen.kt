package abs.uits.gap.ui.group

import android.Manifest
import android.content.Context
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    viewModel: GroupDetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.detailState.collectAsState()
    val addMemberState by viewModel.addMemberState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showAddMemberModal by remember { mutableStateOf(false) }
    
    // iOS System Colors
    val iosBlue = Color(0xFF007AFF)
    val iosBg = Color(0xFFF2F2F7)
    val iosGray = Color(0xFF8E8E93)

    LaunchedEffect(addMemberState) {
        if (addMemberState is AddMemberState.Success) {
            showAddMemberModal = false
            scope.launch { snackbarHostState.showSnackbar("A'zo muvaffaqiyatli qo'shildi ✨") }
            viewModel.resetAddMemberState()
        } else if (addMemberState is AddMemberState.Error) {
            scope.launch { snackbarHostState.showSnackbar((addMemberState as AddMemberState.Error).message) }
            viewModel.resetAddMemberState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    IconButton(onClick = { showAddMemberModal = true }) {
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

    if (showAddMemberModal) {
        AddMemberModal(
            onDismiss = { showAddMemberModal = false },
            onAdd = { phone ->
                viewModel.addMember(phone)
            },
            isLoading = addMemberState is AddMemberState.Loading
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberModal(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    isLoading: Boolean
) {
    var phone by remember { mutableStateOf("") }
    val iosBlue = Color(0xFF007AFF)
    val context = LocalContext.current

    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        uri?.let {
            val contactPhone = extractPhoneNumber(context, it)
            contactPhone?.let { fullNum ->
                // Clean the number and extract last 9 digits if it's Uzbek
                val cleaned = fullNum.replace(Regex("[^0-9]"), "")
                phone = if (cleaned.length >= 9) {
                    cleaned.takeLast(9)
                } else {
                    cleaned
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickContactLauncher.launch(null)
        }
    }

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
                    text = "A'zo qo'shish",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                if (isLoading) {
                    CircularProgressIndicator(color = iosBlue, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Qo'shish",
                        color = if (phone.length == 9) iosBlue else Color(0xFF8E8E93),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(enabled = phone.length == 9) { onAdd(phone) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // iOS Input Row
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("+998", fontSize = 17.sp, color = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = phone,
                        onValueChange = { if (it.length <= 9) phone = it },
                        placeholder = { Text("000000000", color = Color(0xFFC7C7CC)) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = iosBlue
                        ),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 17.sp)
                    )
                    IconButton(onClick = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }) {
                        Icon(Icons.Default.Contacts, contentDescription = "Contacts", tint = iosBlue)
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFC6C6C8), thickness = 0.5.dp)
        }
    }
}

private fun extractPhoneNumber(context: Context, uri: android.net.Uri): String? {
    var phoneNumber: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            val hasPhoneNumber = it.getInt(it.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
            if (hasPhoneNumber > 0) {
                val phones = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null,
                    null
                )
                phones?.use { pCursor ->
                    if (pCursor.moveToFirst()) {
                        phoneNumber = pCursor.getString(pCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    }
                }
            }
        }
    }
    return phoneNumber
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
