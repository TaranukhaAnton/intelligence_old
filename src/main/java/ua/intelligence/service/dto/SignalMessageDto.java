package ua.intelligence.service.dto;

import java.util.List;

/**
 * @author Taranukha Anton
 */

public class SignalMessageDto {

    private String uid;
    private String phone;
    private String nickName;
    private String text;
    private Long time;
    private List<AttachmentDto> attachments;

    private String groupId;

    public SignalMessageDto() {}

    public SignalMessageDto(
        String uid,
        String phone,
        String nickName,
        String text,
        Long time,
        List<AttachmentDto> attachments,
        String groupId
    ) {
        this.uid = uid;
        this.phone = phone;
        this.nickName = nickName;
        this.text = text;
        this.time = time;
        this.attachments = attachments;
        this.groupId = groupId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public List<AttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDto> attachments) {
        this.attachments = attachments;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
