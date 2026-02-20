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



@JsonTypeName("Message")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-02-13T14:37:04.162573194Z[Etc/UTC]", comments = "Generator version: 7.19.0")
public class Message   {
  private String content;
  private String pubDate;
  private String user;

  public Message() {
  }

  /**
   * Text content of the message
   **/
  public Message content(String content) {
    this.content = content;
    return this;
  }

  
  @ApiModelProperty(example = "Hello, World!", value = "Text content of the message")
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  @JsonProperty("content")
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Publication date/time of the message
   **/
  public Message pubDate(String pubDate) {
    this.pubDate = pubDate;
    return this;
  }

  
  @ApiModelProperty(example = "2019-12-01 12:00:00", value = "Publication date/time of the message")
  @JsonProperty("pub_date")
  public String getPubDate() {
    return pubDate;
  }

  @JsonProperty("pub_date")
  public void setPubDate(String pubDate) {
    this.pubDate = pubDate;
  }

  /**
   * Username of the message author
   **/
  public Message user(String user) {
    this.user = user;
    return this;
  }

  
  @ApiModelProperty(example = "Helge", value = "Username of the message author")
  @JsonProperty("user")
  public String getUser() {
    return user;
  }

  @JsonProperty("user")
  public void setUser(String user) {
    this.user = user;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Message message = (Message) o;
    return Objects.equals(this.content, message.content) &&
        Objects.equals(this.pubDate, message.pubDate) &&
        Objects.equals(this.user, message.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, pubDate, user);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Message {\n");
    
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    pubDate: ").append(toIndentedString(pubDate)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
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

