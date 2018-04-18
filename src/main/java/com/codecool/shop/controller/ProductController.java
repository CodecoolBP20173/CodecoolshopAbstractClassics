package com.codecool.shop.controller;

import com.codecool.shop.Cart.CartItems;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.model.Product;
import com.codecool.shop.dao.implementation.SupplierDaoMem;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/","/*"})
public class ProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        SupplierDao productSupplierStore = SupplierDaoMem.getInstance();

        int itemsInCart = 0;

        for (Product cartItem: CartItems.cartItemList) {
            itemsInCart++;
        }

//        Map params = new HashMap<>();
//        params.put("category", productCategoryDataStore.find(1));
//        params.put("products", productDataStore.getBy(productCategoryDataStore.find(1)));

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());

        // This context is specific to Web Applications
        WebContext context = new WebContext(req, resp, req.getServletContext());
//        context.setVariables(params);
        context.setVariable("recipient", "World");
        context.setVariable("category", productCategoryDataStore.getAll());
        context.setVariable("supplier", productSupplierStore.getAll());
        context.setVariable("itemsInCart", itemsInCart);


        String queryString = req.getQueryString();
        if (queryString != null) {
            if (req.getQueryString().contains("category")) {
                int categoryId = Integer.valueOf(req.getParameter("category"));
                context.setVariable("products", productDataStore.getBy(productCategoryDataStore.find(categoryId)));
            } else if (req.getQueryString().contains("supplier")) {
                int supplierId = Integer.valueOf(req.getParameter("supplier"));
                context.setVariable("products", productDataStore.getBy(productSupplierStore.find(supplierId)));
            }
        } else {
            context.setVariable("products", productDataStore.getAll());
        }

        engine.process("product/index.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        int productToAddId = Integer.valueOf(req.getParameter("add-button"));

        CartItems.addItem(productDataStore.find(productToAddId));

        String currentURI = req.getParameter("current-uri");

        resp.sendRedirect(currentURI);
    }
}
