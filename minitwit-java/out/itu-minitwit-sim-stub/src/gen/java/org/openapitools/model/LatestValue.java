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



@JsonTypeName("LatestValue")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-02-13T14:37:04.162573194Z[Etc/UTC]", comments = "Generator version: 7.19.0")
public class LatestValue   {
  private Integer latest;

  public LatestValue() {
  }

  /**
   * The latest global value
   **/
  public LatestValue latest(Integer latest) {
    this.latest = latest;
    return this;
  }

  
  @ApiModelProperty(value = "The latest global value")
  @JsonProperty("latest")
  public Integer getLatest() {
    return latest;
  }

  @JsonProperty("latest")
  public void setLatest(Integer latest) {
    this.latest = latest;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LatestValue latestValue = (LatestValue) o;
    return Objects.equals(this.latest, latestValue.latest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(latest);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LatestValue {\n");
    
    sb.append("    latest: ").append(toIndentedString(latest)).append("\n");
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

