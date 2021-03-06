/**
    EurekaJ Profiler - http://eurekaj.haagen.name
    
    Copyright (C) 2010-2011 Joachim Haagen Skeie

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.eurekaj.manager.json;

import org.apache.log4j.Logger;
import org.eurekaj.api.datatypes.*;
import org.eurekaj.api.datatypes.basic.BasicAlert;
import org.eurekaj.api.datatypes.basic.BasicUser;
import org.eurekaj.api.enumtypes.AlertType;
import org.eurekaj.manager.datatypes.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jhs
 * Date: 12/21/10
 * Time: 8:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParseJsonObjects {
	private static Logger log = Logger.getLogger(ParseJsonObjects.class.getName());
	
    public static Alert parseAlertJson(JSONObject jsonAlert, String id, String accountName) throws JSONException {
        BasicAlert parsedAlert = null;

        /*
         * {"alertWarningValue":15,"alertDelay":5,"alertType":null,"alertErrorValue":12,"alertActivated":true,"alertName":"Test"}
         */
        if (jsonAlert.has("alert")) {
            parsedAlert = new BasicAlert();
            JSONObject alert = jsonAlert.getJSONObject("alert");
            if (id == null || id.length() == 0) {
                parsedAlert.setAlertName(parseStringFromJson(alert, "id"));
            } else {
                parsedAlert.setAlertName(id);
            }

            parsedAlert.setWarningValue(parseDoubleFromJson(alert, "alertWarningValue"));
            parsedAlert.setErrorValue(parseDoubleFromJson(alert, "alertErrorValue"));
            parsedAlert.setGuiPath(parseStringFromJson(alert, "alertSource"));
            
            Integer alertDelay = parseIntegerFromJson(alert, "alertDelay");
            if (alertDelay == null) { alertDelay = 0; }
            parsedAlert.setAlertDelay(alertDelay.longValue());
            
            parsedAlert.setActivated(parseBooleanFromJson(alert, "alertActivated"));
            parsedAlert.setSelectedAlertType(AlertType.fromValue(parseStringFromJson(alert, "alertType")));
            parsedAlert.setSelectedEmailSenderList(getStringArrayFromJson(alert, "alertNotifications"));
            parsedAlert.setSelectedAlertPluginList(getStringArrayFromJson(alert, "alertPlugins"));
            parsedAlert.setAccountName(accountName);
        }

        log.info("alertName: " + parsedAlert.getAlertName() + " account: " + parsedAlert.getAccountName());
        return parsedAlert;
    }

    public static Account parseAccount(JSONObject jsonObject) throws JSONException {
        ManagerAccount account = new ManagerAccount();

        if (jsonObject.has("account_name") && jsonObject.has("account_type")) {
            account.id(jsonObject.getString("account_name"));
            account.setAccountType(jsonObject.getString("account_type"));
        }

        return account;
    }

    public static BasicUser parseUser(JSONObject userJsonObject) throws JSONException {
        BasicUser user = new BasicUser();

        if (userJsonObject.has("accountName")) {
            user.setAccountName(userJsonObject.getString("accountName"));
        }

        if (userJsonObject.has("userRole")) {
            user.setUserRole(userJsonObject.getString("userRole"));
        }
        
        if (userJsonObject.has("firstName")) {
            user.setFirstname(userJsonObject.getString("firstName"));
        }
        
        if (userJsonObject.has("lastName")) {
            user.setLastname(userJsonObject.getString("lastName"));
        }
        
        if (userJsonObject.has("country")) {
            user.setCountry(userJsonObject.getString("country"));
        }
        
        if (userJsonObject.has("company")) {
            user.setCompany(userJsonObject.getString("company"));
        }
        
        if (userJsonObject.has("usage")) {
            user.setUsage(userJsonObject.getString("usage"));
        }
        
        if (userJsonObject.has("uuidToken")) {
            user.setId(userJsonObject.getString("uuidToken"));
        }
        
        if (userJsonObject.has("authLevel")) {
        	String authLevel = userJsonObject.getString("authLevel");
        	if (authLevel != null && authLevel.equals("new")) {
        		user.setUserRole("new");
        	}
        }

        return user;
    }

    public static GroupedStatistics parseInstrumentationGroup(JSONObject jsonGroup, String id, String accountName) throws JSONException {
        ManagerGroupedStatistics groupedStatistics = null;

        //{"chart_group_model":{"id":"New Group","chart_group_path":null}}

        if (jsonGroup.has("chartGroup")) {
            JSONObject chartGroupObject = jsonGroup.getJSONObject("chartGroup");
            groupedStatistics = new ManagerGroupedStatistics();
            if (id != null) {
                groupedStatistics.setName(id);
            } else {
                groupedStatistics.setName(parseStringFromJson(chartGroupObject, "id"));
            }

            if (accountName != null) {
                groupedStatistics.setAccountName(accountName);
            }

            groupedStatistics.setGroupedPathList(getStringArrayFromJson(chartGroupObject, "chartGroups"));
        }

        return groupedStatistics;
    }

    public static ManagerLiveStatistics parseLiveStatistics(JSONObject jsonLiveStatistics, String accountName) {
        ManagerLiveStatistics liveStatistics = null;

        if (jsonLiveStatistics.has("guiPath")) {
            liveStatistics = new ManagerLiveStatistics();
            liveStatistics.setAccountName(accountName);
            liveStatistics.setGuiPath(parseStringFromJson(jsonLiveStatistics, "guiPath"));
            liveStatistics.setTimeperiod(parseLongFromJson(jsonLiveStatistics, "timeperiod"));
            liveStatistics.setValue(parseDoubleFromJson(jsonLiveStatistics, "value"));
            liveStatistics.setValueType(parseStringFromJson(jsonLiveStatistics, "valueType"));
            liveStatistics.setUnitType(parseStringFromJson(jsonLiveStatistics, "unitType"));
        }

        return liveStatistics;
    }

    public static EmailRecipientGroup parseEmailGroup(JSONObject jsonEmailGroup, String id, String accountName) throws JSONException {
        ManagerEmailRecipientGroup emailRecipientGroup = new ManagerEmailRecipientGroup();

        if (jsonEmailGroup.has("email_group_model")) {
            JSONObject eg = jsonEmailGroup.getJSONObject("email_group_model");
            if (id != null) {
                emailRecipientGroup.setEmailRecipientGroupName(id);
            } else {
                emailRecipientGroup.setEmailRecipientGroupName(parseStringFromJson(eg, "id"));
            }
            emailRecipientGroup.setAccountName(accountName);
            emailRecipientGroup.setPort(parseIntegerFromJson(eg, "smtp_port"));
            emailRecipientGroup.setUseSSL(parseBooleanFromJson(eg, "smtp_use_ssl"));
            emailRecipientGroup.setSmtpServerhost(parseStringFromJson(eg, "smtp_host"));
            emailRecipientGroup.setSmtpUsername(parseStringFromJson(eg, "smtp_username"));
            emailRecipientGroup.setSmtpPassword(parseStringFromJson(eg, "smtp_password"));
            emailRecipientGroup.setEmailRecipientList(getStringArrayFromJson(eg, "email_addresses"));
        }

        return emailRecipientGroup;
    }

    public static String parseStringFromJson(JSONObject json, String key) {
        String stringValue = null;

        try {
            stringValue = json.getString(key);
        } catch (JSONException e) {
            stringValue = null;
        }

        return stringValue;
    }

    public static List<String> getStringArrayFromJson(JSONObject json, String key) {
    	List<String> groupList = new ArrayList<String>();
    	JSONArray groupJsonArray = null;
        try {
        	log.info("Parsing JSON Array into String Array");
            groupJsonArray = json.getJSONArray(key);
            for (int index = 0; index < groupJsonArray.length(); index++) {
                groupList.add(groupJsonArray.getString(index));
            }
        } catch (JSONException e) {
            log.info("json array exception: " + e.getMessage());
        }
        
        if (groupJsonArray == null) {
        	log.info("Object not JSON Array. Creating new Array");
        	try {
        		groupJsonArray = new JSONArray(json.getString(key));
        		log.info("Array Created: " + groupJsonArray.toString());
        		for (int index = 0; index < groupJsonArray.length(); index++) {
                    groupList.add(groupJsonArray.getString(index));
                }
            } catch (JSONException e) {
                log.info("json array exception: " + e.getMessage());
            }
        }

        return groupList;
    }

    public static Integer parseIntegerFromJson(JSONObject json, String key) {
        Integer intValue = null;
        try {
        	String stringVal = parseStringFromJson(json, key);
        	if (stringVal != null)
        		intValue = Integer.parseInt(stringVal);
        } catch (NumberFormatException nfe) {
            intValue = null;
        }

        return intValue;
    }

    public static Long parseLongFromJson(JSONObject json, String key) {
        Long longValue = null;
        try {
        	String stringVal = parseStringFromJson(json, key);
        	if (stringVal != null)
        		longValue = Long.parseLong(stringVal);
        } catch (NumberFormatException nfe) {
            longValue = null;
        }

        return longValue;
    }

    public static Double parseDoubleFromJson(JSONObject json, String key) {
        Double doubleValue = null;
        try {
        	String stringVal = parseStringFromJson(json, key);
        	if (stringVal != null)
        		doubleValue = Double.parseDouble(parseStringFromJson(json, key));
        } catch (NumberFormatException nfe) {
            doubleValue = null;
        }

        return doubleValue;
    }

    public static Boolean parseBooleanFromJson(JSONObject json, String key) {
        Boolean boolValue = null;

        try {
            boolValue = json.getBoolean(key);
        } catch (JSONException e) {
            boolValue = false;
        }

        return boolValue;
    }
}
