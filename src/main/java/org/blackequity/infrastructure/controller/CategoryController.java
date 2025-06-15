package org.blackequity.infrastructure.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.blackequity.application.service.CategoryService;
import org.blackequity.domain.dto.CategoryDto;
import org.blackequity.shared.dto.CreateCategoryDTO;

import java.util.List;
import java.util.Map;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryController {

    @Inject
    CategoryService categoryService;

    @POST
    @Path("/create")
    public Response createCategory(CreateCategoryDTO dto) {
        try {
            categoryService.createCategory(dto);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/all")
    public Response getAllCategories() {
        try {
            List<CategoryDto> categories = categoryService.getAllCategories();
            return Response.ok(categories).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
