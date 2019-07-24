/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package molocotest;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static molocotest.MolocoQues2.productsRankedByQuantity;
import static molocotest.MolocoQues2.productsRankedByUniqueUser;
import static molocotest.MolocoQues2.readFileGson;

/**
 *
 * @author miteshpatekar
 */
public class MolocoMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Question 2 results
        try {
            List<Product> products = readFileGson();
            List<String> topByUsers = productsRankedByUniqueUser(products);
            List<String> topByQuantity = productsRankedByQuantity(products);
            System.out.println("\n****Question2 answers*****\n");
            System.out.println("Most popular product(s) based on the number of purchasers: " + topByUsers);
            System.out.println("Most popular product(s) based on the  quantity of goods sold: " + topByQuantity);

        } catch (IOException ex) {
            Logger.getLogger(MolocoQues2.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("\n****Question3 answers*****");
        try {
            List<Site> sites = MolocoQues3.readFile();
            System.out.println("\n--Query 1 answer--");
            System.out.println("Site hit by maximum unique user in country 'BDV' is: " + MolocoQues3.query1(sites, "BDV"));
            String t1 = "2019-02-03 00:00:00";
            String t2 = "2019-02-04 23:59:59";
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = formatter.parse(t1);
                date2 = formatter.parse(t2);
            } catch (ParseException ex) {
                Logger.getLogger(MolocoQues3.class.getName()).log(Level.SEVERE, null, ex);
            }

            Timestamp time1 = new Timestamp(date1.getTime());
            Timestamp time2 = new Timestamp(date2.getTime());

            System.out.println("\n--Query 2 answer--\n");
            List<List<String>> users = MolocoQues3.query2(sites, time1, time2);
            System.out.println("Users who hit the site more than 10 times are: " + users);

            System.out.println("\n--Query 3 answer--\n");
            List<List<String>> sitesWithMostLastVisits=MolocoQues3.query3(sites);
            for(List<String> d:sitesWithMostLastVisits){
                System.out.println(d);
            }

            System.out.println("\n--Query 4 answer--\n");
            Map<String, TreeMap<Timestamp, String>> filteredUsers = MolocoQues3.query4(sites);

            int count = 0;
            for (String u : filteredUsers.keySet()) {
                String firstSite = filteredUsers.get(u).firstEntry().getValue();
                String lastSite = filteredUsers.get(u).lastEntry().getValue();
                if (firstSite.equals(lastSite)) {
                    count++;
                }
                System.out.println(u + " First Site: " + firstSite + " Last site: " + lastSite);
            }

            System.out.println("\nNumber of users with first and last visit are on the same site are: " + count);
        } catch (IOException ex) {
            Logger.getLogger(MolocoQues2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
