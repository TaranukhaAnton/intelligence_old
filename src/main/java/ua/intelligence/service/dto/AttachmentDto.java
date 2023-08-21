package ua.intelligence.service.dto;

public class AttachmentDto {

    byte[] content;
    String contentType;
    String fileName;
    int size;

    public AttachmentDto() {}

    public AttachmentDto(byte[] content, String contentType, String fileName, int size) {
        this.content = content;
        this.contentType = contentType;
        this.fileName = fileName;
        this.size = size;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
