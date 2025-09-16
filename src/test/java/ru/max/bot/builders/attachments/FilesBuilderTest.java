package ru.max.bot.builders.attachments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ru.max.bot.Randoms;
import ru.max.botapi.model.AttachmentRequest;
import ru.max.botapi.model.FileAttachmentRequest;
import ru.max.botapi.model.UploadedInfo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FilesBuilderTest {
    @Test
    public void shouldBuildfromUploadedInfo() {
        String token2 = Randoms.text();
        FilesBuilder builder2 = new FilesBuilder(new UploadedInfo().token(token2));
        List<AttachmentRequest> list2 = builder2.build().collect(Collectors.toList());
        assertThat(list2, is(Collections.singletonList(new FileAttachmentRequest(new UploadedInfo().token(token2)))));

        String token3 = Randoms.text();
        String token4 = Randoms.text();
        FilesBuilder builder3 = new FilesBuilder(new UploadedInfo().token(token3), new UploadedInfo().token(token4));
        List<AttachmentRequest> list3 = builder3.build().collect(Collectors.toList());
        assertThat(list3, is(Arrays.asList(
                new FileAttachmentRequest(new UploadedInfo().token(token3)),
                new FileAttachmentRequest(new UploadedInfo().token(token4)))
        ));
    }

    @Test
    public void shouldBuildfromString() {
        String token2 = Randoms.text();
        FilesBuilder builder2 = new FilesBuilder(token2);
        List<AttachmentRequest> list2 = builder2.build().collect(Collectors.toList());
        assertThat(list2, is(Collections.singletonList(new FileAttachmentRequest(new UploadedInfo().token(token2)))));

        String token3 = Randoms.text();
        String token4 = Randoms.text();
        FilesBuilder builder3 = new FilesBuilder(token3, token4);
        List<AttachmentRequest> list3 = builder3.build().collect(Collectors.toList());
        assertThat(list3, is(Arrays.asList(
                new FileAttachmentRequest(new UploadedInfo().token(token3)),
                new FileAttachmentRequest(new UploadedInfo().token(token4)))
        ));
    }
}