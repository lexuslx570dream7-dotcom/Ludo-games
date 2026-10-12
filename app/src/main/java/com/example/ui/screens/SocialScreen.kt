package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GameRepository
import com.example.model.ChatMessage
import com.example.model.Friend
import com.example.model.FriendRequest
import com.example.model.MessageType
import com.example.ui.components.AndroidStatusBar
import com.example.ui.theme.ActiveGreenGlow
import com.example.ui.theme.CoinGold

@Composable
fun SocialScreen(
  repository: GameRepository,
  onNavigateTab: (Int) -> Unit,
  onChallengeFriend: (Friend) -> Unit,
  modifier: Modifier = Modifier
) {
  val friends by repository.friends.collectAsState()
  val friendRequests by repository.friendRequests.collectAsState()
  val chatMessagesMap by repository.chatMessages.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Friends, 1 = Requests, 2 = Add Friend
  var activeChatFriend by remember { mutableStateOf<Friend?>(null) }
  var addFriendInput by remember { mutableStateOf("") }
  var addFriendStatusMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF072124),
            Color(0xFF093139),
            Color(0xFF061B21),
            Color(0xFF021013)
          )
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 76.dp)
    ) {
      AndroidStatusBar()

      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Social & Friends",
          color = Color.White,
          fontSize = 20.sp,
          fontWeight = FontWeight.Black
        )
        Text(
          text = "${friends.count { it.isOnline }} Online",
          color = ActiveGreenGlow,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Tabs
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFF052B2D),
        contentColor = Color.White,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = ActiveGreenGlow
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Friends (${friends.size})", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Requests (${friendRequests.size})", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("+ Add Friend", fontWeight = FontWeight.Bold) }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      when (selectedTab) {
        0 -> {
          // Friends List
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(friends) { friend ->
              FriendItemRow(
                friend = friend,
                onChat = { activeChatFriend = friend },
                onChallenge = { onChallengeFriend(friend) },
                onRemove = { repository.removeFriend(friend.id) }
              )
            }
          }
        }
        1 -> {
          // Friend Requests
          if (friendRequests.isEmpty()) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Text("No pending friend requests.", color = Color(0xFF80CBC4), fontSize = 14.sp)
            }
          } else {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(friendRequests) { req ->
                FriendRequestRow(
                  request = req,
                  onAccept = { repository.acceptFriendRequest(req.id) },
                  onReject = { repository.rejectFriendRequest(req.id) }
                )
              }
            }
          }
        }
        2 -> {
          // Add Friend Tab
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              "Search & Add Friends",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              "Enter player's User ID or username to send an instant friend request.",
              color = Color(0xFFB0BEC5),
              fontSize = 12.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
              value = addFriendInput,
              onValueChange = { addFriendInput = it },
              placeholder = { Text("e.g. Ali or LK-99214", color = Color.Gray) },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ActiveGreenGlow,
                unfocusedBorderColor = Color(0xFF00897B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              modifier = Modifier.fillMaxWidth().testTag("add_friend_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = {
                if (addFriendInput.isNotBlank()) {
                  val sent = repository.sendFriendRequest(addFriendInput)
                  if (sent) {
                    addFriendStatusMessage = "Friend request sent to $addFriendInput!"
                    addFriendInput = ""
                  }
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.fillMaxWidth().height(46.dp).testTag("send_friend_request_button")
            ) {
              Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF003810))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Send Friend Request", color = Color(0xFF003810), fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            addFriendStatusMessage?.let { msg ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF004D40))
                  .padding(10.dp)
              ) {
                Text(msg, color = ActiveGreenGlow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
            }
          }
        }
      }
    }

    // Direct 1-on-1 Private Chat Modal
    activeChatFriend?.let { friend ->
      val messages = chatMessagesMap[friend.id] ?: emptyList()
      PrivateChatDialog(
        friend = friend,
        messages = messages,
        onDismiss = { activeChatFriend = null },
        onSendMessage = { text ->
          repository.sendChatMessage(friend.id, text, MessageType.TEXT)
        },
        onSendVoice = { duration ->
          repository.sendChatMessage(friend.id, "Voice Message ($duration s)", MessageType.VOICE, duration)
        },
        onSendImage = {
          repository.sendChatMessage(friend.id, "Shared an image 📷", MessageType.IMAGE)
        }
      )
    }

    // Permanent Bottom Navigation Bar
    BottomNavigationBar(
      currentTabIndex = 1,
      onTabSelected = onNavigateTab,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
    )
  }
}

