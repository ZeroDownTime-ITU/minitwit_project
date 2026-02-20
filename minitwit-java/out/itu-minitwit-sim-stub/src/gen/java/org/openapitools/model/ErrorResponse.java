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



@JsonTypeName("ErrorResponse")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-02-13T14:37:04.162573194Z[Etc/UTC]", comments = "Generator version: 7.19.0")
public class ErrorResponse   {
  private Integer status;
  private String errorMsg;

  public ErrorResponse() {
  }

  /**
   * HTTP error code
   **/
  public ErrorResponse status(Integer status) {
    this.status = status;
    return this;
  }

  
  @ApiModelProperty(example = "403", value = "HTTP error code")
  @JsonProperty("status")
  public Integer getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(Integer status) {
    this.status = status;
  }

  /**
   * Error message
   **/
  public ErrorResponse errorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
    return this;
  }

  
  @ApiModelProperty(example = "You are not authorized to use this resource!", value = "Error message")
  @JsonProperty("error_msg")
  public String getErrorMsg() {
    return errorMsg;
  }

  @JsonProperty("error_msg")
  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorResponse errorResponse = (ErrorResponse) o;
    return Objects.equals(this.status, errorResponse.status) &&
        Objects.equals(this.errorMsg, errorResponse.errorMsg);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, errorMsg);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponse {\n");
    
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    errorMsg: ").append(toIndentedString(errorMsg)).append("\n");
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

