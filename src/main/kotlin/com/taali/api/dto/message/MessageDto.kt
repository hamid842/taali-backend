import com.taali.api.dto.message.ConversationDTO
import java.time.LocalDateTime

data class MessageDTO(
    val id: Long?,
    val content: String,
    val sender: ConversationDTO.UserDTO,
    val conversationId: Long?,
    val isRead: Boolean,
    val createdAt: LocalDateTime,
    val readAt: LocalDateTime?
)