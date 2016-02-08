/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WhoShotMyPos;

import net.gpedro.integrations.slack.SlackApi;

/**
 *
 * @author Alex
 */
public class slackApiTokenCreator {
    
    private SlackApi api;
    
    public void createSlackToken(String slackApiToken){   
        api = new SlackApi(slackApiToken);
    }
    
    public SlackApi getApiObject(){
        return api;
    }
}
