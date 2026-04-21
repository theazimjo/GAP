package abs.uits.gap.ui.create

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import abs.uits.gap.ui.group.GroupViewModel
import abs.uits.gap.ui.group.CreateGroupState

// Core Colors
val ThemeRust = Color(0xFFB24F2C)
val IosGrayBackground = Color(0xFFF2F2F7)
val IosLabelGray = Color(0xFF8E8E93)
val IosDividerColor = Color(0xFFC6C6C8)

@Composable
fun CreateScreen(
    viewModel: GroupViewModel,
    onNavigateBack: () -> Unit,
    onViewChange: (String) -> Unit = {}
) {
    var currentView by remember { mutableStateOf("selection") }
    
    LaunchedEffect(currentView) {
        onViewChange(currentView)
    }

    Box(modifier = Modifier.fillMaxSize().background(IosGrayBackground)) {
        AnimatedContent(
            targetState = currentView,
            transitionSpec = {
                val enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(400, easing = FastOutSlowInEasing)) + fadeIn()
                val exit = fadeOut() + slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300))
                
                if (targetState != "selection") {
                    enter togetherWith exit
                } else {
                    fadeIn() togetherWith exit
                }
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
private fun IosTopBar(
    title: String,
    leftAction: String? = null,
    onLeftClick: () -> Unit = {},
    rightAction: String? = null,
    onRightClick: () -> Unit = {},
    isLoading: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(52.dp),
        color = Color.White.copy(alpha = 0.98f),
        border = BorderStroke(0.3.dp, IosDividerColor.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
            if (leftAction != null) {
                Text(
                    text = leftAction,
                    color = ThemeRust,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(horizontal = 8.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onLeftClick
                        )
                )
            }
            
            Text(
                title,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            
            if (rightAction != null) {
                Text(
                    text = rightAction,
                    color = if (isLoading) ThemeRust.copy(alpha = 0.5f) else ThemeRust,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 8.dp)
                        .clickable(
                            enabled = !isLoading,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onRightClick
                        )
                )
            }
        }
    }
}

@Composable
private fun IosSection(title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        if (title != null) {
            Text(
                title.uppercase(),
                modifier = Modifier.padding(start = 28.dp, bottom = 6.dp),
                fontSize = 12.sp,
                color = IosLabelGray,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp
            )
        }
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun IosRow(
    icon: ImageVector? = null,
    iconColor: Color = Color.Gray,
    title: String,
    subtitle: String? = null,
    showChevron: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    var modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp)
    if (onClick != null) modifier = modifier.clickable { onClick() }

    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Surface(
                modifier = Modifier.size(29.dp),
                shape = RoundedCornerShape(7.dp),
                color = iconColor
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.padding(5.dp))
            }
            Spacer(modifier = Modifier.width(15.dp))
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 17.sp, color = Color.Black)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = IosLabelGray)
            }
        }
        
        content?.invoke()
        
        if (showChevron) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = IosDividerColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SelectionView(onCreateSelected: () -> Unit, onJoinSelected: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 80.dp)) {
        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp), contentAlignment = Alignment.Center) {
            Text(
                "GAP",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                letterSpacing = (-2).sp
            )
        }

        IosSection(title = "Boshlash") {
            IosRow(
                icon = Icons.Default.Add,
                iconColor = Color(0xFF34C759),
                title = "Guruh Yaratish",
                subtitle = "Yangi guruhni boshqarish",
                showChevron = true,
                onClick = onCreateSelected
            )
            HorizontalDivider(modifier = Modifier.padding(start = 60.dp), thickness = 0.5.dp, color = IosDividerColor)
            IosRow(
                icon = Icons.Default.GroupAdd,
                iconColor = ThemeRust,
                title = "Guruhga Qo'shilish",
                subtitle = "Kod orqali a'zo bo'lish",
                showChevron = true,
                onClick = onJoinSelected
            )
        }
    }
}

