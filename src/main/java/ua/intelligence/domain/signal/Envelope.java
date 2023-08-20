
package ua.intelligence.domain.signal;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "source",
    "sourceNumber",
    "sourceUuid",
    "sourceName",
    "sourceDevice",
    "timestamp",
    "dataMessage"
})
@Generated("jsonschema2pojo")
public class Envelope {

    @JsonProperty("source")
    private String source;
    @JsonProperty("sourceNumber")
    private String sourceNumber;
    @JsonProperty("sourceUuid")
    private String sourceUuid;
    @JsonProperty("sourceName")
    private String sourceName;
    @JsonProperty("sourceDevice")
    private Integer sourceDevice;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("dataMessage")
    private DataMessage dataMessage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("sourceNumber")
    public String getSourceNumber() {
        return sourceNumber;
    }

    @JsonProperty("sourceNumber")
    public void setSourceNumber(String sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    @JsonProperty("sourceUuid")
    public String getSourceUuid() {
        return sourceUuid;
    }

    @JsonProperty("sourceUuid")
    public void setSourceUuid(String sourceUuid) {
        this.sourceUuid = sourceUuid;
    }

    @JsonProperty("sourceName")
    public String getSourceName() {
        return sourceName;
    }

    @JsonProperty("sourceName")
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @JsonProperty("sourceDevice")
    public Integer getSourceDevice() {
        return sourceDevice;
    }

    @JsonProperty("sourceDevice")
    public void setSourceDevice(Integer sourceDevice) {
        this.sourceDevice = sourceDevice;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("dataMessage")
    public DataMessage getDataMessage() {
        return dataMessage;
    }

    @JsonProperty("dataMessage")
    public void setDataMessage(DataMessage dataMessage) {
        this.dataMessage = dataMessage;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
