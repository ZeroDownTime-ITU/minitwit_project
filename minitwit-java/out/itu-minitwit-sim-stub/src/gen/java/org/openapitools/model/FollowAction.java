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



@JsonTypeName("FollowAction")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-02-13T14:37:04.162573194Z[Etc/UTC]", comments = "Generator version: 7.19.0")
public class FollowAction   {
  private String follow;
  private String unfollow;

  public FollowAction() {
  }

  /**
   * Username to follow (optional, either this or \&quot;unfollow\&quot;)
   **/
  public FollowAction follow(String follow) {
    this.follow = follow;
    return this;
  }

  
  @ApiModelProperty(example = "Helge", value = "Username to follow (optional, either this or \"unfollow\")")
  @JsonProperty("follow")
  public String getFollow() {
    return follow;
  }

  @JsonProperty("follow")
  public void setFollow(String follow) {
    this.follow = follow;
  }

  /**
   * Username to unfollow (optional, either this or \&quot;follow\&quot;)
   **/
  public FollowAction unfollow(String unfollow) {
    this.unfollow = unfollow;
    return this;
  }

  
  @ApiModelProperty(example = "Helge", value = "Username to unfollow (optional, either this or \"follow\")")
  @JsonProperty("unfollow")
  public String getUnfollow() {
    return unfollow;
  }

  @JsonProperty("unfollow")
  public void setUnfollow(String unfollow) {
    this.unfollow = unfollow;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FollowAction followAction = (FollowAction) o;
    return Objects.equals(this.follow, followAction.follow) &&
        Objects.equals(this.unfollow, followAction.unfollow);
  }

  @Override
  public int hashCode() {
    return Objects.hash(follow, unfollow);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FollowAction {\n");
    
    sb.append("    follow: ").append(toIndentedString(follow)).append("\n");
    sb.append("    unfollow: ").append(toIndentedString(unfollow)).append("\n");
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

