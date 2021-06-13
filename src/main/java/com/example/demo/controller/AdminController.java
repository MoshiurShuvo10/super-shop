package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    private static String uploadDirectory = System.getProperty("user.dir")+"/src/main/resources/static/productImages";

    @Autowired
    CategoryService categoryService ;

    @Autowired
    ProductService productService ;

    @GetMapping("/admin")
	public String adminHome(){
	    return "adminHome";
    }

    //------------ categories routes ----------------
    @GetMapping("/admin/categories")
    public String getCategories(Model model)
    {
        model.addAttribute("categories", categoryService.getAllCategory());
        return "categories";
    }

    @GetMapping("/admin/categories/add")
    public String getAddCategoryForm(Model model)
    {
        model.addAttribute("category", new Category());
        return "addCategoryForm";
    }

    @PostMapping("/admin/categories/add")
    public String postCategory(@ModelAttribute("category") Category category)
    {
        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCategory(@PathVariable int id){
        categoryService.deleteCategoryById(id);
        return "redirect:/admin/categories" ;
    }

    @GetMapping("/admin/categories/update/{id}")
    public String updateCategory(@PathVariable int id, Model model){
        // validate id
        Optional<Category> category = categoryService.getCatagoryById(id) ;
        if(category.isPresent()){
            model.addAttribute("category", category.get());
            return "addCategoryForm";
        }
        else{
            return "404" ;
        }
    }

    //------------ products routes ----------------
    @GetMapping("/admin/products")
    public String getProducts(Model model){
        model.addAttribute("products",productService.getAllProducts());
        return "products";
    }


    @GetMapping("/admin/products/add")
    public String showAddProductForm(Model model){
        model.addAttribute("productDTO",new ProductDTO());
        model.addAttribute("categories",categoryService.getAllCategory());//for select dropdown
        return "showProductAddForm" ;
    }

    @PostMapping("/admin/products/add")
    private String addProduct(@ModelAttribute("productDTO")ProductDTO productDTO,
                              @RequestParam("productImage")MultipartFile multipartFile,
                              @RequestParam("imgName")String imgName) throws IOException {

        Product product = new Product() ;
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setCategory(categoryService.getCatagoryById(productDTO.getCategoryId()).get());
        product.setPrice(productDTO.getPrice());
        product.setWeight(productDTO.getWeight());
        product.setDescription(productDTO.getDescription());
        String imageUUID ;

        if(!multipartFile.isEmpty()){
            imageUUID = multipartFile.getOriginalFilename();
            Path fileNameWithPath = Paths.get(uploadDirectory, imageUUID);
            Files.write(fileNameWithPath,multipartFile.getBytes());
        }
        else{
            imageUUID = imgName ;
        }
        product.setImageName(imageUUID);
        productService.addProduct(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/delete/{id}")
    private String deleteProduct(@PathVariable long id){
        productService.deleteProductById(id);
        return "redirect:/admin/products" ;
    }

    @GetMapping("/admin/products/update/{id}")
    private String updateProduct(@PathVariable long id,Model model){
        Product product = productService.getProductById(id).get();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setPrice(product.getPrice());
        productDTO.setWeight(product.getWeight());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageName(product.getImageName());

        model.addAttribute("productDTO",productDTO);
        model.addAttribute("categories",categoryService.getAllCategory());
        return "showProductAddForm";
    }
}