@Composable
private fun CreateFormView(viewModel: GroupViewModel, onBack: () -> Unit, onCreated: () -> Unit) {
    val createState by viewModel.createState.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🤝") }
    var isOptionalAmount by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("500000") }
    var meetingDays by remember { mutableStateOf("10, 25") }
    var selectionMethod by remember { mutableStateOf("random") }
    
    val emojis = remember { listOf("🤝", "💰", "🏠", "🎓", "🚗", "✈️", "👨‍👩", "🎉", "🔥", "🌈", "🍎", "🏀", "⚽", "🎮", "🎸", "📚") }

    LaunchedEffect(createState) {
        if (createState is CreateGroupState.Success) {
            onCreated()
            viewModel.resetCreateState()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        IosTopBar(
            title = "Yangi guruh",
            leftAction = "Bekor qilish",
            onLeftClick = onBack,
            rightAction = "Yaratish",
            onRightClick = {
                viewModel.createGroup(
                    name = name,
                    emoji = selectedEmoji,
                    description = description,
                    isAmountOptional = isOptionalAmount,
                    contributionAmount = if (isOptionalAmount) 0.0 else (amount.toDoubleOrNull() ?: 0.0),
                    meetingDays = meetingDays,
                    selectionMethod = selectionMethod
                )
            },
            isLoading = createState is CreateGroupState.Loading
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 20.dp)
        ) {
            IosSection {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(86.dp),
                        shape = CircleShape,
                        color = IosGrayBackground
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(selectedEmoji, fontSize = 50.sp)
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(emojis, key = { it }) { emoji ->
                        EmojiItem(
                            emoji = emoji,
                            isSelected = selectedEmoji == emoji,
                            onSelect = { selectedEmoji = emoji }
                        )
                    }
                }
            }

            IosSection(title = "Ma'lumotlar") {
                IosInputRow(label = "Guruh nomi", value = name, onValueChange = { name = it }, placeholder = "Nomi")
                HorizontalDivider(modifier = Modifier.padding(start = 16.dp), thickness = 0.5.dp, color = IosDividerColor)
                IosInputRow(label = "Tavsif", value = description, onValueChange = { description = it }, placeholder = "Ixtiyoriy")
            }

            IosSection(title = "Mablag' va jadval") {
                IosRow(title = "Ixtiyoriy miqdor") {
                    Switch(
                        checked = isOptionalAmount,
                        onCheckedChange = { isOptionalAmount = it },
                        modifier = Modifier.scale(0.8f),
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF34C759))
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(start = 16.dp), thickness = 0.5.dp, color = IosDividerColor)
                IosInputRow(
                    label = "Miqdor",
                    value = amount, 
                    onValueChange = { amount = it }, 
                    placeholder = "500 000", 
                    keyboardType = KeyboardType.Number,
                    enabled = !isOptionalAmount
                )
                HorizontalDivider(modifier = Modifier.padding(start = 16.dp), thickness = 0.5.dp, color = IosDividerColor)
                IosInputRow(
                    label = "Sana",
                    value = meetingDays, 
                    onValueChange = { meetingDays = it }, 
                    placeholder = "10, 25", 
                    subtitle = "Oyni qaysi kunlari (masalan: 1, 15)"
                )
            }

            IosSection(title = "Tanlash usuli") {
                Box(modifier = Modifier.padding(12.dp)) {
                    IosSegmentedControl(
                        options = listOf("random" to "Tasodifiy", "manual" to "Qo'lda"),
                        selectedId = selectionMethod,
                        onSelected = { selectionMethod = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmojiItem(emoji: String, isSelected: Boolean, onSelect: () -> Unit) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (isSelected) ThemeRust.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onSelect() },
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 26.sp, modifier = Modifier.alpha(if (isSelected) 1f else 0.8f))
    }
}

@Composable
private fun IosSegmentedControl(options: List<Pair<String, String>>, selectedId: String, onSelected: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(32.dp).padding(horizontal = 2.dp),
        color = Color(0x1F767680),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(2.dp)) {
            options.forEach { (id, label) ->
                val isSelected = selectedId == id
                Surface(
                    modifier = Modifier.weight(1f).fillMaxHeight().clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelected(id) }
                    ),
                    color = if (isSelected) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(7.dp),
                    shadowElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(label, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun IosInputRow(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, subtitle: String? = null, keyboardType: KeyboardType = KeyboardType.Text, enabled: Boolean = true) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 17.sp, color = Color.Black, modifier = Modifier.width(110.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = IosLabelGray.copy(alpha = 0.5f), fontSize = 17.sp) },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = ThemeRust
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End, fontSize = 17.sp, color = if (enabled) Color.Black else IosLabelGray),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
            )
        }
        if (subtitle != null) {
            Text(subtitle, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp), fontSize = 13.sp, color = IosLabelGray)
        }
    }
}

@Composable
private fun JoinView(onBack: () -> Unit) {
    var code by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize()) {
        IosTopBar(title = "Qo'shilish", leftAction = "Bekor qilish", onLeftClick = onBack)

        Column(modifier = Modifier.fillMaxSize().padding(top = 20.dp)) {
            IosSection(title = "Taklif kodi") {
                IosInputRow(label = "ID-kod", value = code, onValueChange = { code = it }, placeholder = "#GAP123")
            }
            
            IosSection {
                IosRow(icon = Icons.Default.QrCodeScanner, iconColor = ThemeRust, title = "QR-kodni skanerlash", showChevron = true, onClick = { /* QR */ })
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { /* Join */ },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ThemeRust),
                enabled = code.isNotBlank()
            ) {
                Text("Qo'shilish", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun Modifier.scale(scale: Float): Modifier = this.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        placeable.placeRelativeWithLayer(0, 0) {
            scaleX = scale
            scaleY = scale
        }
    }
}
