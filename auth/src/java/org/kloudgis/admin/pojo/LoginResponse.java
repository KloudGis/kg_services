/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.admin.pojo;

/**
 *
 * @author jeanfelixg
 */
public class LoginResponse {

    public String auth_token;
    public SignupUser user;

    public LoginResponse(){}

    public LoginResponse(String hashed_token, SignupUser user) {
        this.auth_token = hashed_token;
        this.user = user;
    }
}
