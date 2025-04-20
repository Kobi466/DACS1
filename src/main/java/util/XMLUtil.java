package util;

import dto.MessageDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import java.io.StringReader;
import java.io.StringWriter;

public class XMLUtil {
    public static String toXml(MessageDTO dto) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(MessageDTO.class);
        StringWriter writer = new StringWriter();
        ctx.createMarshaller().marshal(dto, writer);
        return writer.toString();
    }

    public static MessageDTO fromXml(String xml, Class<MessageDTO> messageDTOClass) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(MessageDTO.class);
        return (MessageDTO) ctx.createUnmarshaller().unmarshal(new StringReader(xml));
    }
}
