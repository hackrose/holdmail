package com.spartasystems.holdmail.mapper;

import com.spartasystems.holdmail.model.MessageList;
import com.spartasystems.holdmail.model.MessageListItem;
import com.spartasystems.holdmail.persistence.MessageEntity;
import com.spartasystems.holdmail.persistence.MessageRecipientEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageListMapperTest {

    private static final String EMAIL_HOMER      = "homer.simpson@snpp.com";
    private static final String EMAIL_MONTY      = "monty.burns@snpp.com";
    private static final int    MESSAGE_ID       = 223344;
    private static final String MESSAGE_SUBJECT  = "subject";
    private static final String EMAIL_CARL       = "carl.carlson@snpp.com";
    private static final long   MESSAGE_RECEIVED = 1465767099897L;

    @Spy
    @InjectMocks
    private MessageListMapper messageListMapperSpy;

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMapEntityListToMessageList() throws Exception{

        List<MessageEntity> entityListMock = mock(List.class);
        Stream<MessageEntity> streamMock = mock(Stream.class);
        MessageList messageListMock = mock(MessageList.class);

        when(entityListMock.stream()).thenReturn(streamMock);
        doReturn(messageListMock).when(messageListMapperSpy).toMessageList(streamMock);

        assertThat(messageListMapperSpy.toMessageList(entityListMock)).isEqualTo(messageListMock);
    }

    public void shouldMapEntityStreamToMessageList() throws Exception {

        MessageEntity entityMock1 = mock(MessageEntity.class);
        MessageEntity entityMock2 = mock(MessageEntity.class);
        MessageList messageListMock1 = mock(MessageList.class);
        MessageList messageListMock2 = mock(MessageList.class);
        doReturn(messageListMock1).when(messageListMapperSpy).toMessageListItem(entityMock1);
        doReturn(messageListMock2).when(messageListMapperSpy).toMessageListItem(entityMock2);

        Stream<MessageEntity> stream = asList(entityMock1, entityMock2).stream();
        assertThat(messageListMapperSpy.toMessageList(stream)).isEqualTo(asList(messageListMock1, messageListMock2));
    }

    @Test
    public void shouldMapToEmptyListWhenStreamIsEmpty() throws Exception {
        MessageList messageList = messageListMapperSpy.toMessageList(Stream.empty());
        assertThat(messageList.getMessages()).isEmpty();
    }

    @Test
    public void shouldMapToMessageListItem() throws Exception{

        MessageRecipientEntity recipientEntity1 = new MessageRecipientEntity(EMAIL_HOMER);
        MessageRecipientEntity recipientEntity2 = new MessageRecipientEntity(EMAIL_MONTY);

        Date receivedDate = new Date();
        receivedDate.setTime(MESSAGE_RECEIVED);

        MessageEntity entity = new MessageEntity();
        entity.setRecipients(newHashSet(recipientEntity1, recipientEntity2));
        entity.setMessageId(MESSAGE_ID);
        entity.setReceivedDate(receivedDate);
        entity.setSenderEmail(EMAIL_CARL);
        entity.setSubject(MESSAGE_SUBJECT);

        MessageListItem messageListItem = messageListMapperSpy.toMessageListItem(entity);
        assertThat(messageListItem.getMessageId()).isEqualTo(MESSAGE_ID);
        assertThat(messageListItem.getReceivedDate()).isEqualTo(MESSAGE_RECEIVED);
        assertThat(messageListItem.getSenderEmail()).isEqualTo(EMAIL_CARL);
        assertThat(messageListItem.getRecipients()).isEqualTo(EMAIL_HOMER + ", " + EMAIL_MONTY);
        assertThat(messageListItem.getSubject()).isEqualTo(MESSAGE_SUBJECT);

    }

}