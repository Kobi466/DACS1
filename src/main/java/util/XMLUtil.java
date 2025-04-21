package util;

import dto.MessageDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;
import java.io.StringWriter;

public class XMLUtil {
    public static String toXML(MessageDTO message) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(MessageDTO.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(message, writer);
        return writer.toString();
    }

    public static MessageDTO fromXML(String xml, Class<MessageDTO> messageDTOClass) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(MessageDTO.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (MessageDTO) unmarshaller.unmarshal(new StringReader(xml));
    }
}
