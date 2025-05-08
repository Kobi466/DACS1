package mapper;

import dto.MessageDTO;
import model.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper extends BaseMapper<Message, MessageDTO> {
    // MapStruct tự động tạo các phương thức chuyển đổi giữa Message và MessageDTO
}