@Composable
fun FriendItemRow(
  friend: Friend,
  onChat: () -> Unit,
  onChallenge: () -> Unit,
  onRemove: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFF062C2E))
      .border(1.dp, Color(0xFF0D5356), RoundedCornerShape(16.dp))
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xFF00695C))
            .border(2.dp, if (friend.isOnline) ActiveGreenGlow else Color.Gray, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(friend.username.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        // Online dot
        Box(
          modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(if (friend.isOnline) Color(0xFF00E676) else Color.Gray)
            .border(1.5.dp, Color(0xFF062C2E), CircleShape)
            .align(Alignment.BottomEnd)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(friend.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(
          text = if (friend.inGame) "Playing Match" else if (friend.isOnline) "Online • Lv.${friend.level}" else "Offline",
          color = if (friend.isOnline) ActiveGreenGlow else Color(0xFF80CBC4),
          fontSize = 11.sp
        )
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      IconButton(
        onClick = onChat,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(Color(0xFF00897B))
          .testTag("chat_friend_${friend.id}")
      ) {
        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = Color.White, modifier = Modifier.size(18.dp))
      }

      Button(
        onClick = onChallenge,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.height(36.dp).testTag("challenge_friend_${friend.id}")
      ) {
        Text("Play", color = Color(0xFF3E2723), fontWeight = FontWeight.Black, fontSize = 12.sp)
      }

      IconButton(
        onClick = onRemove,
        modifier = Modifier.size(36.dp).testTag("remove_friend_${friend.id}")
      ) {
        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFEF5350), modifier = Modifier.size(18.dp))
      }
    }
  }
}

@Composable
fun FriendRequestRow(
  request: FriendRequest,
  onAccept: () -> Unit,
  onReject: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0xFF062C2E))
      .border(1.dp, Color(0xFF0D5356), RoundedCornerShape(14.dp))
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      Text(request.senderName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Text("Level ${request.senderLevel} • ${request.timestamp}", color = Color(0xFF80CBC4), fontSize = 11.sp)
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(
        onClick = onAccept,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(34.dp).testTag("accept_req_${request.id}")
      ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF003810), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Accept", color = Color(0xFF003810), fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }

      IconButton(
        onClick = onReject,
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(Color(0xFFD32F2F).copy(alpha = 0.2f))
          .testTag("reject_req_${request.id}")
      ) {
        Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(18.dp))
      }
    }
  }
}

@Composable
fun PrivateChatDialog(
  friend: Friend,
  messages: List<ChatMessage>,
  onDismiss: () -> Unit,
  onSendMessage: (String) -> Unit,
  onSendVoice: (durationSec: Int) -> Unit,
  onSendImage: () -> Unit
) {
  var chatInput by remember { mutableStateOf("") }
  var isRecordingVoice by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(480.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0xFF072124))
        .border(2.dp, Color(0xFF00897B), RoundedCornerShape(20.dp))
        .padding(14.dp)
        .testTag("private_chat_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Chat Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF00897B)),
              contentAlignment = Alignment.Center
            ) {
              Text(friend.username.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(friend.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(if (friend.isOnline) "Online" else "Offline", color = ActiveGreenGlow, fontSize = 11.sp)
            }
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Messages List
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(messages) { msg ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = if (msg.isMine) Arrangement.End else Arrangement.Start
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (msg.isMine) Color(0xFF00695C) else Color(0xFF133E42))
                  .border(1.dp, if (msg.isMine) Color(0xFF00E676) else Color(0xFF26A69A), RoundedCornerShape(12.dp))
                  .padding(8.dp)
              ) {
                Column {
                  if (msg.type == MessageType.VOICE) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.PlayArrow, contentDescription = "Play Voice", tint = ActiveGreenGlow)
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Voice Note (${msg.voiceDurationSec}s) 🎵", color = Color.White, fontSize = 12.sp)
                    }
                  } else {
                    Text(msg.text, color = Color.White, fontSize = 13.sp)
                  }
                  Text(
                    msg.timestamp,
                    color = Color(0xFFB0BEC5),
                    fontSize = 9.sp,
                    modifier = Modifier.align(Alignment.End)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input bar (Text, Voice message, Image share)
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Image share button
          IconButton(
            onClick = onSendImage,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(Icons.Default.Image, contentDescription = "Share Image", tint = Color(0xFF80CBC4))
          }

          // Voice record button
          IconButton(
            onClick = {
              if (isRecordingVoice) {
                isRecordingVoice = false
                onSendVoice(4)
              } else {
                isRecordingVoice = true
              }
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (isRecordingVoice) Color(0xFFD32F2F) else Color(0xFF00897B))
          ) {
            Icon(Icons.Default.Mic, contentDescription = "Record Voice", tint = Color.White)
          }

          // Text field
          OutlinedTextField(
            value = chatInput,
            onValueChange = { chatInput = it },
            placeholder = { Text(if (isRecordingVoice) "Recording voice..." else "Type message...", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier.weight(1f).height(48.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ActiveGreenGlow,
              unfocusedBorderColor = Color(0xFF00695C),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            )
          )

          // Send button
          IconButton(
            onClick = {
              if (chatInput.isNotBlank()) {
                onSendMessage(chatInput)
                chatInput = ""
              }
            },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(ActiveGreenGlow)
              .testTag("send_chat_message_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFF003810))
          }
        }
      }
    }
  }
}
