/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WhoShotMyPos;

/**
 *
 * @author Alex
 */
public class ProgramParameterDefaults {
    private String apiLink;
    private String slackToken;

    public void setApiLink(String apiLink) {
        this.apiLink = apiLink;
    }

    public void setSlackToken(String slackToken) {
        this.slackToken = slackToken;
    }    
    
    public String getApiLink() {
        return apiLink;
    }

    public String getSlackToken() {
        return slackToken;
    }    
    
}
