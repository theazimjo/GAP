package abs.uits.gap.ui.create

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import abs.uits.gap.ui.group.GroupViewModel
import abs.uits.gap.ui.group.CreateGroupState

@Composable
fun CreateScreen(
    viewModel: GroupViewModel,
    onNavigateBack: () -> Unit
) {
    var currentView by remember { mutableStateOf("selection") } // "selection" or "create" or "join"
    
    val themeBeige = Color(0xFFF2EEE4)

    Box(modifier = Modifier.fillMaxSize().background(themeBeige)) {
        AnimatedContent(
            targetState = currentView,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "CreateFlowTransition"
        ) { view ->
            when (view) {
                "selection" -> SelectionView(
                    onCreateSelected = { currentView = "create" },
                    onJoinSelected = { currentView = "join" }
                )
                "create" -> CreateFormView(
                    viewModel = viewModel,
                    onBack = { currentView = "selection" },
                    onCreated = onNavigateBack
                )
                "join" -> JoinView(
                    onBack = { currentView = "selection" }
                )
            }
        }
    }
}

@Composable
fun SelectionView(onCreateSelected: () -> Unit, onJoinSelected: () -> Unit) {
    val themeRust = Color(0xFFB24F2C)
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "GAP boshlang",
            fontSize = 32.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        SelectionCard(
            title = "Guruh Yaratish",
            description = "O'zingizning yangi GAP guruhingizni shakllantiring va do'stlaringizni taklif qiling.",
            icon = Icons.Default.AddCircle,
            onClick = onCreateSelected,
            color = themeRust
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SelectionCard(
            title = "Guruhga Qo'shilish",
            description = "Mavjud guruhga ID yoki QR-kod orqali a'zo bo'ling.",
            icon = Icons.Default.GroupAdd,
            onClick = onJoinSelected,
            color = Color(0xFF424242)
        )
    }
}

@Composable
fun SelectionCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit, color: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = color.copy(alpha = 0.1f)
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 14.sp, color = Color(0xFF757575))
            }
        }
    }
}

@Composable
fun CreateFormView(viewModel: GroupViewModel, onBack: () -> Unit, onCreated: () -> Unit) {
    val themeRust = Color(0xFFB24F2C)
    val themeSecondaryText = Color(0xFF757575)
    val createState by viewModel.createState.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🤝") }
    var isOptionalAmount by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("500000") }
    var meetingDays by remember { mutableStateOf("10,25") }
    
    val emojis = listOf("🤝", "💰", "🏠", "🎓", "🚗", "✈️", "👨‍👩‍👧‍👦", "🎉", "💎", "🌙")

    LaunchedEffect(createState) {
        if (createState is CreateGroupState.Success) {
            onCreated()
            viewModel.resetCreateState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Guruh Yaratish",
                fontSize = 28.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Emoji", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            emojis.forEach { emoji ->
                EmojiItem(
                    emoji = emoji,
                    isSelected = selectedEmoji == emoji,
                    onClick = { selectedEmoji = emoji }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        PremiumTextField(value = name, onValueChange = { name = it }, placeholder = "Guruh nomi")
        Spacer(modifier = Modifier.height(16.dp))
        PremiumTextField(value = description, onValueChange = { description = it }, placeholder = "Tavsif")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ixtiyoriy miqdor", fontWeight = FontWeight.Medium)
                Switch(
                    checked = isOptionalAmount,
                    onCheckedChange = { isOptionalAmount = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = themeRust, checkedTrackColor = themeRust.copy(alpha = 0.3f))
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Tavsiya etilgan miqdor (so'm)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        PremiumTextField(
            value = amount, 
            onValueChange = { amount = it }, 
            placeholder = "500000",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(
            "A'zolar boshqa miqdor ham belgilashi mumkin",
            fontSize = 12.sp,
            color = themeSecondaryText,
            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Yig'ilish kunlari (1-31)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        PremiumTextField(value = meetingDays, onValueChange = { meetingDays = it }, placeholder = "10,25")
        Text(
            "Comma-separated days of month",
            fontSize = 12.sp,
            color = themeSecondaryText,
            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Tanlash usuli", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🎲 Tasodifiy tanlov", color = Color(0xFF424242))
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Button(
            onClick = {
                viewModel.createGroup(
                    name = name,
                    emoji = selectedEmoji,
                    description = description,
                    isAmountOptional = isOptionalAmount,
                    contributionAmount = amount.toDoubleOrNull() ?: 0.0,
                    meetingDays = meetingDays
                )
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeRust),
            enabled = name.isNotBlank() && createState !is CreateGroupState.Loading
        ) {
            if (createState is CreateGroupState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Guruh Yaratish", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun EmojiItem(emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    val themeRust = Color(0xFFB24F2C)
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) themeRust.copy(alpha = 0.1f) else Color.White)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) themeRust else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 20.sp)
    }
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF757575).copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFFB24F2C)
            ),
            singleLine = true,
            keyboardOptions = keyboardOptions
        )
    }
}

@Composable
fun JoinView(onBack: () -> Unit) {
    val themeRust = Color(0xFFB24F2C)
    var code by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Guruhga Qo'shilish",
                fontSize = 28.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            "Guruh ID-sini kiriting:",
            fontSize = 17.sp,
            color = Color(0xFF424242),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        PremiumTextField(value = code, onValueChange = { code = it }, placeholder = "M: #GAP123")
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { /* TODO: Join Logic */ },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeRust)
        ) {
            Text("Qo'shilish", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Yoki QR-kodni skanerlang",
            fontSize = 14.sp,
            color = Color(0xFF757575),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
