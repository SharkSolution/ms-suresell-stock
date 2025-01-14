package org.blackequity.infrastructure.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import org.blackequity.application.service.ProductService;
import org.blackequity.shared.dto.CreateProductDTO;
import org.blackequity.shared.dto.ProductDTO;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    ProductService productService;

    @POST
    public Response createProduct(CreateProductDTO dto) {
        productService.createProduct(dto);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }
}
