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
    
    // Premium Theme Colors
    val themeRust = Color(0xFFB24F2C)
    val themeBeige = Color(0xFFF2EEE4)
    val themeText = Color(0xFF424242)
    val themeSecondaryText = Color(0xFF757575)

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
        containerColor = themeBeige,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    val titleText = if (state is GroupDetailState.Success) {
                        (state as GroupDetailState.Success).group.name
                    } else {
                        "Guruh"
                    }
                    Text(titleText, color = themeText, fontWeight = FontWeight.Bold, fontSize = 18.sp) 
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp), tint = themeRust)
                            Text("Orqaga", color = themeRust, fontSize = 17.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showAddMemberModal = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = themeRust)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = themeBeige.copy(alpha = 0.95f)
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentState = state) {
                is GroupDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = themeRust
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
                            Text("Qayta urinish", color = themeRust)
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
                                fontWeight = FontWeight.Bold,
                                color = themeSecondaryText,
                                modifier = Modifier.padding(start = 24.dp, bottom = 12.dp)
                            )
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column {
                                    group.members.forEachIndexed { index, member ->
                                        MemberItem(member = member)
                                        if (index < group.members.size - 1) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(start = 64.dp),
                                                color = themeBeige,
                                                thickness = 1.dp
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
        containerColor = Color(0xFFF2EEE4),
        dragHandle = null
    ) {
        val themeRust = Color(0xFFB24F2C)
        val themeText = Color(0xFF424242)
        val themeSecondaryText = Color(0xFF757575)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
        ) {
            // Premium Modal Header
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
                    text = "A'zo qo'shish",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeText
                )
                if (isLoading) {
                    CircularProgressIndicator(color = themeRust, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Qo'shish",
                        color = if (phone.length == 9) themeRust else themeSecondaryText.copy(alpha = 0.4f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(enabled = phone.length == 9) { onAdd(phone) }
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("+998", fontSize = 17.sp, color = themeText, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = phone,
                        onValueChange = { if (it.length <= 9) phone = it },
                        placeholder = { Text("000000000", color = themeSecondaryText.copy(alpha = 0.5f)) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = themeRust
                        ),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 17.sp, color = themeText)
                    )
                    IconButton(onClick = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }) {
                        Icon(Icons.Default.Contacts, contentDescription = "Contacts", tint = themeRust)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
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
    val themeRust = Color(0xFFB24F2C)
    val themeText = Color(0xFF424242)
    val themeSecondaryText = Color(0xFF757575)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(themeRust.copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = themeRust, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(member.user.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = themeText)
            Text(member.user.phone, color = themeSecondaryText, fontSize = 13.sp)
        }
        if (member.role == "admin") {
            Surface(
                color = themeRust.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    "Admin",
                    color = themeRust,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
