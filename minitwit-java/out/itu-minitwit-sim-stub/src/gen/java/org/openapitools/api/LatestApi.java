package org.openapitools.api;

import org.openapitools.model.ErrorResponse;
import org.openapitools.model.LatestValue;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.swagger.annotations.*;

import java.io.InputStream;
import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/latest")
@Api(description = "the latest API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-02-13T14:37:04.162573194Z[Etc/UTC]", comments = "Generator version: 7.19.0")
public class LatestApi {

    @GET
    @Produces({ "application/json" })
    @ApiOperation(value = "", notes = "Returns the latest ID saved", response = LatestValue.class, tags={ "Minitwit" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = LatestValue.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)
    })
    public Response getLatestValue() {
        return Response.ok().entity("magic!").build();
    }
}
