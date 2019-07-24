/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package molocotest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

/**
 *
 * @author pooja gupta
 */
class Product {

    private String user_id;

    private String product_id;

    private int quantity;

    /**
     * Get the value of product_id
     *
     * @return the value of product_id
     */
    public String getProduct_id() {
        return product_id;
    }

    /**
     * Set the value of product_id
     *
     * @param product_id new value of product_id
     */
    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    /**
     * Get the value of quantity
     *
     * @return the value of quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Set the value of quantity
     *
     * @param quantity new value of quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Get the value of user_id
     *
     * @return the value of user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * Set the value of user_id
     *
     * @param user_id new value of user_id
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "{" + "user_id=" + user_id + ", product_id=" + product_id + ", quantity=" + quantity + '}';
    }

}

public class MolocoQues2 {

    public static List<Product> readFileGson() throws IOException {
        try (InputStream resource = MolocoQues2.class.getResourceAsStream("sample2.tsv")) {
            List<String> productsJson
                    = new BufferedReader(new InputStreamReader(resource,
                            StandardCharsets.UTF_8)).lines().collect(Collectors.toList());

            Gson gson = new Gson();

            List<Product> products = new ArrayList<>();

            productsJson.stream().map((p) -> gson.fromJson(p, Product.class)).forEachOrdered((prod) -> {
                products.add(prod);
            });

            return products;
        }
    }

    /**
     *
     * @param products
     * @return list of products which are ranked based on total unique users
     */
    public static List<String> productsRankedByUniqueUser(List<Product> products) {
        List<String> topProducts = new ArrayList();
        // Product mapping with unique user counts
        Map<String, Integer> productToUniqueUser = products.stream().collect(
                groupingBy(
                        Product::getProduct_id,
                        collectingAndThen(
                                mapping(Product::getUser_id, toSet()),
                                Set::size)));

        Map<String, Integer> productToUniqueUserSorted = new LinkedHashMap<>();
        productToUniqueUser.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .reversed()).forEachOrdered(e -> productToUniqueUserSorted.put(e.getKey(), e.getValue()));

        Map.Entry<String, Integer> entry = productToUniqueUserSorted.entrySet().iterator().next();
        String key = entry.getKey();
        int value = entry.getValue();

        for (String s : productToUniqueUserSorted.keySet()) {
            if (productToUniqueUserSorted.get(s) == value) {
                topProducts.add(s);
            } else {
                break;
            }
        }

        return topProducts;

    }

    /**
     *
     * @param products
     * @return list of products which are ranked based on total quantity sold
     */
    public static List<String> productsRankedByQuantity(List<Product> products) {

        List<String> topProducts = new ArrayList();
        // Product mapping with unique user counts
        // product mapping with quantity counts
        Map<String, Long> productToQuantity
                = products.stream().collect(
                        Collectors.groupingBy(
                                Product::getProduct_id, Collectors.summingLong(Product::getQuantity)
                        )
                );

        Map<String, Long> productToQuantitySorted = new LinkedHashMap<>();
        productToQuantity.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue()
                        .reversed()).forEachOrdered(e -> productToQuantitySorted.put(e.getKey(), e.getValue()));

        Map.Entry<String, Long> entry = productToQuantitySorted.entrySet().iterator().next();
        String key = entry.getKey();
        long value = entry.getValue();

        for (String s : productToQuantitySorted.keySet()) {
            if (productToQuantitySorted.get(s) == value) {
                topProducts.add(s);
            } else {
                break;
            }
        }

        return topProducts;

    }
}
