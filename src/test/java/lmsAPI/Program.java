package lmsAPI;

import java.util.HashMap;
import java.util.Map;

public class Program {

    private Integer programId;
    private String programName;
    private String programDescription;
    private Boolean online;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramDescription() {
        return programDescription;
    }

    public void setProgramDescription(String programDescription) {
        this.programDescription = programDescription;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    @Override
	public String toString() {
		return "Program [programId=" + programId + ", programName=" + programName + ", programDescription="
				+ programDescription + ", online=" + online + "]";
	}

}



