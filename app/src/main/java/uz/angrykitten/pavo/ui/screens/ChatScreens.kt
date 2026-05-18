package uz.angrykitten.pavo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import uz.angrykitten.pavo.ui.localization.tr
import uz.angrykitten.pavo.ui.navigation.Screen
import uz.angrykitten.pavo.ui.theme.AccentGold
import uz.angrykitten.pavo.ui.theme.AccentSky
import uz.angrykitten.pavo.ui.theme.Brand
import uz.angrykitten.pavo.ui.theme.BrandLight
import uz.angrykitten.pavo.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)

data class ChatPreview(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String,
    val timestamp: Long
)

private val ChatCardShape = RoundedCornerShape(28.dp)

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ChatScreen(viewModel: AppViewModel, navController: NavController) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentUserId by viewModel.userId.collectAsStateWithLifecycle()

    if (!isLoggedIn || currentUserId == null) {
        GuestPromptScreen(
            tr("Chat", "Chats", "Чаты"),
            tr("Chatdan foydalanish uchun tizimga kiring", "Sign in to use chats", "Войдите, чтобы пользоваться чатами"),
            navController
        )
        return
    }

    var chatPreviews by remember { mutableStateOf<List<ChatPreview>>(emptyList()) }
    val database = remember { FirebaseDatabase.getInstance().reference }

    DisposableEffect(currentUserId) {
        val uid = currentUserId
        if (uid == null) {
            onDispose { }
        } else {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val previews = buildList {
                        snapshot.children.forEach { chatSnap ->
                            val chatId = chatSnap.key ?: return@forEach
                            if (!chatId.contains(uid)) return@forEach

                            val ids = chatId.split("_")
                            val otherId = ids.firstOrNull { it != uid } ?: return@forEach
                            val lastMsgSnap = chatSnap.child("messages").children.lastOrNull()
                            val otherName = chatSnap.child("participants").child(otherId)
                                .child("name")
                                .getValue(String::class.java)
                                ?: otherId

                            add(
                                ChatPreview(
                                    chatId = chatId,
                                    otherUserId = otherId,
                                    otherUserName = otherName,
                                    lastMessage = lastMsgSnap?.child("text")?.getValue(String::class.java).orEmpty(),
                                    timestamp = lastMsgSnap?.child("timestamp")?.getValue(Long::class.java) ?: 0L
                                )
                            )
                        }
                    }
                    chatPreviews = previews.sortedByDescending { it.timestamp }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            }

            val chatsRef = database.child("chats")
            chatsRef.addValueEventListener(listener)
            onDispose { chatsRef.removeEventListener(listener) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(tr("Xabarlar", "Messages", "Сообщения"), fontWeight = FontWeight.Bold)
                        Text(
                            text = tr("Sotuvchilar va xaridorlar bilan suhbat", "Talk to sellers and buyers", "Общайтесь с продавцами и покупателями"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Orqaga", "Back", "Назад"))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (chatPreviews.isEmpty()) {
            EmptyChatState(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatPreviews, key = { it.chatId }) { preview ->
                    ChatPreviewCard(
                        preview = preview,
                        onClick = {
                            navController.navigate(
                                Screen.ChatDetail.createRoute(
                                    preview.chatId,
                                    preview.otherUserId,
                                    preview.otherUserName
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyChatState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = ChatCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(BrandLight, AccentSky.copy(alpha = 0.45f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = Brand,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Text(tr("Hali xabarlar yo'q", "No messages yet", "Сообщений пока нет"), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    text = tr("E'lon sahifasidan sotuvchi bilan suhbat boshlashingiz mumkin.", "You can start a conversation from a listing page.", "Вы можете начать диалог со страницы объявления."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ChatPreviewCard(preview: ChatPreview, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = ChatCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AccentGold.copy(alpha = 0.24f), AccentSky.copy(alpha = 0.34f))
                            )
                        ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initialFor(preview.otherUserName),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = AccentGold
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = preview.otherUserName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = preview.lastMessage.ifBlank { tr("Suhbat boshlandi", "Conversation started", "Диалог начат") },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = preview.timestamp.formatChatTime(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(BrandLight)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tr("Ochish", "Open", "Открыть"),
                        style = MaterialTheme.typography.labelSmall,
                        color = Brand,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ChatDetailScreen(
    chatId: String,
    otherUserId: String,
    otherUserName: String,
    viewModel: AppViewModel,
    navController: NavController
) {
    val currentUserId by viewModel.userId.collectAsStateWithLifecycle()
    val currentUserName by viewModel.userName.collectAsStateWithLifecycle()
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    val database = remember { FirebaseDatabase.getInstance().reference }

    DisposableEffect(chatId, currentUserId, currentUserName, otherUserId, otherUserName) {
        currentUserId?.let { uid ->
            database.child("chats").child(chatId).child("participants").child(uid).child("name")
                .setValue(currentUserName ?: uid)
            database.child("chats").child(chatId).child("participants").child(otherUserId).child("name")
                .setValue(otherUserName)
        }

        val messagesRef = database.child("chats").child(chatId).child("messages")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages = snapshot.children.mapNotNull { snap ->
                    val id = snap.key ?: return@mapNotNull null
                    ChatMessage(
                        id = id,
                        senderId = snap.child("senderId").getValue(String::class.java).orEmpty(),
                        senderName = snap.child("senderName").getValue(String::class.java).orEmpty(),
                        text = snap.child("text").getValue(String::class.java).orEmpty(),
                        timestamp = snap.child("timestamp").getValue(Long::class.java) ?: 0L
                    )
                }.sortedBy { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }

        messagesRef.addValueEventListener(listener)
        onDispose { messagesRef.removeEventListener(listener) }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    fun sendMessage() {
        val text = inputText.trim()
        val uid = currentUserId ?: return
        if (text.isBlank()) return

        database.child("chats").child(chatId).child("messages").push().setValue(
            mapOf(
                "senderId" to uid,
                "senderName" to (currentUserName ?: uid),
                "text" to text,
                "timestamp" to ServerValue.TIMESTAMP
            )
        )

        inputText = ""
        keyboard?.hide()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(AccentGold.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initialFor(otherUserName),
                                fontWeight = FontWeight.Black,
                                color = AccentGold
                            )
                        }
                        Column {
                            Text(otherUserName, fontWeight = FontWeight.Bold)
                            Text(
                                text = tr("Pavo chat", "Pavo chat", "Чат Pavo"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(tr("Xabar yozing...", "Write a message...", "Напишите сообщение...")) },
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Brand,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendMessage() }),
                        maxLines = 4
                    )

                    IconButton(
                        onClick = { sendMessage() },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brand)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = tr("Yuborish", "Send", "Отправить"),
                            tint = Color.White
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(BrandLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = Brand,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = tr("$otherUserName bilan suhbat boshlang", "Start a chat with $otherUserName", "Начните чат с $otherUserName"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { message ->
                val isMine = message.senderId == currentUserId
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                            bottomStart = if (isMine) 24.dp else 8.dp,
                            bottomEnd = if (isMine) 8.dp else 24.dp
                        ),
                        color = if (isMine) Brand else MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
                        ) {
                            if (!isMine) {
                                Text(
                                    text = message.senderName.ifBlank { otherUserName },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Brand,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isMine) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.timestamp.formatChatTime(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isMine) Color.White.copy(alpha = 0.72f)
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun initialFor(name: String): String {
    val first = name.firstOrNull()?.uppercaseChar() ?: 'U'
    return first.toString()
}

private fun Long.formatChatTime(): String {
    if (this <= 0L) return ""
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(this))
}
