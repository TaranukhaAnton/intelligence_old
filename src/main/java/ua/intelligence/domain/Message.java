package ua.intelligence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private ZonedDateTime date;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "sender_call_sign")
    private String senderCallSign;

    @Column(name = "reciever_call_sign")
    private String receiverCallSign;

    @Column(name = "text")
    private String text;

    @Column(name = "source_uuid")
    private String sourceUuid;

    @ManyToOne
    @JsonIgnoreProperties(value = { "messages" }, allowSetters = true)
    private Report report;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Message id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public Message date(ZonedDateTime date) {
        this.setDate(date);
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getFrequency() {
        return this.frequency;
    }

    public Message frequency(String frequency) {
        this.setFrequency(frequency);
        return this;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getSenderCallSign() {
        return this.senderCallSign;
    }

    public Message senderCallSign(String senderCallSign) {
        this.setSenderCallSign(senderCallSign);
        return this;
    }

    public void setSenderCallSign(String senderCallSign) {
        this.senderCallSign = senderCallSign;
    }

    public String getReceiverCallSign() {
        return this.receiverCallSign;
    }

    public Message receiverCallSign(String receiverCallSign) {
        this.setReceiverCallSign(receiverCallSign);
        return this;
    }

    public void setReceiverCallSign(String recieverCallSign) {
        this.receiverCallSign = recieverCallSign;
    }

    public String getText() {
        return this.text;
    }

    public Message text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSourceUuid() {
        return this.sourceUuid;
    }

    public Message sourceUuid(String sourceUuid) {
        this.setSourceUuid(sourceUuid);
        return this;
    }

    public void setSourceUuid(String sourceUuid) {
        this.sourceUuid = sourceUuid;
    }

    public Report getReport() {
        return this.report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Message report(Report report) {
        this.setReport(report);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return id != null && id.equals(((Message) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", frequency='" + getFrequency() + "'" +
            ", senderCallSign='" + getSenderCallSign() + "'" +
            ", recieverCallSign='" + getReceiverCallSign() + "'" +
            ", text='" + getText() + "'" +
            ", sourceUuid='" + getSourceUuid() + "'" +
            "}";
    }
}
