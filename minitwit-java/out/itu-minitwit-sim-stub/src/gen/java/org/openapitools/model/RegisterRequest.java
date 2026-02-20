package org.openapitools.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.validation.Valid;

import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;



@JsonTypeName("RegisterRequest")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-02-13T14:37:04.162573194Z[Etc/UTC]", comments = "Generator version: 7.19.0")
public class RegisterRequest   {
  private String username;
  private String email;
  private String pwd;

  public RegisterRequest() {
  }

  @JsonCreator
  public RegisterRequest(
    @JsonProperty(required = true, value = "username") String username,
    @JsonProperty(required = true, value = "email") String email,
    @JsonProperty(required = true, value = "pwd") String pwd
  ) {
    this.username = username;
    this.email = email;
    this.pwd = pwd;
  }

  /**
   * Username
   **/
  public RegisterRequest username(String username) {
    this.username = username;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Username")
  @JsonProperty(required = true, value = "username")
  @NotNull public String getUsername() {
    return username;
  }

  @JsonProperty(required = true, value = "username")
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Email address
   **/
  public RegisterRequest email(String email) {
    this.email = email;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Email address")
  @JsonProperty(required = true, value = "email")
  @NotNull public String getEmail() {
    return email;
  }

  @JsonProperty(required = true, value = "email")
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Password
   **/
  public RegisterRequest pwd(String pwd) {
    this.pwd = pwd;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Password")
  @JsonProperty(required = true, value = "pwd")
  @NotNull public String getPwd() {
    return pwd;
  }

  @JsonProperty(required = true, value = "pwd")
  public void setPwd(String pwd) {
    this.pwd = pwd;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegisterRequest registerRequest = (RegisterRequest) o;
    return Objects.equals(this.username, registerRequest.username) &&
        Objects.equals(this.email, registerRequest.email) &&
        Objects.equals(this.pwd, registerRequest.pwd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, email, pwd);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegisterRequest {\n");
    
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    pwd: ").append(toIndentedString(pwd)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


}

