package ua.intelligence.domain.signal;

import com.fasterxml.jackson.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "envelope",
    "account"
})

public class SignalMessage {

    @JsonProperty("envelope")
    private Envelope envelope;
    @JsonProperty("account")
    private String account;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("envelope")
    public Envelope getEnvelope() {
        return envelope;
    }

    @JsonProperty("envelope")
    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    @JsonProperty("account")
    public String getAccount() {
        return account;
    }

    @JsonProperty("account")
    public void setAccount(String account) {
        this.account = account;
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
