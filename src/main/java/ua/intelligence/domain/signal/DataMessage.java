
package ua.intelligence.domain.signal;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "timestamp",
    "message",
    "expiresInSeconds",
    "viewOnce"
})
@Generated("jsonschema2pojo")
public class DataMessage {

    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("message")
    private String message;
    @JsonProperty("expiresInSeconds")
    private Integer expiresInSeconds;
    @JsonProperty("viewOnce")
    private Boolean viewOnce;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("expiresInSeconds")
    public Integer getExpiresInSeconds() {
        return expiresInSeconds;
    }

    @JsonProperty("expiresInSeconds")
    public void setExpiresInSeconds(Integer expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    @JsonProperty("viewOnce")
    public Boolean getViewOnce() {
        return viewOnce;
    }

    @JsonProperty("viewOnce")
    public void setViewOnce(Boolean viewOnce) {
        this.viewOnce = viewOnce;
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
