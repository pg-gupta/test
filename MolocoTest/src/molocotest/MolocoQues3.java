package molocotest;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author miteshpatekar
 */
class Site {

    private Timestamp ts;
    private String user_id;
    private String country_id;
    private String site_id;

    public Site(Timestamp ts, String user_id, String country_id, String site_id) {
        this.ts = ts;
        this.user_id = user_id;
        this.site_id = site_id;
        this.country_id = country_id;
    }

    /**
     * Get the value of country_id
     *
     * @return the value of country_id
     */
    public String getCountry_id() {
        return country_id;
    }

    /**
     * Set the value of country_id
     *
     * @param country_id new value of country_id
     */
    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    /**
     * Get the value of site_id
     *
     * @return the value of site_id
     */
    public String getSite_id() {
        return site_id;
    }

    /**
     * Set the value of site_id
     *
     * @param site_id new value of site_id
     */
    public void setSite_id(String site_id) {
        this.site_id = site_id;
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

    /**
     * Get the value of ts
     *
     * @return the value of ts
     */
    public Timestamp getTs() {
        return ts;
    }

    /**
     * Set the value of ts
     *
     * @param ts new value of ts
     */
    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "Site{" + "ts=" + ts + ", user_id=" + user_id + ", site_id=" + site_id + ", country_id=" + country_id + '}';
    }

}

public class MolocoQues3 {

    public static List<Site> readFile() throws IOException {

        Pattern pattern = Pattern.compile(",");

        try (InputStream resource = MolocoQues3.class.getResourceAsStream("site_data.csv")) {

            BufferedReader in
                    = new BufferedReader(new InputStreamReader(resource,
                            StandardCharsets.UTF_8));

            List<Site> sites = in
                    .lines()
                    .skip(1)
                    .map(line -> {
                        String[] x = pattern.split(line);
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = formatter.parse(x[0]);
                        } catch (ParseException ex) {
                            Logger.getLogger(MolocoQues3.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        Timestamp timeStampDate = new Timestamp(date.getTime());
                        return new Site(timeStampDate,
                                x[1],
                                x[2],
                                x[3]
                        );
                    })
                    .collect(Collectors.toList());

            return sites;

        }

    }

    //Consider only the rows with country_id = "BDV" (there are 844 such rows). 
    //For each site_id, we can compute the number of unique user_id's found in these 844 rows. 
    //Which site_id has the largest number of unique users? And what's the number?
    public static String query1(List<Site> sites, String country_id) {
        List<Site> filteredByCountry = sites.stream().filter(s -> s.getCountry_id().equals(country_id)).collect(Collectors.toList());

        Map<String, Integer> siteUniqueUser = filteredByCountry.stream().collect(
                groupingBy(
                        Site::getSite_id,
                        collectingAndThen(
                                mapping(Site::getUser_id, toSet()),
                                Set::size)));

        Map<String, Integer> siteUniqueUserSorted = new LinkedHashMap<>();
        siteUniqueUser.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .reversed()).forEachOrdered(e -> siteUniqueUserSorted.put(e.getKey(), e.getValue()));

        if (siteUniqueUserSorted.size() > 0) {
            Map.Entry<String, Integer> entry = siteUniqueUserSorted.entrySet().iterator().next();
            String key = entry.getKey();
            long value = entry.getValue();

            return key;
        } else {
            return null;
        }
    }

    //Between 2019-02-03 00:00:00 and 2019-02-04 23:59:59, there are four users who visited a certain site more than 10 times. 
    //Find these four users & which sites they (each) visited more than 10 times. 
    //(Simply provides four triples in the form (user_id, site_id, number of visits) in the box below.)
    public static List<List<String>> query2(List<Site> sites, Timestamp t1, Timestamp t2) {

        List<Site> filteredByTime = sites.stream().filter(s -> compareTime(s.getTs(), t1, t2) == true
        ).collect(Collectors.toList());

        Map<String, Map<String, Long>> siteUniqueUser = filteredByTime.stream().collect(
                groupingBy(
                        Site::getUser_id,
                        groupingBy(Site::getSite_id,
                                Collectors.counting())));

        Map<String, Map<String, Long>> filteredUsers = new HashMap<>();

        siteUniqueUser.forEach((k, v) -> {
            v.forEach((s, c) -> {
                if (c >= 10) {
                    filteredUsers.computeIfAbsent(k, a -> new HashMap<String, Long>()).put(s, c);
                }
            });

        });

        List<List<String>> users = new ArrayList();

        for (String u : filteredUsers.keySet()) {

            for (String s : filteredUsers.get(u).keySet()) {
                List<String> details = new ArrayList<>();
                details.add(u);
                details.add(s);
                details.add(filteredUsers.get(u).get(s).toString());
                users.add(details);
            }

        }

        return users;
    }

    private static boolean compareTime(Timestamp t, Timestamp t1, Timestamp t2) {

        long l1 = t1.getTime();
        long l2 = t2.getTime();
        long l = t.getTime();
        if (l > l1 && l < l2) {
            return true;
        }
        return false;
    }

    //For each site, compute the unique number of users whose last visit (found in the original data set) was to that site. 
    //For instance, user "LC3561"'s last visit is to "N0OTG" based on timestamp data. 
    //Based on this measure, what are top three sites? 
    //(hint: site "3POLC" is ranked at 5th with 28 users whose last visit in the data set was to 3POLC; 
    //simply provide three pairs in the form (site_id, number of users).)
    public static List<List<String>> query3(List<Site> sites) {

        Map<String, TreeMap<Timestamp, String>> usersWithFirstAndLast = query4(sites);

        HashMap<String, Integer> map = new HashMap<>();

        usersWithFirstAndLast.forEach((u, m) -> {
            int n = map.getOrDefault(m.lastEntry().getValue(), 0);
            map.put(m.lastEntry().getValue(), ++n);
        });

        HashMap<String, Integer> mapSorted = new HashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .reversed()).forEachOrdered(e -> mapSorted.put(e.getKey(), e.getValue()));

        List<List<String>> res = new ArrayList();

        int i = 3;

        for (String s : mapSorted.keySet()) {
            if (i == 0) {
                break;
            }
            res.add(Arrays.asList(s, mapSorted.get(s).toString()));
            i--;
        }

        return res;
    }

    //For each user, determine the first site he/she visited and the last site he/she visited based on the timestamp data. 
    //Compute the number of users whose first/last visits are to the same website. What is the number?
    public static Map<String, TreeMap<Timestamp, String>> query4(List<Site> sites) {

        Map<String, Map<Timestamp, String>> users = sites.stream().collect(
                groupingBy(
                        Site::getUser_id,
                        Collectors.toMap(Site::getTs,
                                Site::getSite_id
                        )));

        Map<String, TreeMap<Timestamp, String>> filteredUsers = new HashMap<>();

        users.forEach((k, v) -> {

            HashMap<Timestamp, String> map = new HashMap();

            v.entrySet().stream()
                    .sorted(Map.Entry.<Timestamp, String>comparingByValue()
                            .reversed()).forEachOrdered(e -> map.put(e.getKey(), e.getValue()));

            List<Entry<Timestamp, String>> entryList
                    = new ArrayList<>(map.entrySet());

            filteredUsers.computeIfAbsent(k, a -> new TreeMap<>()).put(entryList.get(0).getKey(), entryList.get(0).getValue());
            filteredUsers.computeIfAbsent(k, a -> new TreeMap<>()).put(entryList.get(entryList.size() - 1).getKey(), entryList.get(entryList.size() - 1).getValue());

        });

        

        return filteredUsers;
    }
}
