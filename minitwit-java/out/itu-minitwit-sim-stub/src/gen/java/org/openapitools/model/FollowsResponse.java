package org.openapitools.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;



@JsonTypeName("FollowsResponse")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-02-13T14:37:04.162573194Z[Etc/UTC]", comments = "Generator version: 7.19.0")
public class FollowsResponse   {
  private @Valid List<String> follows = new ArrayList<>();

  public FollowsResponse() {
  }

  /**
   * List of usernames the user is following
   **/
  public FollowsResponse follows(List<String> follows) {
    this.follows = follows;
    return this;
  }

  
  @ApiModelProperty(example = "[\"Helge\",\"John\"]", value = "List of usernames the user is following")
  @JsonProperty("follows")
  public List<String> getFollows() {
    return follows;
  }

  @JsonProperty("follows")
  public void setFollows(List<String> follows) {
    this.follows = follows;
  }

  public FollowsResponse addFollowsItem(String followsItem) {
    if (this.follows == null) {
      this.follows = new ArrayList<>();
    }

    this.follows.add(followsItem);
    return this;
  }

  public FollowsResponse removeFollowsItem(String followsItem) {
    if (followsItem != null && this.follows != null) {
      this.follows.remove(followsItem);
    }

    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FollowsResponse followsResponse = (FollowsResponse) o;
    return Objects.equals(this.follows, followsResponse.follows);
  }

  @Override
  public int hashCode() {
    return Objects.hash(follows);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FollowsResponse {\n");
    
    sb.append("    follows: ").append(toIndentedString(follows)).append("\n");
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

