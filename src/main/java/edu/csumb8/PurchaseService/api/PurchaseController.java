package edu.csumb8.PurchaseService.api;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class PurchaseController {

    /**
     * @route /purchase
     * @param String [] ids of the items to be purchased
     * @return Boolean purchased, whether or not the transaction was succesful
     */
    @CrossOrigin()
    @PostMapping("/purchase")
    @ResponseBody
    Boolean purchase(@RequestParam(value = "ids") String[] ids, @RequestParam(value = "id") String uid, @RequestParam(value = "total") String total) {
        boolean purchased = dbPurchase(ids, uid, total);
        return purchased;
    }

    /**
     * Function that make HTTPRequest to the grocdb route: /purchase
     * @param String [] ids
     * @return boolean succesful purchase
     */
    private boolean dbPurchase(String[] ids, String id, String total) {
        // call grocdb
        String url = "https://grocdb.herokuapp.com/purchase";
        // Create the request body as a MultiValueMap
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        for (int i = 0; i < ids.length; i++) {
            body.add("ids", ids[i]);
        }
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, null);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Boolean> result = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<Boolean>() {
                });
        boolean first = result.getBody();

        // call userdb
        String url2 = "https://userdb438.herokuapp.com/purchase";
        MultiValueMap<String, String> body2 = new LinkedMultiValueMap<>();
        body2.add("id", id);
        body2.add("amount", total);
        HttpEntity<MultiValueMap<String, String>> httpEntity2 = new HttpEntity<>(body2, null);
        RestTemplate restTemplate2 = new RestTemplate();
        ResponseEntity<Boolean> result2 = restTemplate2.exchange(
                url2,
                HttpMethod.POST,
                httpEntity2,
                new ParameterizedTypeReference<Boolean>() {
                });
        boolean second = result2.getBody();
        return first && second;
    }
}
